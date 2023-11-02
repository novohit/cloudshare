'use strict'

import axios from 'axios'
import {getShareToken, getToken} from '../cookie'
import {ElMessage} from 'element-plus'
import panUtil from '@/utils/common'

const httpSimple = axios.create({
    baseURL: panUtil.getUrlPrefix(),
    timeout: 1000 * 30,
    headers: {
        'Content-Type': 'application/json;charset=utf-8'
    }
})

httpSimple.interceptors.request.use(config => {
    if (config.data) {
        config.data = JSON.stringify(config.data)
    }
    let token = getToken(),
        shareToken = getShareToken()
    if (token) {
        config.headers['Authorization'] = token
    }
    if (shareToken) {
        config.headers['Share-Token'] = shareToken
    }
    return config
}, error => {
    ElMessage.error(error.response.data.message)
    return Promise.reject(error)
})

httpSimple.interceptors.response.use(res => {
    return res.data
}, error => {
    ElMessage.error(error.response.data.message)
    return Promise.reject(error)
})

export default httpSimple
