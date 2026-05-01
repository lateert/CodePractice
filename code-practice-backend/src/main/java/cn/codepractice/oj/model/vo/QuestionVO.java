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

    /** Идентификатор задачи. */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /** Заголовок задачи. */
    private String title;

    /** Условие задачи. */
    private String content;

    /** Список тегов задачи. */
    private List<String> tags;

    /** Количество отправок. */
    private Integer submitNum;

    /** Количество принятых решений. */
    private Integer acceptedNum;

    /** Ограничения проверки. */
    private JudgeConfig judgeConfig;

    /** Количество лайков. */
    private Integer thumbNum;

    /** Количество добавлений в избранное. */
    private Integer favourNum;

    /** Идентификатор автора задачи. */
    private Long userId;

    /** Данные автора задачи. */
    private UserVO user;


    private static final long serialVersionUID = 1L;


    /** Преобразует `QuestionVO` в сущность `Question`. */
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

    /** Преобразует сущность `Question` в `QuestionVO`. */
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
