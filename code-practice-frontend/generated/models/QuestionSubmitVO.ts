/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

import type { JudgeInfo } from './JudgeInfo';
import type { QuestionVO } from './QuestionVO';
import type { UserVO } from './UserVO';

export type QuestionSubmitVO = {
    id?: number;
    language?: string;
    code?: string;
    judgeInfo?: JudgeInfo;
    status?: number;
    statusStr?: string;
    questionId?: number;
    userId?: number;
    userName?: string;
    user?: UserVO;
    questionVO?: QuestionVO;
    courseTitles?: Array<string>;
    createTime?: string;
    updateTime?: string;
};
