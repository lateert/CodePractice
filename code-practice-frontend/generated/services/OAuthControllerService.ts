/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponseLoginUserVO } from '../models/BaseResponseLoginUserVO';
import type { BaseResponseString } from '../models/BaseResponseString';

import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';

export class OAuthControllerService {

    /**
     * @param state 
     * @returns BaseResponseString OK
     * @throws ApiError
     */
    public static githubLoginUrl(
state?: string,
): CancelablePromise<BaseResponseString> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/oauth/github/login-url',
            query: {
                'state': state,
            },
        });
    }

    /**
     * @param state 
     * @returns BaseResponseString OK
     * @throws ApiError
     */
    public static githubLoginUrl1(
state?: string,
): CancelablePromise<BaseResponseString> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/oauth/github/login-url',
            query: {
                'state': state,
            },
        });
    }

    /**
     * @param code 
     * @param state 
     * @returns BaseResponseLoginUserVO OK
     * @throws ApiError
     */
    public static githubCallback(
code: string,
state?: string,
): CancelablePromise<BaseResponseLoginUserVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/oauth/github/callback',
            query: {
                'code': code,
                'state': state,
            },
        });
    }

    /**
     * @param code 
     * @param state 
     * @returns BaseResponseLoginUserVO OK
     * @throws ApiError
     */
    public static githubCallback1(
code: string,
state?: string,
): CancelablePromise<BaseResponseLoginUserVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/oauth/github/callback',
            query: {
                'code': code,
                'state': state,
            },
        });
    }

}
