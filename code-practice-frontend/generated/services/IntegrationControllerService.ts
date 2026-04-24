/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponseString } from '../models/BaseResponseString';

import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';

export class IntegrationControllerService {

    /**
     * @param q 
     * @param limit 
     * @returns BaseResponseString OK
     * @throws ApiError
     */
    public static searchGithubRepositories(
q?: string,
limit: number = 5,
): CancelablePromise<BaseResponseString> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/integration/github/repositories/search',
            query: {
                'q': q,
                'limit': limit,
            },
        });
    }

    /**
     * @param q 
     * @param limit 
     * @returns BaseResponseString OK
     * @throws ApiError
     */
    public static searchGithubRepositories1(
q?: string,
limit: number = 5,
): CancelablePromise<BaseResponseString> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/integration/github/repositories/search',
            query: {
                'q': q,
                'limit': limit,
            },
        });
    }

}
