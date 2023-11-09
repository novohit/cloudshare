'use strict'

import http from '@/utils/http'
import simpleHttp from '@/utils/simple-http'

let userService = {
    login: function (data, resolve, reject) {
        http({
            url: '/user/login',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    register: function (data, resolve, reject) {
        http({
            url: '/user/register',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    info: function (resolve, reject) {
        http({
            url: '/user/',
            params: {},
            method: 'get'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    checkUsername: function (data, resolve, reject) {
        http({
            url: '/user/check-username',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    checkAnswer: function (data, resolve, reject) {
        http({
            url: '/user/answer/check',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    resetPassword: function (data, resolve, reject) {
        http({
            url: '/user/password/reset',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    changePassword: function (data, resolve, reject) {
        http({
            url: '/user/password/change',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    logout: function (resolve, reject) {
        http({
            url: '/user/logout',
            method: 'post'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    searchHistories: function (resolve, reject) {
        http({
            url: '/search/history',
            params: { size: 5},
            method: 'get'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    checkUserLoginStatus: function (params, resolve, reject) {
        http({
            url: '/user/login/status',
            params: params,
            method: 'get'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    infoWithoutPageJump(resolve, reject) {
        http({
            url: '/user',
            params: {},
            method: 'get'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    pay:function(data,resolve, reject) {
        http({
            url: '/order/place',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(res => reject(res))
    },
    list: function (resolve, reject) {
        http({
            url: '/product/list',
            params: {},
            method: 'get'
        }).then(res => resolve(res)).catch(res => reject(res))
    }
}

export default userService
