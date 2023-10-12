'use strict'

import http from '@/utils/http'

let fileService = {
    list: function (data, resolve, reject) {
        http({
            url: '/file/list',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    addDir: function (data, resolve, reject) {
        http({
            url: '/file/dir',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    update: function (data, resolve, reject) {
        http({
            url: '/file',
            data: data,
            method: 'put'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    delete: function (data, resolve, reject) {
        http({
            url: '/file',
            data: data,
            method: 'delete'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    getFolderTree: function (resolve, reject) {
        http({
            url: '/file/folder/tree',
            params: {},
            method: 'get'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    transfer: function (data, resolve, reject) {
        http({
            url: '/file/transfer',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    copy: function (data, resolve, reject) {
        http({
            url: '/file/copy',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    search: function (params, resolve, reject) {
        http({
            url: '/file/search',
            params: params,
            method: 'get'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    secUpload: function (data, resolve, reject) {
        http({
            url: '/file/sec-upload',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(err => reject(err))
    },
    merge: function (data, resolve, reject) {
        http({
            url: '/file/merge',
            data: data,
            method: 'post'
        }).then(res => resolve(res)).catch(err => reject(err))
    }
}

export default fileService
