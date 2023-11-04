'use strict'

import axios from 'axios'
import {clearToken, getToken, getShareToken} from '@/utils/cookie'
import {ElMessage, ElMessageBox} from 'element-plus'
import panUtil from '@/utils/common'
import {useBreadcrumbStore} from '@/stores/breadcrumb'
import {useFileStore} from '@/stores/file'
import {useNavbarStore} from '@/stores/navbar'
import {useUserStore} from '@/stores/user'

const toLogin = function () {
        const breadcrumbStore = useBreadcrumbStore()
        const fileStore = useFileStore()
        const navbarStore = useNavbarStore()
        const userStore = useUserStore()
        // to re-login
        ElMessageBox.confirm('您需要重新登陆', '确认退出登录', {
            confirmButtonText: '重新登陆',
            cancelButtonText: '取消',
            type: 'warning'
        }).then(() => {
            clearToken()
            fileStore.clear()
            breadcrumbStore.clear()
            navbarStore.clear()
            userStore.clear()
            window.location.reload()
        })
    },
    http = axios.create({
        baseURL: panUtil.getUrlPrefix(),
        timeout: 1000 * 60,
        headers: {
            'Content-Type': 'application/json;charset=utf-8'
        }
    })

http.interceptors.request.use(config => {
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

http.interceptors.response.use(res => {
    if (res.data.code === 10) {
        toLogin()
        res.data.message = '您需要重新登陆'
        return Promise.reject(res.data)
    }
    if (res.data.code !== 0) {
        return Promise.reject(res.data)
    }
    return res.data
}, error => {
    if (error.response.data.message === 'Share-Token不存在') {

    } else {
        ElMessage.error(error.response.data.message)
    }
    return Promise.reject(error)
})

export default http
