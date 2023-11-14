<template>
    <div class="share-button-content">
        <el-button class="share-button" v-if="roundFlag" :size="size" @click="shareFile">
            分享
            <el-icon class="el-icon--right">
                <Share/>
            </el-icon>
        </el-button>
        <el-button class="share-button" v-if="circleFlag" icon="Share" :size="size" circle @click="shareFile">
        </el-button>
        <el-dialog
            :title="shareTitle"
            v-model="shareDialogVisible"
            @closed="resetForm"
            width="30%"
            append-to-body
            :modal-append-to-body="false"
            center>
            <div>
                <div v-if="step === 1">
                    <el-form label-width="100px" ref="shareFormRef"
                             :model="shareFileForm"
                             status-icon
                             @submit.native.prevent>
                        <el-form-item label="分享类型">
                            <el-radio-group v-model="shareFileForm.visibleType">
                                <el-radio disabled label="PRIVATE">有提取码</el-radio>
                            </el-radio-group>
                        </el-form-item>
                        <el-form-item label="分享有效期">
                            <el-date-picker
                                v-model="shareFileForm.expiredAt"
                                type="datetime"
                                value-format="YYYY-MM-DD HH:mm:ss"
                                placeholder="选择日期"
                            ></el-date-picker>
                        </el-form-item>
                    </el-form>
                </div>
                <div v-if="step === 2">
                    <el-form label-width="100px"
                             status-icon
                             @submit.native.prevent>
                        <el-form-item label="分享链接" prop="url">
                            <el-link :underline=false type="primary"><span>{{ shareResultForm.url }}</span>
                            </el-link>
                        </el-form-item>
                        <el-form-item label="提取码">
                            <el-link :underline=false type="success"><span>{{ shareResultForm.code }}</span>
                            </el-link>
                        </el-form-item>
                        <div class="share-result-button-content">
                            <el-button type="primary" class="share-result-copy-button" @click="copy">
                                点击复制
                                <el-icon class="el-icon--right">
                                    <DocumentCopy/>
                                </el-icon>
                            </el-button>
                        </div>
                    </el-form>
                </div>
            </div>
            <template #footer>
                <span v-if="step === 1" class="dialog-footer">
                    <el-button @click="shareDialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="doShareFile" :loading="loading">确 定</el-button>
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

import {reactive, ref} from 'vue'
import {ElMessage} from 'element-plus'
import {useFileStore} from '@/stores/file'
import {storeToRefs} from 'pinia'
import shareService from '@/api/share'
import useClipboard from 'vue-clipboard3'

const {toClipboard} = useClipboard()

const fileStore = useFileStore()
const {multipleSelection} = storeToRefs(fileStore)

const shareTitle = ref('')
const shareDialogVisible = ref(false)
const loading = ref(false)
const shareFormRef = ref(null)
const step = ref(1)

const shareFileForm = reactive({
  visibleType: 'PRIVATE',
  expiredAt: ''
});

const today = new Date();
const tomorrow = new Date(today);
tomorrow.setDate(today.getDate() + 2);
tomorrow.setHours(0, 0, 0, 0);

shareFileForm.expiredAt = tomorrow.toISOString();

const shareResultForm = reactive({
    url: '',
    code: ''
})

const handleFilename = (fileName) => {
    if (fileName.length > 10) {
        fileName = fileName.substring(0, 11) + '...'
    }
    return fileName
}

const shareFile = () => {
    if (!props.item && (!multipleSelection.value || multipleSelection.value.length === 0)) {
        ElMessage.error('请选择要分享的文件')
        return
    }
    if (!props.item && (!multipleSelection.value || multipleSelection.value.length !== 1)) {
        ElMessage.error('暂不支持多选共享')
        return
    }
    if (props.item) {
        shareTitle.value = '分享文件（' + handleFilename(props.item.fileName) + ')'
    } else {
        if (multipleSelection.value.length === 1) {
            shareTitle.value = '分享文件（' + handleFilename(multipleSelection.value[0].fileName) + ')'
        } else {
            shareTitle.value = '分享文件（' + handleFilename(multipleSelection.value[0].fileName) + '等)'
        }
    }
    shareDialogVisible.value = true
}

const doShareFile = async () => {

    await shareFormRef.value.validate((valid, fields) => {
        if (valid) {
            let fileIdList = new Array()
            loading.value = true
            if (props.item) {
                fileIdList.push(props.item.fileId)
            } else {
                multipleSelection.value.forEach(item => {
                    fileIdList.push(item.fileId)
                })
            }
            console.log(fileIdList)
            shareService.createShare({
                visibleType: shareFileForm.visibleType,
                expiredAt: shareFileForm.expiredAt,
                fileId: fileIdList[0]
            }, res => {
                loading.value = false
                shareTitle.value = '分享成功！'
                shareResultForm.url = res.data.url
                shareResultForm.code = res.data.code
                step.value = 2
            }, res => {
                loading.value = false
                ElMessage.error(res.message)
            })
        }
    })
}

const resetForm = () => {
    if (shareFormRef.value) {
        shareFormRef.value.resetFields()
    }
    step.value = 1
    shareTitle.value = ''
    shareResultForm.value = {
        url: '',
        code: ''
    }
}

const copy = async () => {
    try {
        let shareMessage = '链接：' + shareResultForm.url + '\n提取码：' + shareResultForm.code + '\n赶快分享给小伙伴吧！'
        await toClipboard(shareMessage)
        ElMessage.success('复制成功')
    } catch (e) {
        console.error(e)
        ElMessage.error('复制失败')
    }
}

</script>

<style>

.share-button-content {
    display: inline-block;
    margin-right: 10px;
}

.share-button-content .share-button {
    background-color: #F2F6FC;
}

.share-result-button-content {
    width: 100%;
    height: 10px;
    line-height: 30px;
    text-align: right;
    padding: 0 10px 20px 0;
}

</style>