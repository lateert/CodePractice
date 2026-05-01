package cn.codepractice.oj.model.vo;

import cn.hutool.json.JSONUtil;
import cn.codepractice.oj.judege.codesandbox.model.JudgeInfo;
import cn.codepractice.oj.model.entity.QuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class QuestionSubmitVO implements Serializable {

    /** Идентификатор отправки. */
    private Long id;

    /** Язык программирования отправки. */
    private String language;

    /** Исходный код пользователя. */
    private String code;

    /** Детали проверки. */
    private JudgeInfo judgeInfo;

    /** Статус проверки (коды из `QuestionSubmitStatusEnum`). */
    private Integer status;

    /** Текстовое представление статуса. */
    private String statusStr;

    /** Идентификатор задачи. */
    private Long questionId;

    /** Идентификатор пользователя. */
    private Long userId;

    /** Имя пользователя. */
    private String userName;

    /** Публичные данные пользователя. */
    private UserVO user;

    /** Данные задачи. */
    private QuestionVO questionVO;

    /**
     * Названия курсов, в которых встречается задача (для списка отправок)
     */
    private List<String> courseTitles;

    private Date createTime;

    private Date updateTime;


    private static final long serialVersionUID = 1L;


    /** Преобразует `QuestionSubmitVO` в сущность `QuestionSubmit`. */
    public static QuestionSubmit voToObj(QuestionSubmitVO questionSubmitVO) {
        if (questionSubmitVO == null) {
            return null;
        }
        QuestionSubmit question = new QuestionSubmit();
        BeanUtils.copyProperties(questionSubmitVO, question);
        JudgeInfo judgeInfoObj = questionSubmitVO.getJudgeInfo();
        if (judgeInfoObj != null) {
            question.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoObj));
        }
        return question;
    }

    /** Преобразует сущность `QuestionSubmit` в `QuestionSubmitVO`. */
    public static QuestionSubmitVO objToVo(QuestionSubmit questionSubmit) {
        if (questionSubmit == null) {
            return null;
        }
        QuestionSubmitVO questionSubmitVO = new QuestionSubmitVO();
        BeanUtils.copyProperties(questionSubmit, questionSubmitVO);
        String judgeInfoStr = questionSubmit.getJudgeInfo();
        questionSubmitVO.setJudgeInfo(JSONUtil.toBean(judgeInfoStr, JudgeInfo.class));

        return questionSubmitVO;
    }

}
