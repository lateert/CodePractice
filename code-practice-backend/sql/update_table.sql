INSERT INTO code_practice.question (id, title, content, tags, answer, submit_num, accepted_num, judge_case, judge_config, thumb_num, favour_num, user_id, create_time, update_time, is_delete) VALUES (1740729755944218626, 'Максимальная сумма подмассива', '## Описание задачи

Дан массив целых чисел `nums`. Необходимо найти непустой непрерывный подмассив с максимальной суммой элементов.

Реализуйте функцию `maxSubArray`, которая возвращает максимальную сумму.

### Примеры

**Пример 1:**
```markdown
Ввод: nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
Вывод: 6
Пояснение: подмассив [4,-1,2,1] имеет максимальную сумму 6.
```

**Пример 2:**
```markdown
Ввод: nums = [1]
Вывод: 1
Пояснение: единственный подмассив имеет сумму 1.
```

**Пример 3:**
```markdown
Ввод: nums = [5, 4, -1, 7, 8]
Вывод: 23
Пояснение: подмассив [5,4,-1,7,8] имеет максимальную сумму 23.
```

### Ограничения

- `1 <= nums.length <= 10^5`
- `-10^4 <= nums[i] <= 10^4`', '["динамическое программирование","сложная"]', '**answers**', 0, 0, '[{"input":"-2, 1, -3, 4, -1, 2, 1, -5, 4","output":"6"},{"input":"1","output":"1"},{"input":"5, 4, -1, 7, 8","output":"23"}]', '{"timeLimit":1000,"memoryLimit":1000,"stackLimit":1000}', 0, 0, 1740718129912344578, '2023-12-29 13:41:29', '2024-01-07 09:04:40', 0);
INSERT INTO code_practice.question (id, title, content, tags, answer, submit_num, accepted_num, judge_case, judge_config, thumb_num, favour_num, user_id, create_time, update_time, is_delete) VALUES (1743929591988277250, 'A+B', '## Описание задачи

Даны два целых числа A и B. Необходимо вычислить их сумму.

### Формат ввода

Считать два целых числа A и B из стандартного ввода.

### Формат вывода

Вывести их сумму в стандартный вывод.

### Пример

#### Ввод
3 5

#### Вывод
8


### Ограничения

- `-10^9 <= A, B <= 10^9`

---
', '["простая"]', 'Ответ', 0, 0, '[{"input":"3 5","output":"8"},{"input":"1 2","output":"3"},{"input":"1 4","output":"5"}]', '{"timeLimit":1000,"memoryLimit":1000,"stackLimit":1000}', 0, 0, 1740718129912344578, '2024-01-07 09:36:30', '2024-01-07 09:36:43', 0);



INSERT INTO code_practice.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES (1744282346928005122, '# Разбор: максимальная сумма подмассива

## Описание задачи

Дан массив `nums`. Нужно найти непустой непрерывный подмассив с максимальной суммой.

## Идея решения

Это классическая задача на динамическое программирование. Используем `currentSum` и `maxSum`.

1. Инициализируем `maxSum` и `currentSum` значением `nums[0]`.
2. Для каждого следующего элемента:
   - `currentSum = Math.max(nums[i], currentSum + nums[i])`
   - `maxSum = Math.max(maxSum, currentSum)`
3. После прохода `maxSum` — итоговый ответ.

## Реализация на Java

```java
public class MaxSubArray {

    public static int maxSubArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }

        int maxSum = nums[0];
        int currentSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    public static void main(String[] args) {
        int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println(maxSubArray(nums));
    }
}
```
## Анализ сложности
- Время: O(n), где n — длина массива.
- Память: O(1).', 1740729755944218626, 1740718129912344578, '2024-01-08 16:58:13', 0, 'Официальный разбор', '["c++", "c", "go", "динамическое программирование"]');
INSERT INTO code_practice.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES (1744535205644922881, 'Тестовый разбор', 1740729755944218626, 1740718129912344578, '2024-01-09 09:42:59', 0, 'Максимальная сумма подмассива', '["Java", "c++", "c", "go", "динамическое программирование"]');
INSERT INTO code_practice.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES (1744543128358023169, '**Тестовая заметка**', 1740729755944218626, 1740718129912344578, '2024-01-09 10:14:28', 0, 'Тест', '["Java", "c++", "c", "go", "динамическое программирование"]');
INSERT INTO code_practice.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES (1744543920028774402, '> Демо-комментарий', 1740729755944218626, 1740718129912344578, '2024-01-09 10:17:37', 0, 'Тест', '["Java", "c++", "c", "go", "динамическое программирование"]');
INSERT INTO code_practice.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES (1744545724640555009, '`Демо`', 1740729755944218626, 1740718129912344578, '2024-01-09 10:24:47', 0, 'Теги', '["Java", "c++", "c", "go", "динамическое программирование"]');
INSERT INTO code_practice.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES (1744575216570363906, '2323', 1740729755944218626, 1740718129912344578, '2024-01-09 12:21:58', 0, '12312', '["121"]');
INSERT INTO code_practice.question_solution (solution_id, solution, question_id, user_id, create_time, is_delete, title, tags) VALUES (1746158467263078402, '# Разбор: задача A + B

## Описание задачи

Даны два целых числа A и B. Нужно вычислить их сумму.

## Идея решения

Это базовая задача: результат вычисляется оператором сложения.

## Реализация на Java

```java
public class SumAB {
    public static void main(String[] args) {
        // Два целых числа A и B
        int A = 3;
        int B = 5;

        // Вычисляем сумму
        int sum = A + B;

        // Выводим результат
        System.out.println("Сумма равна: " + sum);
    }
}
```
## Анализ сложности
- Время: O(1), выполняется одна операция сложения.
- Память: O(1), используется константный объем памяти.', 1743929591988277250, 1740718129912344578, null, 0, 'Официальный разбор', '["a+b", "java"]');



INSERT INTO code_practice.question_submit (id, language, code, judge_info, status, question_id, user_id, create_time, update_time, is_delete) VALUES (1743933547359576065, 'java', 'public class Main {
    public static void main(String[] args) {
        int num1 = Integer.parseInt(args[0]);
        int num2 = Integer.parseInt(args[1]);

        int sum = num1 + num2;

        System.out.println(sum);
    }
}
', '{"message":"Accepted","memory":18022400,"time":436}', 2, 1743929591988277250, 1740718129912344578, '2024-01-07 09:52:13', '2024-01-07 09:52:25', 0);
INSERT INTO code_practice.question_submit (id, language, code, judge_info, status, question_id, user_id, create_time, update_time, is_delete) VALUES (1743991683059621889, 'java', 'public class Main {
    public static void main(String[] args) {
        System.out.println("3");
    }
}
', '{"message":"wrong Answer","memory":1822720,"time":380}', 3, 1743929591988277250, 1740718129912344578, '2024-01-07 13:43:13', '2024-01-07 13:49:32', 0);
INSERT INTO code_practice.question_submit (id, language, code, judge_info, status, question_id, user_id, create_time, update_time, is_delete) VALUES (1743994886425751554, 'java', 'public class Main {
    public static void main(String[] args) {
        System.out.println("3");
    }
}
', '{"message":"wrong Answer","memory":1814528,"time":332,"status":3}', 2, 1743929591988277250, 1740718129912344578, '2024-01-07 13:55:57', '2024-01-07 13:56:04', 0);
INSERT INTO code_practice.question_submit (id, language, code, judge_info, status, question_id, user_id, create_time, update_time, is_delete) VALUES (1745087714325168129, 'java', 'public class Main {
    public static void main(String[] args) {
        int num1 = Integer.parseInt(args[0]);
        int num2 = Integer.parseInt(args[1]);

        int sum = num1 + num2;

        System.out.println(sum);
    }
}
', '{"message":"Accepted","memory":37302272,"time":458,"status":2}', 2, 1743929591988277250, 1740718129912344578, '2024-01-10 14:18:27', '2024-01-10 14:18:34', 0);
INSERT INTO code_practice.question_submit (id, language, code, judge_info, status, question_id, user_id, create_time, update_time, is_delete) VALUES (1746163490688704514, 'java', 'public class Main {
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);

        System.out.println(a + b)
    }
}
', '{}', 1, 1743929591988277250, 1740718129912344578, '2024-01-13 13:33:12', '2024-01-13 13:33:13', 0);
INSERT INTO code_practice.question_submit (id, language, code, judge_info, status, question_id, user_id, create_time, update_time, is_delete) VALUES (1746163720939216898, 'java', 'public class Main {
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);

        System.out.println(a + b)
    }
}
', '{}', 1, 1743929591988277250, 1740718129912344578, '2024-01-13 13:34:07', '2024-01-13 13:34:07', 0);
INSERT INTO code_practice.question_submit (id, language, code, judge_info, status, question_id, user_id, create_time, update_time, is_delete) VALUES (1746163825306083330, 'java', 'public class Main {
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);

        System.out.println(a + b)
    }
}
', '{}', 1, 1743929591988277250, 1740718129912344578, '2024-01-13 13:34:32', '2024-01-13 13:34:32', 0);
INSERT INTO code_practice.question_submit (id, language, code, judge_info, status, question_id, user_id, create_time, update_time, is_delete) VALUES (1746163896433090562, 'java', 'public class Main {
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);

        System.out.println(a + b);
    }
}
', '{"message":"Accepted","memory":37339136,"time":496,"status":2}', 2, 1743929591988277250, 1740718129912344578, '2024-01-13 13:34:49', '2024-01-13 13:35:03', 0);
INSERT INTO code_practice.question_submit (id, language, code, judge_info, status, question_id, user_id, create_time, update_time, is_delete) VALUES (1746164584873566209, 'java', 'public class Main {
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);

        System.out.println(a + b)
    }
}
', '{}', 1, 1743929591988277250, 1740718129912344578, '2024-01-13 13:37:33', '2024-01-13 13:37:33', 0);
INSERT INTO code_practice.question_submit (id, language, code, judge_info, status, question_id, user_id, create_time, update_time, is_delete) VALUES (1746164636006326274, 'java', 'public class Main {
    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);

        System.out.println(a + b);
    }
}
', '{"message":"Accepted","memory":6082560,"time":306,"status":2}', 2, 1743929591988277250, 1740718129912344578, '2024-01-13 13:37:46', '2024-01-13 13:37:57', 0);



INSERT INTO code_practice.user (id, user_account, user_password, union_id, mp_open_id, user_name, user_avatar, user_profile, user_role, create_time, update_time, is_delete) VALUES (1740718129912344578, 'codepractice', 'd1d25478b110153be95c8527eccec2da', null, null, 'codepractice', '//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/9eeb1800d9b78349b24682c3518ac4a3.png~tplv-uwbnlip3yd-webp.webp', null, 'admin', '2024-02-10 08:26:10', '2024-02-10 08:37:37', 0);
INSERT INTO code_practice.user (id, user_account, user_password, union_id, mp_open_id, user_name, user_avatar, user_profile, user_role, create_time, update_time, is_delete) VALUES (1743894277299142658, 'zhangfan', 'd4e810fa5a6c050aa31c658a7b7daa46', null, null, 'Иван', '//p1-arco.byteimg.com/tos-cn-i-uwbnlip3yd/9eeb1800d9b78349b24682c3518ac4a3.png~tplv-uwbnlip3yd-webp.webp', null, 'user', '2024-01-07 07:16:10', '2024-01-10 13:59:44', 0);

INSERT INTO code_practice.user (id, user_account, user_password, union_id, mp_open_id, user_name, user_avatar, user_profile, user_role, create_time, update_time, is_delete)
SELECT 2000000000000000001, 'admin1', '1e0932fa9472a0ce996df8bedbdf1e55', null, null, 'Администратор 1', null, null, 'admin', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM code_practice.user WHERE user_account = 'admin1');

INSERT INTO code_practice.user (id, user_account, user_password, union_id, mp_open_id, user_name, user_avatar, user_profile, user_role, create_time, update_time, is_delete)
SELECT 2000000000000000002, 'user1', 'a1b5bf79a771be374c0e7a9703ba9768', null, null, 'Студент 1', null, null, 'user', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM code_practice.user WHERE user_account = 'user1');

INSERT INTO code_practice.user (id, user_account, user_password, union_id, mp_open_id, user_name, user_avatar, user_profile, user_role, create_time, update_time, is_delete)
SELECT 2000000000000000003, 'teacher1', 'fd19951444f3c6fedea546a11d0701b7', null, null, 'Преподаватель 1', null, null, 'teacher', NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM code_practice.user WHERE user_account = 'teacher1');

ALTER TABLE code_practice.course
    ADD COLUMN IF NOT EXISTS is_published TINYINT NOT NULL DEFAULT 0 COMMENT 'Публикация: 0 - черновик, 1 - опубликован';
