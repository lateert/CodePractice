/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponseBoolean } from '../models/BaseResponseBoolean';
import type { BaseResponseListCourse } from '../models/BaseResponseListCourse';
import type { BaseResponseListQuestionVO } from '../models/BaseResponseListQuestionVO';
import type { BaseResponseLong } from '../models/BaseResponseLong';
import type { BaseResponseMapStringListCourse } from '../models/BaseResponseMapStringListCourse';
import type { BaseResponsePageCourse } from '../models/BaseResponsePageCourse';
import type { CourseAddRequest } from '../models/CourseAddRequest';
import type { CourseEnrollRequest } from '../models/CourseEnrollRequest';
import type { CoursePublishRequest } from '../models/CoursePublishRequest';
import type { CourseQuestionBindRequest } from '../models/CourseQuestionBindRequest';
import type { CourseUpdateRequest } from '../models/CourseUpdateRequest';
import type { DeleteRequest } from '../models/DeleteRequest';

import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';

export class CourseControllerService {

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateCourse(
requestBody: CourseUpdateRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/course/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateCourse1(
requestBody: CourseUpdateRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/course/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static addQuestionToCourse(
requestBody: CourseQuestionBindRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/course/question/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static addQuestionToCourse1(
requestBody: CourseQuestionBindRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/course/question/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static publishCourse(
requestBody: CoursePublishRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/course/publish',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static publishCourse1(
requestBody: CoursePublishRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/course/publish',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static cancelEnrollment(
requestBody: CourseEnrollRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/course/enroll/cancel',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static cancelEnrollment1(
requestBody: CourseEnrollRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/course/enroll/cancel',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static enrollCourse(
requestBody: CourseEnrollRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/course/enroll',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static enrollCourse1(
requestBody: CourseEnrollRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/course/enroll',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static deleteCourse(
requestBody: DeleteRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/course/delete',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static deleteCourse1(
requestBody: DeleteRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/course/delete',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static addCourse(
requestBody: CourseAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/course/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static addCourse1(
requestBody: CourseAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/course/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param courseId 
     * @returns BaseResponseListQuestionVO OK
     * @throws ApiError
     */
    public static listCourseQuestions(
courseId: number,
): CancelablePromise<BaseResponseListQuestionVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/course/question/list',
            query: {
                'courseId': courseId,
            },
        });
    }

    /**
     * @param courseId 
     * @returns BaseResponseListQuestionVO OK
     * @throws ApiError
     */
    public static listCourseQuestions1(
courseId: number,
): CancelablePromise<BaseResponseListQuestionVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/course/question/list',
            query: {
                'courseId': courseId,
            },
        });
    }

    /**
     * @param questionIds 
     * @returns BaseResponseMapStringListCourse OK
     * @throws ApiError
     */
    public static getCoursesByQuestionIds(
questionIds: string,
): CancelablePromise<BaseResponseMapStringListCourse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/course/question/courses-by-questions',
            query: {
                'questionIds': questionIds,
            },
        });
    }

    /**
     * @param questionIds 
     * @returns BaseResponseMapStringListCourse OK
     * @throws ApiError
     */
    public static getCoursesByQuestionIds1(
questionIds: string,
): CancelablePromise<BaseResponseMapStringListCourse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/course/question/courses-by-questions',
            query: {
                'questionIds': questionIds,
            },
        });
    }

    /**
     * @param current 
     * @param pageSize 
     * @param keyword 
     * @param sortField 
     * @param sortOrder 
     * @returns BaseResponsePageCourse OK
     * @throws ApiError
     */
    public static listPublicCourseByPage(
current: number,
pageSize: number,
keyword?: string,
sortField?: string,
sortOrder?: string,
): CancelablePromise<BaseResponsePageCourse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/course/list/public',
            query: {
                'current': current,
                'pageSize': pageSize,
                'keyword': keyword,
                'sortField': sortField,
                'sortOrder': sortOrder,
            },
        });
    }

    /**
     * @param current 
     * @param pageSize 
     * @param keyword 
     * @param sortField 
     * @param sortOrder 
     * @returns BaseResponsePageCourse OK
     * @throws ApiError
     */
    public static listPublicCourseByPage1(
current: number,
pageSize: number,
keyword?: string,
sortField?: string,
sortOrder?: string,
): CancelablePromise<BaseResponsePageCourse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/course/list/public',
            query: {
                'current': current,
                'pageSize': pageSize,
                'keyword': keyword,
                'sortField': sortField,
                'sortOrder': sortOrder,
            },
        });
    }

    /**
     * @param current 
     * @param pageSize 
     * @param title 
     * @param isPublished 
     * @returns BaseResponsePageCourse OK
     * @throws ApiError
     */
    public static listCourseByPage(
current: number,
pageSize: number,
title?: string,
isPublished?: number,
): CancelablePromise<BaseResponsePageCourse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/course/list/page',
            query: {
                'current': current,
                'pageSize': pageSize,
                'title': title,
                'isPublished': isPublished,
            },
        });
    }

    /**
     * @param current 
     * @param pageSize 
     * @param title 
     * @param isPublished 
     * @returns BaseResponsePageCourse OK
     * @throws ApiError
     */
    public static listCourseByPage1(
current: number,
pageSize: number,
title?: string,
isPublished?: number,
): CancelablePromise<BaseResponsePageCourse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/course/list/page',
            query: {
                'current': current,
                'pageSize': pageSize,
                'title': title,
                'isPublished': isPublished,
            },
        });
    }

    /**
     * @returns BaseResponseListCourse OK
     * @throws ApiError
     */
    public static listMyCourses(): CancelablePromise<BaseResponseListCourse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/course/enroll/my',
        });
    }

    /**
     * @returns BaseResponseListCourse OK
     * @throws ApiError
     */
    public static listMyCourses1(): CancelablePromise<BaseResponseListCourse> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/course/enroll/my',
        });
    }

}
