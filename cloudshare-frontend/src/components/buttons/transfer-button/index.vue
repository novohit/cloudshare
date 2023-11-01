<template>
    <div class="transfer-button-content">
        <el-button v-if="roundFlag" :size="size" @click="transferFile">
            移动到
            <el-icon class="el-icon--right">
                <Position/>
            </el-icon>
        </el-button>
        <el-button v-if="circleFlag" icon="Position" :size="size" circle @click="transferFile">
        </el-button>
        <el-dialog
            title="移动文件"
            v-model="treeDialogVisible"
            @open="loadTreeData"
            @closed="resetTreeData"
            width="30%"
            append-to-body
            :modal-append-to-body="false"
            center>
            <div class="tree-content">
                <el-tree
                    class="tree"
                    :data="treeData"
                    empty-text="暂无文件夹数据"
                    default-expand-all
                    highlight-current
                    check-on-click-node
                    :expand-on-click-node="false"
                    ref="treeRef">
                    <template #default="{ node, data }">
                        <span class="custom-tree-node">
                            <el-icon :size="20" style="margin-right: 15px; cursor: pointer;"><Folder/></el-icon>
                            <span>{{ data.name }}</span>
                        </span>
                    </template>
                </el-tree>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="treeDialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="doChoseTreeNodeCallBack" :loading="loading">确 定</el-button>
                </span>
            </template>
        </el-dialog>
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
import {ref} from 'vue'
import {useFileStore} from '@/stores/file'
import {storeToRefs} from 'pinia'
import {ElMessage} from 'element-plus'

const fileStore = useFileStore()
const {multipleSelection} = storeToRefs(fileStore)
const treeRef = ref(null)

const transferFile = () => {
    if (!props.item && (!multipleSelection.value || multipleSelection.value.length == 0)) {
        ElMessage.error('请选择要移动的文件')
        return
    }
    treeDialogVisible.value = true
}

const treeDialogVisible = ref(false)

const loadTreeData = () => {
    fileService.getDirTree(res => {
        treeData.value = res.data
    }, res => {
        ElMessage.error(res.message)
    })
}

const resetTreeData = () => {
    treeData.value = new Array()
}

const treeData = ref([])

const doTransferFile = (target, parentId) => {
    let idArr = new Array()
    if (props.item) {
        idArr.push(props.item.fileId)
    } else {
        multipleSelection.value.forEach(item => idArr.push(item.fileId))
    }
    fileService.transfer({
        parentId: parentId,
        fileIds: idArr,
        target: target
    }, res => {
        loading.value = false
        treeDialogVisible.value = false
        ElMessage.success('文件移动成功')
        fileStore.loadFileList()
    }, res => {
        loading.value = false
        ElMessage.error(res.message)
    })
}

const doChoseTreeNodeCallBack = () => {
    loading.value = true
    let checkNode = treeRef.value.getCurrentNode()
    if (!checkNode) {
        ElMessage.error('请选择文件夹')
        loading.value = false
        return
    }
    doTransferFile(checkNode.path, checkNode.fileId)
}

const loading = ref(false)

</script>

<style>

.transfer-button-content {
    display: inline-block;
    margin-right: 10px;
}

.tree-content {
    height: 400px;
}

.tree-content .tree {
    height: 100%;
    overflow: auto;
}

</style>