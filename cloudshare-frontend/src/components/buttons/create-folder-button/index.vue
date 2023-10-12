<template>
    <div class="create-folder-button-content">
        <el-button v-if="roundFlag" type="success" :size="size" round @click="addDirDialogVisible = true">
            新建文件夹
            <el-icon class="el-icon--right">
                <FolderAdd/>
            </el-icon>
        </el-button>
        <el-button v-if="circleFlag" icon="FolderAdd" type="success" :size="size" circle
                   @click="addDirDialogVisible = true"></el-button>
        <el-dialog
            title="新建文件夹"
            v-model="addDirDialogVisible"
            width="30%"
            @opened="focusInput"
            @closed="resetForm"
            :append-to-body=true
            :modal-append-to-body=false
            :center=true>
            <div>
                <el-form label-width="100px" :rules="addDirRules" ref="addDirFormRef"
                         :model="addDirForm"
                         status-icon
                         @submit.native.prevent>
                    <el-form-item label="文件夹名称" prop="dirName">
                        <el-input type="text"
                                  ref="dirNameEl"
                                  @keyup.enter.native="doAddDir"
                                  v-model="addDirForm.dirName" autocomplete="off"/>
                    </el-form-item>
                </el-form>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="addDirDialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="doAddDir" :loading="loading">确 定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
const props = defineProps({
    roundFlag: Boolean,
    circleFlag: Boolean,
    size: String
})

import fileService from '@/api/file'
import {reactive, ref} from 'vue'
import {useFileStore} from '@/stores/file'
import {storeToRefs} from 'pinia'
import {ElMessage} from 'element-plus'

const fileStore = useFileStore()
const {parentId, curDirectory} = storeToRefs(fileStore)

const addDirDialogVisible = ref(false)
const loading = ref(false)

const dirNameEl = ref(null)
const addDirFormRef = ref(null)

const addDirForm = reactive({
    dirName: ''
})

const resetForm = () => {
    addDirFormRef.value.resetFields()
}

const focusInput = () => {
    dirNameEl.value.focus()
}

const doAddDir = async () => {
    await addDirFormRef.value.validate((valid, fields) => {
        if (valid) {
            loading.value = true
            fileService.addDir({
                parentId: parentId.value,
                curDirectory: curDirectory.value,
                dirName: addDirForm.dirName
            }, res => {
                loading.value = false
                addDirDialogVisible.value = false
                ElMessage.success('新建成功')
                fileStore.loadFileList()
            }, res => {
                ElMessage.error(res.message)
                loading.value = false
            })
        }
    })
}

const addDirRules = reactive({
    dirName: [
        {required: true, message: '请输入文件夹名称', trigger: 'blur'}
    ]
})

</script>

<style>

.create-folder-button-content {
    display: inline-block;
    margin-right: 10px;
}

</style>