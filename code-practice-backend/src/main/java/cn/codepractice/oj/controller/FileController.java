package cn.codepractice.oj.controller;

import cn.hutool.core.io.FileUtil;
import cn.codepractice.oj.common.BaseResponse;
import cn.codepractice.oj.common.ErrorCode;
import cn.codepractice.oj.common.ResultUtils;
import cn.codepractice.oj.config.AppFileProperties;
import cn.codepractice.oj.exception.BusinessException;
import cn.codepractice.oj.manager.S3StorageService;
import cn.codepractice.oj.model.dto.file.UploadFileRequest;
import cn.codepractice.oj.model.entity.User;
import cn.codepractice.oj.model.enums.FileUploadBizEnum;
import cn.codepractice.oj.service.UserService;

import java.io.File;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * 
 *
 */
@RestController
@RequestMapping({"/file", "/v1/file"})
@Slf4j
public class FileController {

    @Resource
    private UserService userService;

    @Resource
    private S3StorageService s3StorageService;

    @Resource
    private AppFileProperties appFileProperties;

    /**
     * 
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @param request
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                           UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String biz = uploadFileRequest.getBiz();
        FileUploadBizEnum fileUploadBizEnum = FileUploadBizEnum.getEnumByValue(biz);
        if (fileUploadBizEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        validFile(multipartFile, fileUploadBizEnum);
        User loginUser = userService.getLoginUser(request);
        // ：、（имя файла — uuid + расширение, без path traversal）
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String suffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        String ext = suffix == null ? "" : suffix.toLowerCase();
        String filename = ext.isEmpty() ? uuid : uuid + "." + ext;
        String filepath = String.format("/%s/%s/%s", fileUploadBizEnum.getValue(), loginUser.getId(), filename);
        File file = null;
        try {
            // 
            file = File.createTempFile("cp-upload-", ".tmp");
            multipartFile.transferTo(file);
            if (appFileProperties.isLocalStorageEnabled()) {
                Path base = Paths.get(appFileProperties.getLocalStorageRoot()).toAbsolutePath().normalize();
                Path dest = base.resolve(filepath.substring(1));
                Files.createDirectories(dest.getParent());
                Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
                String publicPath = "/file/local" + filepath;
                String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(publicPath)
                        .build()
                        .toUriString();
                return ResultUtils.success(url);
            }
            if (s3StorageService.isReady()) {
                String url = s3StorageService.upload(filepath, file, multipartFile.getContentType());
                return ResultUtils.success(url);
            }
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "Файловое хранилище не настроено: задайте app.file.local-storage-enabled=true "
                            + "или app.s3.enabled=true с параметрами бакета и ключей (S3-совместимый сервис, например MinIO).");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            Throwable root = ExceptionUtils.getRootCause(e);
            String msg = "Не удалось загрузить файл";
            if (root instanceof UnknownHostException) {
                msg = "Объектное хранилище (S3) недоступно по сети. Проверьте app.s3.endpoint или включите "
                        + "app.file.local-storage-enabled=true для локальной разработки.";
            }
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, msg);
        } finally {
            if (file != null) {
                // 
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 
     *
     * @param multipartFile
     * @param fileUploadBizEnum 
     */
    private void validFile(MultipartFile multipartFile, FileUploadBizEnum fileUploadBizEnum) {
        // 
        long fileSize = multipartFile.getSize();
        // 
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        String ext = fileSuffix == null ? "" : fileSuffix.toLowerCase();
        final long ONE_M = 1024 * 1024L;
        if (FileUploadBizEnum.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "Размер файла не больше 1 МБ");
            }
            if (ext.isEmpty() || !Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(ext)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,
                        "Допустимые форматы аватара: JPEG, JPG, PNG, WebP, SVG");
            }
        }
    }
}
