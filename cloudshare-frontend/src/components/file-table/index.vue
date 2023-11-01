<template>
    <div class="pan-main-file-list-content">
        <el-table
            v-loading="tableLoading"
            ref="fileTableRef"
            :data="fileList"
            :height="tableHeight"
            tooltip-effect="dark"
            style="width: 100%"
            @selection-change="handleSelectionChange"
            @cell-mouse-enter="showOperation"
            @cell-mouse-leave="hiddenOperation"
        >
            <el-table-column
                type="selection"
                width="55">
            </el-table-column>
            <el-table-column
                label="文件名"
                prop="fileName"
                sortable
                show-overflow-tooltip
                min-width="750">
                <template #default="scope">
                    <div @click="clickFilename(scope.row)" class="file-name-content">
                        <i :class="getFileFontElement(scope.row.fileType)"
                           style="margin-right: 15px; font-size: 20px; cursor: pointer;"/>
                        <span style="cursor:pointer;">{{ scope.row.fileName }}</span>
                    </div>
                    <div class="file-operation-content">
                        <el-tooltip class="item" effect="light" content="下载" placement="top">
                            <download-button size="small" circle-flag :item="scope.row"/>
                        </el-tooltip>
                        <el-tooltip class="item" effect="light" content="重命名" placement="top">
                            <rename-button size="small" circle-flag :item="scope.row"/>
                        </el-tooltip>
                        <el-tooltip class="item" effect="light" content="删除" placement="top">
                            <delete-button size="small" circle-flag :item="scope.row"/>
                        </el-tooltip>
                        <el-tooltip class="item" effect="light" content="分享" placement="top">
                            <share-button size="small" circle-flag :item="scope.row"/>
                        </el-tooltip>
                        <el-tooltip class="item" effect="light" content="复制到" placement="top">
                            <copy-button size="small" circle-flag :item="scope.row"/>
                        </el-tooltip>
                        <el-tooltip class="item" effect="light" content="移动到" placement="top">
                            <transfer-button size="small" circle-flag :item="scope.row"/>
                        </el-tooltip>
                    </div>
                </template>
            </el-table-column>
            <el-table-column
                v-if="searchFlag"
                prop="parentFilename"
                label="位置"
                min-width="120"
                align="center">
                <template #default="scope">
                    <el-link @click="goInFolder(scope.row.parentId)" type="primary">{{ scope.row.parentFilename }}
                    </el-link>
                </template>
            </el-table-column>
            <el-table-column
                prop="size"
                sortable
                label="大小"
                min-width="120"
                align="center">
            </el-table-column>
            <el-table-column
                prop="updatedAt"
                sortable
                align="center"
                label="修改日期"
                min-width="240">
            </el-table-column>
        </el-table>
    </div>
</template>

<script setup>

import DownloadButton from '@/components/buttons/download-button/index.vue'
import DeleteButton from '@/components/buttons/delete-button/index.vue'
import RenameButton from '@/components/buttons/rename-button/index.vue'
import CopyButton from '@/components/buttons/copy-button/index.vue'
import TransferButton from '@/components/buttons/transfer-button/index.vue'
import ShareButton from '@/components/buttons/share-button/index.vue'
import fileService from '@/api/file'
import panUtil from '@/utils/common'
import {onMounted, ref} from 'vue'
import {useFileStore} from '@/stores/file'
import {useBreadcrumbStore} from '@/stores/breadcrumb'
import {storeToRefs} from 'pinia'
import {ElMessage} from 'element-plus'
import {useRouter} from 'vue-router'
import ImageViewer from '@luohc92/vue3-image-viewer'
import '@luohc92/vue3-image-viewer/dist/style.css'

const router = useRouter()

const fileStore = useFileStore()
const breadcrumbStore = useBreadcrumbStore()

const {fileList, tableLoading, searchFlag} = storeToRefs(fileStore)

const tableHeight = ref(window.innerHeight - 200)

const handleSelectionChange = (multipleSelection) => {
    fileStore.setMultipleSelection(multipleSelection)
}

const showOperation = (row, column, cell, event) => {
    panUtil.showOperation(cell)
}

const hiddenOperation = (row, column, cell, event) => {
    panUtil.hiddenOperation(cell)
}

const goInFolder = (fileId, curDirectory, fileName) => {
    fileStore.setSearchFlag(false)
    breadcrumbStore.addItem({
            fileId: fileId,
            name: fileName
        })
    fileStore.setParentId(fileId)
    const newCurDirectory = curDirectory === '/' ? curDirectory + fileName : curDirectory + '/' + fileName
    fileStore.setCurDirectory(newCurDirectory)
    fileStore.loadFileList()
}

const openNewPage = (path, name, params, query) => {
    const {href} = router.resolve({
        path: path,
        name: name,
        params: params,
        query: query
    })
    window.open(href, '_blank')
}

const showOffice = (row) => {
    openNewPage('/preview/office', 'PreviewOffice', {
        id: panUtil.handleId(row.id)
    })
}

const showIframe = (row) => {
    openNewPage('/preview/iframe', 'PreviewIframe', {
        id: row.id
    })
}

const showImg = (row) => {
    let imgs = new Array()
    let imgIndex = 0
    for (let i = 0, iLength = fileList.value.length; i < iLength; ++i) {
        if (fileList.value[i].fileType === 'IMAGE') {
            imgs.push(panUtil.getPreviewUrl(fileList.value[i].id))
            if (fileList.value[i].id === row.id) {
                imgIndex = imgs.length - 1
            }
        }
    }

    ImageViewer({
        images: imgs,
        curIndex: imgIndex,
        zIndex: 2000,
        showDownload: false,
        showThumbnail: true,
        handlePosition: "bottom",
        maskBgColor: "rgba(0,0,0,0.7)",
        onClose: () => {

        },
    })
}

const showMusic = (row) => {
    openNewPage('/preview/music', 'PreviewMusic', {
        id: row.id,
        curDirectory: panUtil.handleId(row.curDirectory)
    })
}

const showVideo = (row) => {
    console.log(row)
    openNewPage('/preview/video', 'PreviewVideo', {
        id: row.id,
        curDirectory: panUtil.handleId(row.curDirectory)
    })
}

const showCode = (row) => {
    openNewPage('/preview/code', 'PreviewCode', {
        id: row.id,
    }, {
        fileName: row.fileName
    })
}

const getFileFontElement = (type) => {
    return panUtil.getFileFontElement(type)
}

const clickFilename = (row) => {
    switch (row.fileType) {
        case 'DIR':
            // goInFolder(panUtil.handleId(row.id))
            goInFolder(row.fileId, row.curDirectory, row.fileName)
            break
        case 3:
        case 4:
        case 10:
            showOffice(row)
            break
        case 5:
        case 'TXT':
            showIframe(row)
            break
        case 'IMAGE':
            showImg(row)
            break
        case 'AUDIO':
            showMusic(row)
            break
        case 'VIDEO':
            showVideo(row)
            break
        case 'SOURCE_CODE':
            showCode(row)
        default:
            break
    }
}

onMounted(() => {
    fileStore.setMultipleSelection(new Array())
})

</script>

<style>

.pan-main-file-list-content {
    width: 100%;
    height: 100%;
    margin-top: 20px;
}

.file-operation-content {
    display: none;
    position: absolute;
    right: 100px;
    top: 8px;
}

</style>