package cn.codepractice.oj.model.dto.question;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class QuestionAddRequest implements Serializable {

    /** Заголовок задачи. */
    private String title;

    /** Условие задачи. */
    private String content;

    /** Список тегов. */
    private List<String> tags;

    /** Эталонный ответ/пояснение. */
    private String answer;


    /** Набор тест-кейсов проверки. */
    private List<JudgeCase> judgeCase;

    /** Ограничения проверки. */
    private JudgeConfig judgeConfig;



    private static final long serialVersionUID = 1L;
}