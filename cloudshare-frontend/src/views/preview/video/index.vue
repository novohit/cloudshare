<template>
    <div>
        <pan-simple-header/>
        <div class="pan-video-content">
            <div class="video-name-content">
                <p class="video-name">{{ videoName }}</p>
            </div>
            <el-divider></el-divider>
            <el-row>
                <el-col :span="18">
                    <!--视频播放容器-->
                    <div class="video-content">
                        <video id="r_pan_video_player" :src="videoShowPath" autoplay="true" controls="true"/>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="video-list-content">
                        <el-menu class="video-list"
                                 :default-active="activeIndex"
                                 @select="selectVideo">
                            <el-menu-item v-for="(item, index) in videoList" :key="index" :index="item.fileId">
                                <i class="fa fa-video-camera"></i>
                                <span slot="title">{{ item.fileName }}</span>
                            </el-menu-item>
                        </el-menu>
                    </div>
                </el-col>
            </el-row>
        </div>
    </div>
</template>

<script setup>

import PanSimpleHeader from '@/components/simple-header/index.vue'
import fileService from '@/api/file'
import panUtil from '@/utils/common'
import {useRoute} from 'vue-router'
import {onMounted, ref} from 'vue'
import {ElMessage} from 'element-plus'

const route = useRoute()
const videoName = ref('')
const videoList = ref([])
const videoShowPath = ref('')
const activeIndex = ref('0')

const renderVideoList = (dataList) => {
    videoList.value = new Array()
    dataList.forEach((item, index) => {
        item.fileName = item.fileName.substring(0, item.fileName.lastIndexOf('.'))
        if (item.fileName.length > 15) {
            item.fileName = item.fileName.substring(0, 16) + '...'
        }
        if (item.fileId === route.params.fileId) {
            videoName.value = item.fileName
            videoShowPath.value = panUtil.getPreviewUrl(item.fileId)
        }
        videoList.value.push(item)
    })
    console.log(route.params.fileId)
    activeIndex.value = route.params.fileId
    console.log("active", activeIndex.value)
}

const selectNext = () => {
    let i = '',
        currentId = activeIndex.value
    videoList.value.some((item, index) => {
        if (item.fileId === currentId) {
            i = index
            return true
        }
    })
    if (i === videoList.value.length - 1) {
        return
    }
    let item = videoList.value[++i]
    videoName.value = item.fileName
    videoShowPath.value = panUtil.getPreviewUrl(item.fileId)
    activeIndex.value = item.fileId
}

const selectVideo = (index) => {
    activeIndex.value = index
    videoList.value.some(item => {
        if (item.fileId === index) {
            videoName.value = item.fileName
            videoShowPath.value = panUtil.getPreviewUrl(item.fileId)
            return true
        }
    })
}

const listenVideoPlayer = () => {
    document.getElementById('r_pan_video_player').addEventListener('ended', () => {
        selectNext()
    }, false)
}

const init = () => {
    fileService.list({
        curDirectory: route.params.curDirectory,
        fileTypeList: ['VIDEO']
    }, function (res) {
        if (res.code === 0) {
            const list = res.data.map(video => {
            // 将视频对象的 id 属性转换为字符串
            return { ...video, id: String(video.id) };
            });
            renderVideoList(list)
            listenVideoPlayer()
        } else {
            ElMessage.error(res.message)
        }
    }, function (err) {
        ElMessage.error(err)
    })
}

onMounted(() => {
    init()
})

</script>

<style scoped>

.pan-video-content {
    width: 100%;
    margin-top: 62px;
    display: block;
}

.pan-video-content .video-name-content {
    display: block;
    width: 100%;
    text-align: center;
    padding: 10px 0 0 0;
}

.pan-video-content .video-name-content .video-name {
    color: #409EFF;
    font-size: 35px;
    font-weight: bold;
    font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}

.pan-video-content .video-content {
    width: 100%;
    height: 100px;
    display: block;
    text-align: center;
}

.pan-video-content .video-content #r_pan_video_player {
    display: inline-block;
    width: 100%;
    height: 500px;
    line-height: 500px;
}

.pan-video-content .video-list-content {
    display: block;
    margin: 0 auto;
    width: 250px;
    height: 500px;
    line-height: 500px;
    overflow: hidden;
}

.pan-video-content .video-list-content .video-list {
    width: 100%;
    height: 100%;
    overflow: scroll;
}

.pan-video-content .video-list i {
    margin-right: 15px;
}

</style>
