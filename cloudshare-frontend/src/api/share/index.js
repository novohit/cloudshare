'use strict'

import http from '@/utils/http'
import simpleHttp from '@/utils/simple-http'

let shareService = {
    getShareDetail: function (params, resolve, reject) {
        http({
            url: '/share/access',
            params: params,
            method: 'get'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    createShare: function (data, resolve, reject) {
        http({
            url: '/share',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    cancelShare: function (data, resolve, reject) {
        http({
            url: '/share',
            data: data,
            method: 'delete'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    checkShareCode: function (data, resolve, reject) {
        http({
            url: '/share/check-code',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    getShareFiles: function (params, resolve) {
        simpleHttp({
            url: '/share/file/list',
            params: params,
            method: 'get'
        }).then(res => resolve(res))
    },
    saveShareFiles: function (data, resolve) {
        simpleHttp({
            url: '/share/save',
            data: data,
            method: 'post'
        }).then(res => resolve(res))
    },
    getShareList: function (resolve, reject) {
        http({
            url: '/share/list',
            params: {},
            method: 'get'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    getSharer: function (params, resolve) {
        simpleHttp({
            url: '/share/sharer',
            params: params,
            method: 'get'
        }).then(res => resolve(res))
    }
}

export default shareService
