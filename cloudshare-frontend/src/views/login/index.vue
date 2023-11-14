<template>
    <div class="login-content">
        <el-card class="content">
            <div class="box">
                <div class="loginBox">
                    <h3>Cloudshare Login</h3>
                    <el-form class="loginForm" :model="loginForm">
                        <el-form-item label="账号">
                            <el-input type="text" v-model="loginForm.username" autocomplete="off" placeholder="请输入" ref="usernameEl"/>
                        </el-form-item>
                        <el-form-item label="密码">
                            <el-input type="password" v-model="loginForm.password" autocomplete="off" placeholder="请输入"/>
                        </el-form-item>
                        <el-form-item>
                            <el-button style="width: 100%;" type="primary" :loading="loading" @click="doLogin">登录</el-button>
                        </el-form-item>
                    </el-form>
                    
                    <div class="btns">
                        <el-button link>
                            &lt&lt
                            <el-icon><HomeFilled /></el-icon>
                            首页
                        </el-button>
                        <el-button @click="visitor" link>
                            <el-icon><UserFilled /></el-icon>
                            游客登录
                        </el-button>
                        <el-button @click="google" link>
                            <el-icon><ChromeFilled /></el-icon>
                            Google
                        </el-button>
                    </div>
                </div>
                <div style="flex:1">
                    <img class="imgNew" src="../../assets/imgs/bg.jpg"/>
                </div>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import {onMounted, reactive, ref} from 'vue'
import {useRouter} from 'vue-router'
import panUtil from '@/utils/common'
import {ElMessage,ElCard,ElForm,ElInput,ElButton,ElFormItem} from 'element-plus'
import userService from '@/api/user'
import {setToken} from '@/utils/cookie'
import {useFileStore} from '@/stores/file'
import {useUserStore} from '@/stores/user'

const router = useRouter()

const loading = ref(false)

const loginForm = reactive({
    username: '',
    password: ''
})

const visitorForm = reactive({
    username: 'guest',
    password: 'guest'
})

const fileStore = useFileStore()
const userStore = useUserStore()

const {setParentId, setDefaultParentId, setCurDirectory, setDefaultCurDirectory} = fileStore
const {setUsername, setAvatar, setPlan} = userStore

const usernameEl = ref(null)
onMounted(() => {
    usernameEl.value.focus()
})

const doLogin = () => {
    if (checkLoginForm()) {
        loading.value = true
        userService.login(loginForm, res => {
            setToken(res.data)
            userService.info(res => {
                setParentId(res.data.rootId)
                setDefaultParentId(res.data.rootId)
                setCurDirectory(res.data.rootName)
                setDefaultCurDirectory(res.data.rootName)
                setUsername(res.data.username)
                setAvatar(res.data.avatar)
                setPlan(res.data.plan,res.data.totalQuota,res.data.usedQuota)
                router.push({name: 'Index'})
            }, res => {
                ElMessage.error(res.message)
            })
        }, res => {
            ElMessage.error(res.message)
            loading.value = false
        })
    }
}

const visitor = ()=>{
    loading.value = true
        userService.login(visitorForm, res => {
            setToken(res.data)
            userService.info(res => {
                setParentId(res.data.rootId)
                setDefaultParentId(res.data.rootId)
                setCurDirectory(res.data.rootName)
                setDefaultCurDirectory(res.data.rootName)
                setUsername(res.data.username)
                setAvatar(res.data.avatar)
                setPlan(res.data.plan,res.data.totalQuota,res.data.usedQuota)
                router.push({name: 'Index'})
            }, res => {
                ElMessage.error(res.message)
            })
        }, res => {
            ElMessage.error(res.message)
            loading.value = false
        })
}

const google = ()=>{
    if (panUtil.getEnv() === 'prod') {
        ElMessage.error("线上环境未开启Google登录")
        return
    }
    window.location.href ="/api/oauth/render/google";
    console.log(window.location.href);
}

const goForget = () => {
    router.push({name: 'Forget'})
}

const checkLoginForm = () => {
    // if (!panUtil.checkUsername(loginForm.username)) {
    //     ElMessage.error('请输入6-16位只包含数字和字母的用户名')
    //     return false
    // }
    // if (!panUtil.checkPassword(loginForm.password)) {
    //     ElMessage.error('请输入8-16位的密码')
    //     return false
    // }
    return true
}

const goRegister = () => {
    router.push({name: 'Register'})
}
</script>

<style scoped>

*, *:before, *:after {
    box-sizing: border-box;
    margin: 0;
    padding: 0;
}

input {
    border: none;
    outline: none;
    background: none;
    font-family: 'Open Sans', Helvetica, Arial, sans-serif;
}

.login-content{
    width:100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}
.content {
    height: 400px;
    width: 850px;
    min-width: 850px;
}

.box{
    display: flex;
    flex-direction: row;

    position: relative;
    height: 100%;
    width:100%;
}
.loginBox{
    display: flex;
    align-items: center;
    flex-direction: column;
    height:100%;
    text-align: center;
    margin-left:20px;
    margin-right: 20px;
}

.loginForm{
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height:200px;
}

.el-form-item{
    margin-top: 20px;
}

.btns{
    position: absolute;
    bottom: 0;
    left: 0;
}

.imgNew{
    width: 100%;
    height: 100%;
    object-fit: cover;
}


h2 {
    width: 100%;
    font-size: 26px;
    text-align: center;
    font-weight: normal;
}

label {
    display: block;
    width: 260px;
    margin: 25px auto 0;
    text-align: center;
}

label span {
    font-size: 12px;
    color: #909399;
    text-transform: uppercase;
}

input {
    display: block;
    width: 100%;
    margin-top: 5px;
    padding-bottom: 5px;
    font-size: 16px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.4);
    text-align: center;
}
</style>