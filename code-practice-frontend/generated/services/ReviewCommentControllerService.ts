/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponseBoolean } from '../models/BaseResponseBoolean';
import type { BaseResponseListReviewComment } from '../models/BaseResponseListReviewComment';
import type { BaseResponseLong } from '../models/BaseResponseLong';
import type { ReviewCommentAddRequest } from '../models/ReviewCommentAddRequest';
import type { ReviewCommentUpdateStatusRequest } from '../models/ReviewCommentUpdateStatusRequest';

import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';

export class ReviewCommentControllerService {

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateStatus(
requestBody: ReviewCommentUpdateStatusRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/review/comment/status',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateStatus1(
requestBody: ReviewCommentUpdateStatusRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/review/comment/status',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static addReviewComment(
requestBody: ReviewCommentAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/review/comment/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static addReviewComment1(
requestBody: ReviewCommentAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/review/comment/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param submissionId 
     * @returns BaseResponseListReviewComment OK
     * @throws ApiError
     */
    public static listBySubmission(
submissionId: number,
): CancelablePromise<BaseResponseListReviewComment> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/review/comment/list',
            query: {
                'submissionId': submissionId,
            },
        });
    }

    /**
     * @param submissionId 
     * @returns BaseResponseListReviewComment OK
     * @throws ApiError
     */
    public static listBySubmission1(
submissionId: number,
): CancelablePromise<BaseResponseListReviewComment> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/review/comment/list',
            query: {
                'submissionId': submissionId,
            },
        });
    }

}
