/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponseLong } from '../models/BaseResponseLong';
import type { BaseResponseObject } from '../models/BaseResponseObject';
import type { BaseResponsePageQuestionSolution } from '../models/BaseResponsePageQuestionSolution';
import type { BaseResponseQuestionVO } from '../models/BaseResponseQuestionVO';
import type { QuestionSolutionAddRequest } from '../models/QuestionSolutionAddRequest';
import type { QuestionSolutionQueryRequest } from '../models/QuestionSolutionQueryRequest';

import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';

export class QuestionSolutionControllerService {

    /**
     * @param requestBody 
     * @returns BaseResponsePageQuestionSolution OK
     * @throws ApiError
     */
    public static listQuestionVoByPage(
requestBody: QuestionSolutionQueryRequest,
): CancelablePromise<BaseResponsePageQuestionSolution> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/solution/list/page',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageQuestionSolution OK
     * @throws ApiError
     */
    public static listQuestionVoByPage1(
requestBody: QuestionSolutionQueryRequest,
): CancelablePromise<BaseResponsePageQuestionSolution> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/solution/list/page',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static addQuestion(
requestBody: QuestionSolutionAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/solution/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static addQuestion1(
requestBody: QuestionSolutionAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/solution/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param id 
     * @returns BaseResponseQuestionVO OK
     * @throws ApiError
     */
    public static getQuestionVoById(
id: number,
): CancelablePromise<BaseResponseQuestionVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/question/solution/get/vo',
            query: {
                'id': id,
            },
        });
    }

    /**
     * @param id 
     * @returns BaseResponseQuestionVO OK
     * @throws ApiError
     */
    public static getQuestionVoById1(
id: number,
): CancelablePromise<BaseResponseQuestionVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/question/solution/get/vo',
            query: {
                'id': id,
            },
        });
    }

    /**
     * @param id 
     * @returns BaseResponseObject OK
     * @throws ApiError
     */
    public static getQuestionById(
id: number,
): CancelablePromise<BaseResponseObject> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/question/solution/get',
            query: {
                'id': id,
            },
        });
    }

    /**
     * @param id 
     * @returns BaseResponseObject OK
     * @throws ApiError
     */
    public static getQuestionById1(
id: number,
): CancelablePromise<BaseResponseObject> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/question/solution/get',
            query: {
                'id': id,
            },
        });
    }

}
