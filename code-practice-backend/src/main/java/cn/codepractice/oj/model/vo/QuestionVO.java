package cn.codepractice.oj.model.vo;

import cn.hutool.json.JSONUtil;
import cn.codepractice.oj.model.dto.question.CodeTemplateQuery;
import cn.codepractice.oj.model.dto.question.JudgeConfig;
import cn.codepractice.oj.model.entity.Question;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

@Data
public class QuestionVO implements Serializable {

    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 
     */
    private String title;

    /**
     * 
     */
    private String content;

    /**
     * （json ）
     */
    private List<String> tags;

    /**
     * 
     */
    private Integer submitNum;

    /**
     * 
     */
    private Integer acceptedNum;

    /**
     * （json ）
     */
    private JudgeConfig judgeConfig;

    /**
     * 
     */
    private Integer thumbNum;

    /**
     * 
     */
    private Integer favourNum;

    /**
     *  id
     */
    private Long userId;

    /**
     * 
     */
    private UserVO user;


    private static final long serialVersionUID = 1L;


    /**
     * 
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }

        JudgeConfig judgeConfigVO = questionVO.getJudgeConfig();
        if (judgeConfigVO != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfigVO));
        }
        return question;
    }

    /**
     * 
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setTags(JSONUtil.toList(question.getTags(), String.class));
        questionVO.setJudgeConfig(JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class));
        return questionVO;
    }

}
