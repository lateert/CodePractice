/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

import type { QuestionProgressItemVO } from './QuestionProgressItemVO';

export type StudentProgressVO = {
    userId?: number;
    userAccount?: string;
    userName?: string;
    submitTotal?: number;
    acceptedTotal?: number;
    successRate?: number;
    questionProgress?: Array<QuestionProgressItemVO>;
};
