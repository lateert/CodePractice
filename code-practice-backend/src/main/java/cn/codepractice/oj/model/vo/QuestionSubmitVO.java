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

    /**
     * id
     */
    private Long id;

    /**
     * 
     */
    private String language;

    /**
     * 
     */
    private String code;

    /**
     * （json ）
     */
    private JudgeInfo judgeInfo;

    /**
     * （0 - 、1 - 、2 - 、3 - ）
     */
    private Integer status;

    /**
     * 
     */
    private String statusStr;

    /**
     *  id
     */
    private Long questionId;

    /**
     *  id
     */
    private Long userId;

    /**
     * 
     */
    private String userName;

    /**
     * 
     */
    private UserVO user;

    /**
     * 
     */
    private QuestionVO questionVO;

    /**
     * Названия курсов, в которых встречается задача (для списка отправок)
     */
    private List<String> courseTitles;

    private Date createTime;

    private Date updateTime;


    private static final long serialVersionUID = 1L;


    /**
     * 
     *
     * @param questionSubmitVO
     * @return
     */
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

    /**
     * 
     *
     * @param questionSubmit
     * @return
     */
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
