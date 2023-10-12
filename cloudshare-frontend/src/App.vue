<template>
    <div id="app">
        <router-view v-slot="{ Component, route }">
            <Transition name="fade">
                <component :is="Component" :key="route.path"/>
            </Transition>
        </router-view>
    </div>
</template>

<script setup>
import {RouterView} from 'vue-router'
import { onMounted } from 'vue'

onMounted(() => {
  document.body.style.setProperty('--el-color-primary', '#16904f');
  document.body.style.setProperty('--el-color-primary-light-9', '#F5FBF0');
  document.body.style.setProperty('--el-color-primary-light-3', '#2faa69');
})
</script>

<style scoped>

</style>
