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
    userService.pay({
        "token": "test_f334d0d47a83",
        "product_id": 1,
        "buy_num": 1,
        "total_amount": 27.69,
        "actual_pay_amount": 19,
        "pay_type": "ALI_PAY_PC",
        "bill_type": "test_ea304456dffc",
        "bill_header": "test_02f56667b032",
        "bill_content": "test_951a4562e602",
        "bill_receiver_phone": "test_71398c1e670e",
        "bill_receiver_email": "test_17b07498eadd"
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