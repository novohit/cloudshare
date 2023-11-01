<template>
    <div class="pan-main-breadcrumb-content">
        <el-breadcrumb style="display: inline-block;">
            <el-breadcrumb-item>
                <a class="breadcrumb-item-a" @click="goBack" href="#">返回</a>
            </el-breadcrumb-item>
        </el-breadcrumb>
        <el-divider direction="vertical" style="vertical-align: top !important;"/>
        <el-breadcrumb separator-icon="ArrowRight" style="display: inline-block;">
            <el-breadcrumb-item v-for="(item, index) in breadCrumbs" :key="index">
                <a class="breadcrumb-item-a" @click="goToThis(item.fileId, index)" href="#">{{ item.name }}</a>
            </el-breadcrumb-item>
        </el-breadcrumb>
    </div>
</template>

<script setup>
import {useBreadcrumbStore} from '@/stores/breadcrumb'
import {useFileStore} from '@/stores/file'
import {storeToRefs} from 'pinia'

const breadcrumbStore = useBreadcrumbStore()
const fileStore = useFileStore()

const {breadCrumbs} = storeToRefs(breadcrumbStore)

const goBack = () => {
    fileStore.setSearchFlag(false)
    if (breadCrumbs.value.length > 1) {
        let resolveBreadCrumbs = [...breadCrumbs.value]
        resolveBreadCrumbs.pop()
        let newId = resolveBreadCrumbs.pop().fileId
        goToThis(newId, breadCrumbs.value.length - 2)
    }
}

const goToThis = (fileId, index) => {
    let curDirectory = breadCrumbs.value
        .slice(0, index + 1)
        .map(item => item.name)
        .join('/');
    // 去除多余的 /
    curDirectory = curDirectory.length > 1 ? curDirectory.replace(/^\//, '') + "/" : curDirectory;
    console.log("goToThis", curDirectory)
    if (fileId !== '-1') {
        let newBreadCrumbs = new Array()
        breadCrumbs.value.some(item => {
            newBreadCrumbs.push(item)
            if (item.fileId == fileId) {
                return true
            }
        })
        breadcrumbStore.reset(newBreadCrumbs)
        fileStore.setParentId(fileId)
        fileStore.setCurDirectory(curDirectory)
        fileStore.setSearchFlag(false)
        fileStore.loadFileList()
    }
}

</script>

<style scoped>
.pan-main-breadcrumb-content {
    width: 100%;
    padding: 10px 0 0 25px;
}

.breadcrumb-item-a {
    cursor: pointer !important;
    color: #2faa69 !important;
}
</style>