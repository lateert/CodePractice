/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponseBoolean } from '../models/BaseResponseBoolean';
import type { BaseResponseLong } from '../models/BaseResponseLong';
import type { BaseResponseObject } from '../models/BaseResponseObject';
import type { BaseResponsePageQuestion } from '../models/BaseResponsePageQuestion';
import type { BaseResponsePageQuestionSubmitVO } from '../models/BaseResponsePageQuestionSubmitVO';
import type { BaseResponsePageQuestionVO } from '../models/BaseResponsePageQuestionVO';
import type { BaseResponseQuestionVO } from '../models/BaseResponseQuestionVO';
import type { BaseResponseString } from '../models/BaseResponseString';
import type { CodeTemplateQuery } from '../models/CodeTemplateQuery';
import type { DeleteRequest } from '../models/DeleteRequest';
import type { QuestionAddRequest } from '../models/QuestionAddRequest';
import type { QuestionEditRequest } from '../models/QuestionEditRequest';
import type { QuestionQueryRequest } from '../models/QuestionQueryRequest';
import type { QuestionSubmitAddRequest } from '../models/QuestionSubmitAddRequest';
import type { QuestionSubmitQueryRequest } from '../models/QuestionSubmitQueryRequest';
import type { QuestionUpdateRequest } from '../models/QuestionUpdateRequest';

import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';

export class QuestionControllerService {

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateQuestion(
requestBody: QuestionUpdateRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateQuestion1(
requestBody: QuestionUpdateRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageQuestionSubmitVO OK
     * @throws ApiError
     */
    public static listQuestionSubmitByPage2(
requestBody: QuestionSubmitQueryRequest,
): CancelablePromise<BaseResponsePageQuestionSubmitVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/submit/list/page',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageQuestionSubmitVO OK
     * @throws ApiError
     */
    public static listQuestionSubmitByPage3(
requestBody: QuestionSubmitQueryRequest,
): CancelablePromise<BaseResponsePageQuestionSubmitVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/submit/list/page',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static doQuestionSubmit2(
requestBody: QuestionSubmitAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/submit',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static doQuestionSubmit3(
requestBody: QuestionSubmitAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/submit',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageQuestionVO OK
     * @throws ApiError
     */
    public static listMyQuestionVoByPage(
requestBody: QuestionQueryRequest,
): CancelablePromise<BaseResponsePageQuestionVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/my/list/page/vo',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageQuestionVO OK
     * @throws ApiError
     */
    public static listMyQuestionVoByPage1(
requestBody: QuestionQueryRequest,
): CancelablePromise<BaseResponsePageQuestionVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/my/list/page/vo',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageQuestionVO OK
     * @throws ApiError
     */
    public static listQuestionVoByPage2(
requestBody: QuestionQueryRequest,
): CancelablePromise<BaseResponsePageQuestionVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/list/page/vo',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageQuestionVO OK
     * @throws ApiError
     */
    public static listQuestionVoByPage3(
requestBody: QuestionQueryRequest,
): CancelablePromise<BaseResponsePageQuestionVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/list/page/vo',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageQuestion OK
     * @throws ApiError
     */
    public static listQuestionByPage(
requestBody: QuestionQueryRequest,
): CancelablePromise<BaseResponsePageQuestion> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/list/page',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageQuestion OK
     * @throws ApiError
     */
    public static listQuestionByPage1(
requestBody: QuestionQueryRequest,
): CancelablePromise<BaseResponsePageQuestion> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/list/page',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static editQuestion(
requestBody: QuestionEditRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/edit',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static editQuestion1(
requestBody: QuestionEditRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/edit',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static deleteQuestion(
requestBody: DeleteRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/delete',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static deleteQuestion1(
requestBody: DeleteRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/delete',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseString OK
     * @throws ApiError
     */
    public static getCodeTemplate(
requestBody: CodeTemplateQuery,
): CancelablePromise<BaseResponseString> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/codeTemplate',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseString OK
     * @throws ApiError
     */
    public static getCodeTemplate1(
requestBody: CodeTemplateQuery,
): CancelablePromise<BaseResponseString> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/codeTemplate',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static addQuestion2(
requestBody: QuestionAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/question/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static addQuestion3(
requestBody: QuestionAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/question/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param id 
     * @returns BaseResponseObject OK
     * @throws ApiError
     */
    public static getQuestionByPath(
id: string | number,
): CancelablePromise<BaseResponseObject> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/question/{id}',
            path: {
                'id': id,
            },
        });
    }

    /**
     * @param id 
     * @returns BaseResponseObject OK
     * @throws ApiError
     */
    public static getQuestionByPath1(
id: string | number,
): CancelablePromise<BaseResponseObject> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/question/{id}',
            path: {
                'id': id,
            },
        });
    }

    /**
     * @param id 
     * @returns BaseResponseQuestionVO OK
     * @throws ApiError
     */
    public static getQuestionVoById2(
id: string | number,
): CancelablePromise<BaseResponseQuestionVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/question/get/vo',
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
    public static getQuestionVoById3(
id: string | number,
): CancelablePromise<BaseResponseQuestionVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/question/get/vo',
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
    public static getQuestionById2(
id: string | number,
): CancelablePromise<BaseResponseObject> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/question/get',
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
    public static getQuestionById3(
id: string | number,
): CancelablePromise<BaseResponseObject> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/question/get',
            query: {
                'id': id,
            },
        });
    }

}
