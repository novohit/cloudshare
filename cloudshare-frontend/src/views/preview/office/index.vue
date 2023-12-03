<template>
    <div>
        <pan-simple-header/>
        <div class="office-iframe-content">
            <iframe class="office-iframe" :src="showPath" frameborder="0"/>
        </div>
    </div>
</template>

<script setup>

import PanSimpleHeader from '@/components/simple-header/index.vue'
import panUtil from '@/utils/common'
import {useRoute} from 'vue-router'
import {onMounted, ref} from 'vue'

const showPath = ref('')
const route = useRoute()

const init = () => {
    let id = route.params.fileId
    // 这里用 preview url 会预览不了 The URL of the original file is not valid or the document is not publicly accessible.
    showPath.value = 'https://view.officeapps.live.com/op/view.aspx?src=http://cloud.novohit.top' + escape(panUtil.getDownloadUrl(id))
}

onMounted(() => {
    init()
})

</script>

<style scoped>

.office-iframe-content {
    width: 100%;
    margin-top: 62px;
    display: block;
    text-align: center;
}

.office-iframe {
    width: 100%;
    height: 1200px;
}

</style>
