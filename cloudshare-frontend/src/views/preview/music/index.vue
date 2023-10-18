<template>
    <div>
        <pan-simple-header/>
        <div class="pan-music-content">
            <div class="music-name-content">
                <p class="music-name">{{ musicName }}</p>
            </div>
            <el-divider></el-divider>
            <el-row>
                <el-col :span="18">
                    <div class="record-img-content">
                        <img class="record-img" src="@/assets/imgs/record.png">
                    </div>
                    <!--音频播放容器-->
                    <div class="music-content">
                        <audio id="r_pan_music_player" :src="musicShowPath" autoplay="true" controls="true"/>
                    </div>
                </el-col>
                <el-col :span="6">
                    <div class="music-list-content">
                        <el-menu class="music-list"
                                 :default-active="activeIndex"
                                 @select="selectMusic">
                            <el-menu-item v-for="(item, index) in musicList" :key="index" :index="item.id">
                                <i class="fa fa-music"></i>
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
const musicName = ref('')
const musicList = ref([])
const musicShowPath = ref('')
const activeIndex = ref('0')

const renderMusicList = (dataList) => {
    musicList.value = new Array()
    dataList.forEach((item, index) => {
        item.fileName = item.fileName.substring(0, item.fileName.lastIndexOf('.'))
        if (item.fileName.length > 15) {
            item.fileName = item.fileName.substring(0, 16) + '...'
        }
        if (item.id === route.params.id) {
            musicName.value = item.fileName
            musicShowPath.value = panUtil.getPreviewUrl(item.id)
        }
        musicList.value.push(item)
    })
    activeIndex.value = route.params.id
}

const selectNext = () => {
    let i = '',
        currentId = activeIndex.value
    musicList.value.some((item, index) => {
        if (item.id === currentId) {
            i = index
            return true
        }
    })
    if (i === musicList.value.length - 1) {
        return
    }
    let item = musicList.value[++i]
    musicName.value = item.fileName
    musicShowPath.value = panUtil.getPreviewUrl(item.id)
    activeIndex.value = item.id
}

const selectMusic = (index, indexPath) => {
    activeIndex.value = index
    musicList.value.some(item => {
        if (item.id === index) {
            musicName.value = item.fileName
            musicShowPath.value = panUtil.getPreviewUrl(item.id)
            return true
        }
    })
}

const listenMusicPlayer = () => {
    document.getElementById('r_pan_music_player').addEventListener('ended', () => {
        selectNext()
    }, false)
}

const init = () => {
    fileService.list({
        curDirectory: route.params.curDirectory,
        fileType: 'AUDIO'
    }, res => {
        if (res.code === 0) {
            const list = res.data.map(video => {
            // 将视频对象的 id 属性转换为字符串
            return { ...video, id: String(video.id) };
            });
            renderMusicList(list)
            listenMusicPlayer()
        } else {
            ElMessage.error(res.message)
        }
    }, res => {
        ElMessage.error(res.message)
    })
}

onMounted(() => {
    init()
})


</script>

<style scoped>
.pan-music-content {
    width: 100%;
    margin-top: 62px;
    display: block;
}

.pan-music-content .music-name-content {
    display: block;
    width: 100%;
    text-align: center;
    padding: 10px 0 0 0;
}

.pan-music-content .music-name-content .music-name {
    color: #409EFF;
    font-size: 35px;
    font-weight: bold;
    font-family: "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "微软雅黑", Arial, sans-serif;
}

.pan-music-content .record-img-content {
    display: inline-block;
    width: 100%;
    height: 100%;
    margin-top: 80px;
    text-align: center;
}

.pan-music-content .record-img-content .record-img {
    width: 300px;
}

.pan-music-content .music-content {
    width: 100%;
    height: 100px;
    display: block;
    margin-top: 60px;
    text-align: center;
    position: relative;
}

.pan-music-content .music-content #r_pan_music_player {
    display: inline-block;
    width: 100%;
    position: absolute;
    bottom: 10px;
    left: 0;
}

.pan-music-content .music-list-content {
    display: block;
    margin: 0 auto;
    width: 250px;
    height: 500px;
    line-height: 500px;
    overflow: hidden;
}

.pan-music-content .music-list-content .music-list {
    width: 100%;
    height: 100%;
    overflow: scroll;
}

.pan-music-content .music-list i {
    margin-right: 15px;
}

</style>
