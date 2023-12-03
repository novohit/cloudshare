'use strict';

import {getToken} from '@/utils/cookie'

let panUtil = {
    fileStatus: {
        PARSING: {
            code: 1,
            text: '解析中'
        },
        WAITING: {
            code: 2,
            text: '等待上传'
        },
        UPLOADING: {
            code: 3,
            text: '正在上传'
        },
        PAUSE: {
            code: 4,
            text: '暂停上传'
        },
        SUCCESS: {
            code: 5,
            text: '上传成功'
        },
        FAIL: {
            code: 6,
            text: '上传失败'
        },
        MERGE: {
            code: 7,
            text: '服务器处理中'
        }
    },
    translateFileSize(fileSize) {
        let KB_STR = 'K',
            MB_STR = 'M',
            GB_STR = 'G',
            UNIT = 1024,
            fileSizeSuffix = KB_STR
        fileSize = fileSize / UNIT;
        if (fileSize >= UNIT) {
            fileSize = fileSize / UNIT;
            fileSizeSuffix = MB_STR;
        }
        if (fileSize >= UNIT) {
            fileSize = fileSize / UNIT;
            fileSizeSuffix = GB_STR;
        }
        return fileSize.toFixed(2) + fileSizeSuffix;
    },
    translateSpeed(byteSpeed) {
        return this.translateFileSize(byteSpeed) + '/s'
    },
    translateTime(timeRemaining) {
        if (!timeRemaining || Number.POSITIVE_INFINITY === timeRemaining) {
            return '--:--:--'
        }
        let timeRemainingInt = parseInt(timeRemaining),
            hNum = Math.floor(timeRemainingInt / 3600),
            mNum = Math.floor((timeRemainingInt / 60 % 60)),
            sNum = Math.floor((timeRemainingInt % 60)),
            h = hNum < 10 ? '0' + hNum : hNum,
            m = mNum < 10 ? '0' + mNum : mNum,
            s = sNum < 10 ? '0' + sNum : sNum
        return h + ':' + m + ':' + s
    },
    checkUsername(username) {
        return !!username && /^[0-9A-Za-z]{6,16}$/.test(username)
    },
    checkPassword(password) {
        return !!password && password.length >= 8 && password.length <= 16
    },
    showOperation(dom) {
        let parentDiv = dom.firstElementChild
        if (parentDiv && parentDiv.classList.contains('el-tooltip')) {
            let div = parentDiv.lastElementChild
            div.style.display = 'inline-block'
        }
    },
    hiddenOperation(dom) {
        let parentDiv = dom.firstElementChild
        if (parentDiv && parentDiv.classList.contains('el-tooltip')) {
            let div = parentDiv.lastElementChild
            div.style.display = 'none'
        }
    },
    getFileFontElement(type) {
        let tagStr = 'fa fa-file'
        switch (type) {
            case 'DIR':
                tagStr = 'fa fa-folder-o'
                break
            case 'ARCHIVE':
                tagStr = 'fa fa-file-archive-o'
                break
            case 'EXCEL':
                tagStr = 'fa fa-file-excel-o'
                break
            case 'WORD':
                tagStr = 'fa fa-file-word-o'
                break
            case 'PDF':
                tagStr = 'fa fa-file-pdf-o'
                break
            case 'TXT':
                tagStr = 'fa fa-file-text-o'
                break
            case 'IMAGE':
                tagStr = 'fa fa-file-image-o'
                break
            case 'AUDIO':
                tagStr = 'fa fa-file-audio-o'
                break
            case 'VIDEO':
                tagStr = 'fa fa-file-video-o'
                break
            case 'PPT':
                tagStr = 'fa fa-file-powerpoint-o'
                break
            case 'SOURCE_CODE':
                tagStr = 'fa fa-file-code-o'
                break
            default:
                break
        }
        return tagStr
    },
    getPreviewUrl(id) {
        return panUtil.getUrlPrefix() + '/file/preview/' + id + '?Authorization=' + getToken()
    },
    getUrlPrefix() {
        return '/api'
    },
    getChunkSize() {
        if (this.getChunkUploadSwitch()) {
            return 1024 * 1024 * 2
        }
        return this.getMaxFileSize()
    },
    getMaxFileSize() {
        return 1024 * 1024 * 1024 * 1
    },
    getChunkUploadSwitch() {
        return true
    },
    goHome() {
        window.location.href = '/'
    },
    handleId(id) {
        return id.replace(/\+/g, '%2B').replace(/\//g, '%2F')
    },
    getEnv() {
        // return "prod"
        return "dev"
    }
}

export default panUtil
