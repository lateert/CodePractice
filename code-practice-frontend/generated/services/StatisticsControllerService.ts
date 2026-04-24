/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponseCourseProgressVO } from '../models/BaseResponseCourseProgressVO';
import type { BaseResponseMapStringLong } from '../models/BaseResponseMapStringLong';

import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';

export class StatisticsControllerService {

    /**
     * @returns BaseResponseMapStringLong OK
     * @throws ApiError
     */
    public static getSummary(): CancelablePromise<BaseResponseMapStringLong> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/statistics/summary',
        });
    }

    /**
     * @returns BaseResponseMapStringLong OK
     * @throws ApiError
     */
    public static getSummary1(): CancelablePromise<BaseResponseMapStringLong> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/statistics/summary',
        });
    }

    /**
     * @param courseId 
     * @returns BaseResponseCourseProgressVO OK
     * @throws ApiError
     */
    public static getCourseProgress(
courseId: number,
): CancelablePromise<BaseResponseCourseProgressVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/statistics/progress/course/{courseId}',
            path: {
                'courseId': courseId,
            },
        });
    }

    /**
     * @param courseId 
     * @returns BaseResponseCourseProgressVO OK
     * @throws ApiError
     */
    public static getCourseProgress1(
courseId: number,
): CancelablePromise<BaseResponseCourseProgressVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/statistics/progress/course/{courseId}',
            path: {
                'courseId': courseId,
            },
        });
    }

}
