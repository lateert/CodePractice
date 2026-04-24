/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponseBoolean } from '../models/BaseResponseBoolean';
import type { BaseResponseLoginUserVO } from '../models/BaseResponseLoginUserVO';
import type { BaseResponseLong } from '../models/BaseResponseLong';
import type { BaseResponsePageUser } from '../models/BaseResponsePageUser';
import type { BaseResponsePageUserVO } from '../models/BaseResponsePageUserVO';
import type { BaseResponseUser } from '../models/BaseResponseUser';
import type { BaseResponseUserVO } from '../models/BaseResponseUserVO';
import type { DeleteRequest } from '../models/DeleteRequest';
import type { UserAddRequest } from '../models/UserAddRequest';
import type { UserLoginRequest } from '../models/UserLoginRequest';
import type { UserQueryRequest } from '../models/UserQueryRequest';
import type { UserRefreshRequest } from '../models/UserRefreshRequest';
import type { UserRegisterRequest } from '../models/UserRegisterRequest';
import type { UserUpdateMyRequest } from '../models/UserUpdateMyRequest';
import type { UserUpdatePasswordRequest } from '../models/UserUpdatePasswordRequest';
import type { UserUpdateRequest } from '../models/UserUpdateRequest';

import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';

export class UserControllerService {

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateMyPassword(
requestBody: UserUpdatePasswordRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/update/my/password',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateMyPassword1(
requestBody: UserUpdatePasswordRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/user/update/my/password',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateMyUser(
requestBody: UserUpdateMyRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/update/my',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateMyUser1(
requestBody: UserUpdateMyRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/user/update/my',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateUser(
requestBody: UserUpdateRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static updateUser1(
requestBody: UserUpdateRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/user/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static userRegister(
requestBody: UserRegisterRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/register',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static userRegister1(
requestBody: UserRegisterRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/user/register',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLoginUserVO OK
     * @throws ApiError
     */
    public static refresh(
requestBody?: UserRefreshRequest,
): CancelablePromise<BaseResponseLoginUserVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/refresh',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLoginUserVO OK
     * @throws ApiError
     */
    public static refresh1(
requestBody?: UserRefreshRequest,
): CancelablePromise<BaseResponseLoginUserVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/user/refresh',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static userLogout(): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/logout',
        });
    }

    /**
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static userLogout1(): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/user/logout',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLoginUserVO OK
     * @throws ApiError
     */
    public static userLogin(
requestBody: UserLoginRequest,
): CancelablePromise<BaseResponseLoginUserVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/login',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLoginUserVO OK
     * @throws ApiError
     */
    public static userLogin1(
requestBody: UserLoginRequest,
): CancelablePromise<BaseResponseLoginUserVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/user/login',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageUserVO OK
     * @throws ApiError
     */
    public static listUserVoByPage(
requestBody: UserQueryRequest,
): CancelablePromise<BaseResponsePageUserVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/list/page/vo',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageUserVO OK
     * @throws ApiError
     */
    public static listUserVoByPage1(
requestBody: UserQueryRequest,
): CancelablePromise<BaseResponsePageUserVO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/user/list/page/vo',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageUser OK
     * @throws ApiError
     */
    public static listUserByPage(
requestBody: UserQueryRequest,
): CancelablePromise<BaseResponsePageUser> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/list/page',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponsePageUser OK
     * @throws ApiError
     */
    public static listUserByPage1(
requestBody: UserQueryRequest,
): CancelablePromise<BaseResponsePageUser> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/user/list/page',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static deleteUser(
requestBody: DeleteRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/delete',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseBoolean OK
     * @throws ApiError
     */
    public static deleteUser1(
requestBody: DeleteRequest,
): CancelablePromise<BaseResponseBoolean> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/user/delete',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static addUser(
requestBody: UserAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param requestBody 
     * @returns BaseResponseLong OK
     * @throws ApiError
     */
    public static addUser1(
requestBody: UserAddRequest,
): CancelablePromise<BaseResponseLong> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/v1/user/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }

    /**
     * @param id 
     * @returns BaseResponseUserVO OK
     * @throws ApiError
     */
    public static getUserVoById(
id: number,
): CancelablePromise<BaseResponseUserVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/get/vo',
            query: {
                'id': id,
            },
        });
    }

    /**
     * @param id 
     * @returns BaseResponseUserVO OK
     * @throws ApiError
     */
    public static getUserVoById1(
id: number,
): CancelablePromise<BaseResponseUserVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/user/get/vo',
            query: {
                'id': id,
            },
        });
    }

    /**
     * @returns BaseResponseLoginUserVO OK
     * @throws ApiError
     */
    public static getLoginUser(): CancelablePromise<BaseResponseLoginUserVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/get/login',
        });
    }

    /**
     * @returns BaseResponseLoginUserVO OK
     * @throws ApiError
     */
    public static getLoginUser1(): CancelablePromise<BaseResponseLoginUserVO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/user/get/login',
        });
    }

    /**
     * @param id 
     * @returns BaseResponseUser OK
     * @throws ApiError
     */
    public static getUserById(
id: number,
): CancelablePromise<BaseResponseUser> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/get',
            query: {
                'id': id,
            },
        });
    }

    /**
     * @param id 
     * @returns BaseResponseUser OK
     * @throws ApiError
     */
    public static getUserById1(
id: number,
): CancelablePromise<BaseResponseUser> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/v1/user/get',
            query: {
                'id': id,
            },
        });
    }

}
