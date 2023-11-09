<template>
    <div class="upload-button-content">
        <el-button v-if="roundFlag" type="default" id="uploadButton" :size="size"
                   @click="uploadDialogVisible = true">
            上传
            <el-icon class="el-icon--right">
                <Upload/>
            </el-icon>
        </el-button>
        <el-button v-if="circleFlag" icon="Upload" size="size" id="uploadButton" circle></el-button>
        <div>
            <el-dialog
                title="文件上传"
                v-model="uploadDialogVisible"
                width="40%"
                :modal="false"
                append-to-body
                @opened="rebindUploader"
                center>
                <div class="upload-content" id="upload-content">
                    <div class="drag-content">
                        <div class="drag-icon-content">
                            <el-icon size="7em" color="#DCDFE6" class="el-icon--upload">
                                <upload-filled/>
                            </el-icon>
                        </div>
                        <div class="drag-text-content">
                            <el-link :underline="false" type="info">将文件拖到此处,或</el-link>
                            <el-link :underline="false" type="primary">点击上传</el-link>
                        </div>
                    </div>
                </div>
            </el-dialog>
        </div>
    </div>
</template>

<script setup>

const props = defineProps({
    roundFlag: Boolean,
    circleFlag: Boolean,
    size: String
})

import {onMounted, ref} from 'vue'
import Uploader from 'simple-uploader.js'
import {getToken} from '@/utils/cookie'
import panUtil from '@/utils/common'
import {MD5} from '@/utils/md5'
import fileService from '@/api/file'
import {ElMessage} from 'element-plus'
import {useFileStore} from '@/stores/file'
import {useTaskStore} from '@/stores/task'
import {storeToRefs} from 'pinia'

const fileStore = useFileStore()
const taskStore = useTaskStore()

const {parentId, curDirectory} = storeToRefs(fileStore)

const uploadDialogVisible = ref(false)

// 详细文档地址：https://github.com/simple-uploader/Uploader/blob/develop/README_zh-CN.md#%E9%85%8D%E7%BD%AE
const fileOptions = {
    target: function (file, chunk) {
        if (panUtil.getChunkUploadSwitch()) {
            return panUtil.getUrlPrefix() + '/file/chunk-upload'
        }
        return panUtil.getUrlPrefix() + '/file/single-upload'
    },
    singleFile: false,
    chunkSize: panUtil.getChunkSize(),
    testChunks: panUtil.getChunkUploadSwitch(),
    forceChunkSize: false,
    simultaneousUploads: 3,
    fileParameterName: 'file',
    processParams: function (params) {
        return {
            chunkNum: params.chunkNumber,
            totalChunkSize: params.totalChunks,
            chunkSize: params.chunkSize,
            totalSize: params.totalSize,
            md5: params.identifier,
            fileName: params.filename,
            relativePath: params.relativePath,
        }
    },
    query: function (file, chunk) {
        return {
            parentId: parentId.value,
            curDirectory: curDirectory.value
        }
    },
    headers: {
        Authorization: getToken()
    },
    checkChunkUploadedByResponse: function (chunk, message) {
        let objMessage = {}
        try {
            objMessage = JSON.parse(message)
        } catch (e) {
        }
        if (objMessage.data) {
            return (objMessage.data.uploadedChunks || []).indexOf(chunk.offset + 1) >= 0
        }
        // fake response
        // objMessage.uploaded_chunks = [2, 3, 4, 5, 6, 8, 10, 11, 12, 13, 17, 20, 21]
        // check the chunk is uploaded
        return true
    },
    maxChunkRetries: 0,
    chunkRetryInterval: null,
    progressCallbacksInterval: 500,
    successStatuses: [200, 201, 202],
    permanentErrors: [404, 415, 500, 501],
    initialPaused: false
}

let uploader = undefined
const assignFlag = ref(false)

const rebindUploader = () => {
    if (uploader && !uploader.support) {
        ElMessage.error('本浏览器不支持simple-uploader，请更换浏览器重试')
    }
    if (uploader && !assignFlag.value) {
        uploader.assignBrowse(document.getElementById('upload-content'))
        uploader.assignDrop(document.getElementById('upload-content'))
        assignFlag.value = true
    }
}

const filesAdded = (files, fileList, event) => {
    // 插件在调用该方法之前会自动过滤选择的文件 去除正在上传的文件 新添加的文件就是第一个参数files
    uploadDialogVisible.value = false
    try {
        files.forEach((f) => {
            f.pause()
            if (f.size > panUtil.getMaxFileSize()) {
                throw new Error('测试环境最大上传文件限制（' + panUtil.translateFileSize(panUtil.getMaxFileSize()) + '）')
            }
            let taskItem = {
                target: f,
                fileName: f.name,
                fileSize: panUtil.translateFileSize(f.size),
                uploadedSize: panUtil.translateFileSize(0),
                status: panUtil.fileStatus.PARSING.code,
                statusText: panUtil.fileStatus.PARSING.text,
                timeRemaining: panUtil.translateTime(Number.POSITIVE_INFINITY),
                speed: panUtil.translateSpeed(f.averageSpeed),
                percentage: 0,
                parentId: parentId.value,
                curDirectory: curDirectory.value
            }
            // 添加
            taskStore.add(taskItem)
            MD5(f.file, (e, md5) => {
                f['uniqueIdentifier'] = md5
                fileService.secUpload({
                    fileName: f.name,
                    md5: md5,
                    parentId: parentId.value,
                    curDirectory: curDirectory.value,
                }, res => {
                    if (res.data === true) {
                        ElMessage.success('文件：' + f.name + ' 上传完成')
                        f.cancel()
                        taskStore.remove(f.name)
                        fileStore.loadFileList()
                        if (uploader.files.length === 0) {
                            taskStore.updateViewFlag(false)
                        }
                    } else {
                        f.resume()
                        taskStore.updateStatus({
                            fileName: f.name,
                            status: panUtil.fileStatus.WAITING.code,
                            statusText: panUtil.fileStatus.WAITING.text
                        })
                    }
                }, error => {
                    if (error.response.data.message !== '空间不足') {
                        f.resume()
                        taskStore.updateStatus({
                            fileName: f.name,
                            status: panUtil.fileStatus.WAITING.code,
                            statusText: panUtil.fileStatus.WAITING.text
                        })
                    } else {
                        f.cancel()
                        taskStore.remove(f.name)
                        taskStore.updateViewFlag(false)
                    }
                })
            })
        })
    } catch (e) {
        ElMessage.error(e.message)
        uploader.cancel()
        taskStore.clear()
        return false
    }
    taskStore.updateViewFlag(true)
    return true
}

const uploadProgress = (rootFile, file, chunk) => {
    let uploadTaskItem = taskStore.getUploadTask(file.name)
    if (file.isUploading()) {
        if (uploadTaskItem.status !== panUtil.fileStatus.UPLOADING.code) {
            taskStore.updateStatus({
                fileName: file.name,
                status: panUtil.fileStatus.UPLOADING.code,
                statusText: panUtil.fileStatus.UPLOADING.text
            })
        }
        taskStore.updateProcess({
            fileName: file.name,
            speed: panUtil.translateSpeed(file.averageSpeed),
            percentage: Math.floor(file.progress() * 100),
            uploadedSize: panUtil.translateFileSize(file.sizeUploaded()),
            timeRemaining: panUtil.translateTime(file.timeRemaining())
        })
    }
}

const doMerge = (file) => {
    let uploadTaskItem = taskStore.getUploadTask(file.name)
    taskStore.updateStatus({
        fileName: file.name,
        status: panUtil.fileStatus.MERGE.code,
        statusText: panUtil.fileStatus.MERGE.text
    })
    taskStore.updateProcess({
        fileName: file.name,
        speed: panUtil.translateSpeed(file.averageSpeed),
        percentage: 99,
        uploadedSize: panUtil.translateFileSize(file.sizeUploaded()),
        timeRemaining: panUtil.translateTime(file.timeRemaining())
    })
    fileService.mergeChunk({
        fileName: uploadTaskItem.fileName,
        md5: uploadTaskItem.target.uniqueIdentifier,
        parentId: uploadTaskItem.parentId,
        curDirectory: uploadTaskItem.curDirectory,
        totalSize: uploadTaskItem.target.size
    }, res => {
        ElMessage.success('文件：' + file.name + ' 上传完成')
        uploader.removeFile(file)
        fileStore.loadFileList()
        taskStore.updateStatus({
            fileName: file.name,
            status: panUtil.fileStatus.SUCCESS.code,
            statusText: panUtil.fileStatus.SUCCESS.text
        })
        taskStore.remove(file.name)
        if (uploader.files.length === 0) {
            taskStore.updateViewFlag(false)
        }
    }, res => {
        file.pause()
        taskStore.updateStatus({
            fileName: file.name,
            status: panUtil.fileStatus.FAIL.code,
            statusText: panUtil.fileStatus.FAIL.text
        })
    })
}

const fileUploaded = (rootFile, file, message, chunk) => {
    let res = {}
    try {
        res = JSON.parse(message)
    } catch (e) {
    }
    if (res.code === 0) {
        if (res.data) {
            if (res.data) {
                doMerge(file)
            } else if (res.data.uploadedChunks && res.data.uploadedChunks.length === file.chunks.length) {
                doMerge(file)
            }
        } else {
            ElMessage.success('文件：' + file.name + ' 上传完成')
            uploader.removeFile(file)
            fileStore.loadFileList()
            taskStore.updateStatus({
                fileName: file.name,
                status: panUtil.fileStatus.SUCCESS.code,
                statusText: panUtil.fileStatus.SUCCESS.text
            })
            taskStore.remove(file.name)
            if (uploader.files.length === 0) {
                taskStore.updateViewFlag(false)
            }
        }
    } else {
        file.pause()
        taskStore.updateStatus({
            fileName: file.name,
            status: panUtil.fileStatus.FAIL.code,
            statusText: panUtil.fileStatus.FAIL.text
        })
    }
}

const uploadComplete = () => {
}

const uploadError = (rootFile, file, data, chunk) => {
    if (data.indexOf('空间不足')) {
        ElMessage.error("空间不足")
        taskStore.remove(file.name)
        taskStore.updateViewFlag(false)
    } else {
        taskStore.updateStatus({
            fileName: file.name,
            status: panUtil.fileStatus.FAIL.code,
            statusText: panUtil.fileStatus.FAIL.text
        })
        taskStore.updateProcess({
            fileName: file.name,
            speed: panUtil.translateSpeed(0),
            percentage: 0,
            uploadedSize: panUtil.translateFileSize(0),
            timeRemaining: panUtil.translateTime(Number.POSITIVE_INFINITY)
        })
    }
}

const initUploader = () => {
    taskStore.clear()
    // 实例化一个上传对象
    uploader = new Uploader(fileOptions)
    // 如果不支持 需要降级的地方
    if (!uploader.support) {
        ElMessage.error('本浏览器不支持simple-uploader，请更换浏览器重试')
    }
    // 绑定进队列
    uploader.on("filesAdded", filesAdded)
    // 绑定进度
    uploader.on("fileProgress", uploadProgress)
    // 上传成功监听
    uploader.on("fileSuccess", fileUploaded)
    // 上传全部完成调用
    uploader.on("complete", uploadComplete)
    // 上传出错调用
    uploader.on("fileError", uploadError)
}

onMounted(() => {
    initUploader()
})

</script>

<style>
.upload-button-content {
    display: inline-block;
    margin-right: 10px;
}

.upload-content {
    width: 100%;
    height: 300px;
    line-height: 300px;
    display: flex;
    justify-content: center;
}

.upload-content .drag-content {
    border: 0.2em dashed #DCDFE6;
    border-radius: 1em;
    width: 80%;
    height: 250px;
    line-height: 250px;
    display: flex;
    align-items: center;
    flex-direction: column;
}

.upload-content .drag-content:hover {
    border: 0.2em dashed #2faa69;
}

.upload-content .drag-content .drag-icon-content {
    height: 100px;
    line-height: 100px;
    width: 100px;
    margin-top: 25px;
}

.upload-content .drag-content .drag-text-content {
    display: block;
    height: 30px;
    line-height: 30px;
    margin-top: 30px;
}
</style>