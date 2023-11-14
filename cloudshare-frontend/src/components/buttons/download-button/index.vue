<template>
    <div class="download-button-content">
        <el-button v-if="roundFlag" type="info" :size="size" @click="downloadFile" :loading="loading">
            下载
            <el-icon class="el-icon--right">
                <Download/>
            </el-icon>
        </el-button>
        <el-button v-if="circleFlag" icon="Download" type="info" :size="size" circle @click="downloadFile"
                   :loading="loading"></el-button>
    </div>
</template>

<script setup>
const props = defineProps({
    roundFlag: Boolean,
    circleFlag: Boolean,
    size: String,
    item: Object
})

import {ref} from 'vue'
import {ElMessage} from 'element-plus'
import {useFileStore} from '@/stores/file'
import {storeToRefs} from 'pinia'
import panUtil from '@/utils/common'
import {getToken} from '@/utils/cookie'

const fileStore = useFileStore()
const {multipleSelection} = storeToRefs(fileStore)

const loading = ref(false)

const doDownload = (item) => {
    let url = panUtil.getUrlPrefix() + '/file/download/' + item.fileId,
        fileName = item.fileName;

    fetch(url, {
        method: 'GET',
        headers: {
            'Authorization': getToken(),
        },
    })
    .then(response => {
        if (!response.ok) {
            ElMessage.error("操作频繁")
            return;
        }
        return response.blob();
    })
    .then(blob => {
        // 创建一个虚拟链接并模拟点击以触发下载
        const link = document.createElement('a');
        link.style.display = 'none';
        const objectUrl = window.URL.createObjectURL(blob);
        link.href = objectUrl;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(objectUrl);
    })
    .catch(error => {
        // 在这里处理错误，可以弹出提示信息或执行其他操作
    });
}

const doDownLoads = (items, i) => {
    if (!i) {
        i = 0
    }
    if (items.length === i) {
        return
    }
    setTimeout(function () {
        doDownload(items[i]);
        i++
        doDownLoads(items, i)
    }, 500)
}

const downloadFile = () => {
    if (!props.item && (!multipleSelection.value || multipleSelection.value.length === 0)) {
        ElMessage.error('请选择要下载的文件')
        return
    }
    if (!props.item) {
        for (let i = 0, iLength = multipleSelection.value.length; i < iLength; i++) {
            if (multipleSelection.value[i].fileType === 'DIR') {
                ElMessage.error('文件夹暂不支持下载')
                return
            }
        }
        doDownLoads(multipleSelection.value)
    }
    if (props.item) {
        if (props.item.fileType === 'DIR') {
            ElMessage.error('文件夹暂不支持下载')
            return
        }
        doDownload(props.item)
    }
}

</script>

<style>

.download-button-content {
    display: inline-block;
    margin-right: 10px;
}

</style>