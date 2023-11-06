<template>
    <div class="pay-content">
        <el-card class="content">
            <h1>套餐选择</h1>
            <div>
                <div class="list" v-for="item in listData" :key="item.id" @click="pay(item.amount)">
                    <h2>{{item.title}}</h2>
                    <h2>{{item.amount}}</h2>
                </div>
            </div>

            <el-dialog
                v-model="paylog"
                title="请选择支付方式"
                width="30%"
            >
                <div>
                    <h2 style="text-align: center">{{payPrice}}RMB</h2>
                    <div class="pay-button" @click="alipay">
                        <i class="iconfont icon-alipay"></i>
                        <span>支付宝支付</span>
                    </div>
                    <div class="pay-button" @click="wechat">
                        <i class="iconfont icon-wechat-pay"></i>
                        <span>微信支付</span>
                    </div>
                </div>
                <template #footer>
                    <span class="dialog-footer">
                        <el-button @click="paylog = false">重新选择</el-button>
                    </span>
                </template>
            </el-dialog>
            
        </el-card>
    </div>
</template>

<script setup>
import PanHeader from '@/components/header/index.vue'
import userService from '@/api/user'
import {ref, onBeforeMount} from 'vue'

const listData = ref([])
const paylog = ref(false)
const payPrice = ref(0)

onBeforeMount(()=>{
    userService.list(res=>{
        for(let i = 0; i < res.data.length; i++){
            const temp = {
                id:res.data[i].id,
                title:res.data[i].title,
                amount:res.data[i].amount
            }
            listData.value.push(temp)
        }
    },err=>{
        ElMessage.error(err.message)
    })

})

const pay = (price)=>{
    paylog.value = true
    payPrice.value = price
} 

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
.icon-alipay{
    color: rgba(64, 158, 255);
}
.icon-wechat-pay{
    color: rgba(9, 187, 7);
}
.list{
    height:50px;
    margin-top:20px;
    padding:10px;
    border:2px solid #eee;
    border-radius: 5px;
    display: flex;
    align-items: center;
    justify-content: space-between;
}
.list:hover{
    cursor: pointer;
    background-color: #eee;
}

.pay-button{
    height:50px;
    margin-top:20px;
    padding:10px;
    border:2px solid #eee;
    border-radius: 5px;
    display: flex;
    align-items: center;
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