<template>
    <div class="pay-content">
        <el-card class="content">
            <h1>支付方式</h1>
            <div class="pay-button" @click="alipay">
                <i class="iconfont icon-zhifubaozhifu"></i>
                <span>支付宝支付</span>
            </div>
            <div class="pay-button" @click="wechat">
                <i class="iconfont icon-wechat-pay"></i>
                <span>微信支付</span>
            </div>
        </el-card>
    </div>
</template>

<script setup>
import PanHeader from '@/components/header/index.vue'
import userService from '@/api/user'
import {ref} from 'vue'

const data = ref('')
const alipay = ()=>{
    const productId = 1;
    const buyNum = 1;
    const actualPayAmount = 1;

    userService.pay({
        productId: productId,
        buyNum: buyNum,
        actualPayAmount: actualPayAmount,
        payType: "ALI_PAY_PC",
    }, res => {
        // 处理掉script标签的内容
        const fragment = document.createElement('divform');
        fragment.innerHTML=document.write(res.data);
        document.forms[0].submit();
    }, err => {
        ElMessage.error(err.message)
    })
    
}
const wechat = ()=>{
    console.log('wechat');
}
</script>

<style scoped>
.pay-content{
    width:100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
}
.content{
    width:800px;
    height: 500px;
    border:1px solid #eee;
}
.icon-zhifubaozhifu{
    color: rgba(64, 158, 255);
}
.icon-wechat-pay{
    color: rgba(9, 187, 7);
}
.pay-button{
    height:50px;
    margin-top:20px;
    padding:10px;
    border:2px solid #eee;
    border-radius: 5px;
}
.pay-button i{
    font-size: 50px;
}
.pay-button span{
    padding-left:10px;
    font-size: 20px;
}
.pay-button:hover{
    cursor: pointer;
    background-color: #eee;
}
</style>