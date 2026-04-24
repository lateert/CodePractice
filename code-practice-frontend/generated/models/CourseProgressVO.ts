/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

import type { CourseSummaryVO } from './CourseSummaryVO';
import type { QuestionProgressItemVO } from './QuestionProgressItemVO';
import type { QuestionSummaryVO } from './QuestionSummaryVO';
import type { StudentProgressVO } from './StudentProgressVO';

export type CourseProgressVO = {
    courseId?: number;
    courseTitle?: string;
    courseSummary?: CourseSummaryVO;
    courseQuestions?: Array<QuestionProgressItemVO>;
    questionSummaries?: Array<QuestionSummaryVO>;
    students?: Array<StudentProgressVO>;
};
