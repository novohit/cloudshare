<template>
    <div>
        <span style="">欢迎您&nbsp</span>
        <el-dropdown @command="handleCommand">
            <div class="pan-user-info">
                <img class="avatar" :src="photo === ''?'/src/assets/imgs/avatar.png':photo">
            </div>
            <template #dropdown>
                <el-dropdown-menu>
                    <el-dropdown-item command="#">用户名：{{ username }}</el-dropdown-item>
                    <el-dropdown-item command="payment">套餐购买</el-dropdown-item>
                    <el-dropdown-item command="changePassword">修改密码</el-dropdown-item>
                    <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
            </template>
        </el-dropdown>
        <el-dialog
            title="修改密码"
            v-model="changePasswordDialogVisible"
            @opened="focusPasswordInput"
            @closed="resetChangePasswordForm"
            width="30%"
            :append-to-body=true
            :modal-append-to-body=false
            :center=true>
            <div>
                <el-form label-width="100px" :rules="changePasswordRules" ref="changePasswordFormRef"
                         :model="changePasswordForm"
                         status-icon
                         @submit.native.prevent>
                    <el-form-item label="旧密码" prop="password">
                        <el-input type="password"
                                  show-password
                                  ref="passwordEl"
                                  @keyup.enter.native="doChangePassword"
                                  v-model="changePasswordForm.password" autocomplete="off"/>
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPassword">
                        <el-input type="password"
                                  show-password
                                  @keyup.enter.native="doChangePassword"
                                  v-model="changePasswordForm.newPassword" autocomplete="off"/>
                    </el-form-item>
                    <el-form-item label="确认密码" prop="reNewPassword">
                        <el-input type="password"
                                  show-password
                                  @keyup.enter.native="doChangePassword"
                                  v-model="changePasswordForm.reNewPassword" autocomplete="off"/>
                    </el-form-item>
                </el-form>
            </div>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="changePasswordDialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="doChangePassword" :loading="loading">确 定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import {storeToRefs} from 'pinia'
import {ElMessage, ElMessageBox, ElNotification} from 'element-plus'
import userService from '@/api/user'
import {clearToken} from '@/utils/cookie'
import {useUserStore} from '@/stores/user'
import {useBreadcrumbStore} from '@/stores/breadcrumb'
import {useFileStore} from '@/stores/file'
import {useNavbarStore} from '@/stores/navbar'
import {useTaskStore} from '@/stores/task'

const userStore = useUserStore()
const {username} = storeToRefs(userStore)

const router = useRouter()

const breadcrumbStore = useBreadcrumbStore()
const fileStore = useFileStore()
const navbarStore = useNavbarStore()
const taskStore = useTaskStore()

const changePasswordDialogVisible = ref(false)
const loading = ref(false)

const changePasswordForm = reactive({
    password: '',
    newPassword: '',
    reNewPassword: ''
})

//后端头像
let photo = ref('')

const goLogin = () => {
    clearToken()
    breadcrumbStore.clear()
    userStore.clear()
    fileStore.clear()
    navbarStore.clear()
    taskStore.clear()
    window.location.reload()
}

const doLogout = () => {
    ElMessageBox.confirm('确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        userService.logout(() => {
            goLogin()
        }, res => {
            ElMessage.error(res.message)
        })

    })
}

const doPayment = () =>{
    console.log('???');
    router.push({name: 'Pay'})
}

const passwordEl = ref(null)

const handleCommand = (command) => {
    if (command === 'changePassword') {
        changePasswordDialogVisible.value = true
    } else if (command === 'logout') {
        doLogout()
    } else if (command === 'payment') {
        doPayment()
    }
}

const changePasswordFormRef = ref(null)

const resetChangePasswordForm = () => {
    changePasswordFormRef.value.resetFields()
}

const focusPasswordInput = () => {
    passwordEl.value.focus()
}

const checkReNewPassword = (rule, value, callback) => {
    if (value !== changePasswordForm.newPassword) {
        return callback(new Error('两次密码不一致'));
    } else {
        callback()
    }
}

const changePasswordRules = reactive({
    password: [
        {required: true, message: '请输入旧密码', trigger: 'blur'},
        {min: 8, max: 16, message: '请输入8-16位的密码', trigger: 'blur'}
    ],
    newPassword: [
        {required: true, message: '请输入新密码', trigger: 'blur'},
        {min: 8, max: 16, message: '请输入8-16位的密码', trigger: 'blur'}
    ],
    reNewPassword: [
        {required: true, message: '请输入确认密码', trigger: 'blur'},
        {validator: checkReNewPassword, trigger: 'blur'}
    ]
})

const doChangePassword = async () => {
    await changePasswordFormRef.value.validate((valid, fields) => {
        if (valid) {
            loading.value = true
            userService.changePassword({
                password: changePasswordForm.password,
                newPassword: changePasswordForm.newPassword
            }, () => {
                loading.value = false
                changePasswordDialogVisible.value = false
                ElNotification({
                    title: '成功',
                    message: '密码修改成功,即将跳转至登录页面',
                    type: 'success'
                })
                setTimeout(goLogin, 1000)
            }, res => {
                ElMessage.error(res.message)
                loading.value = false
            })
        }
    })
}

const initUserInfoIfNecessary = () => {
    if (!username.value) {
        userService.info(res => {
            fileStore.setParentId(res.data.rootId)
            fileStore.setDefaultParentId(res.data.rootId)
            fileStore.setCurDirectory(res.data.rootName)
            fileStore.setDefaultCurDirectory(res.data.rootName)
            userStore.setUsername(res.data.username)
        }, res => {
            ElMessage.error(res.message)
        })
    }
}

onMounted(() => {
    initUserInfoIfNecessary()
})

</script>

<style scoped>
.pan-user-info {
    color: #16904f;
    margin-top:10px;
}
.avatar{
    width: 36px;
    height: auto;
    margin-right: 5px;
    border-radius: 100%;
    vertical-align: middle;
}
</style>