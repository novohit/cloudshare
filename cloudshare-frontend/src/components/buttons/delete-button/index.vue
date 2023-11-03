<template>
    <div class="download-button-content">
        <el-button v-if="roundFlag" type="danger" :size="size" @click="deleteFile">
            删除
            <el-icon class="el-icon--right">
                <Delete/>
            </el-icon>
        </el-button>
        <el-button v-if="circleFlag" icon="Delete" type="danger" :size="size" circle @click="deleteFile"></el-button>
    </div>
</template>

<script setup>
const props = defineProps({
    roundFlag: Boolean,
    circleFlag: Boolean,
    size: String,
    item: Object
})

import fileService from '@/api/file'
import {useFileStore} from '@/stores/file'
import {storeToRefs} from 'pinia'
import {ElMessage, ElMessageBox} from 'element-plus'

const fileStore = useFileStore()
const {multipleSelection} = storeToRefs(fileStore)
const {parentId, curDirectory} = storeToRefs(fileStore)

const doDeleteFile = (fileIds) => {
    ElMessageBox.confirm('文件删除后将保存在回收站，您确定这样做吗？', '删除文件', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        fileService.delete({
            fileIds: fileIds
        }, res => {
            ElMessage.success('删除成功')
            fileStore.loadFileList()
        }, res => {
            ElMessage.error(res.message)
        })
    })
}

const deleteFile = () => {
    let idArr = new Array()
    if (props.item) {
        idArr.push(props.item.fileId)
        doDeleteFile(idArr)
        return
    }
    if (multipleSelection.value && multipleSelection.value.length > 0) {
        multipleSelection.value.forEach(item => idArr.push(item.fileId))
        doDeleteFile(idArr)
        return
    }
    ElMessage.error('请选择要删除的文件')
}
</script>

<style>
.download-button-content {
    display: inline-block;
    margin-right: 10px;
}
</style>