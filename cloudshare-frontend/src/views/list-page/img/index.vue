<template>
    <div>
        <div class="button-search-content">
            <file-button-group :button-array="buttonArray"/>
            <search/>
        </div>
        <file-table/>
    </div>
</template>

<script setup>

import FileButtonGroup from '@/components/file-button-group/index.vue'
import Search from '@/components/search/index.vue'
import FileTable from '@/components/file-table/index.vue'
import {onMounted, ref} from 'vue'
import {useFileStore} from '@/stores/file'

const fileStore = useFileStore()

const buttonArray = ref(['upload', 'download', 'delete', 'rename', 'share', 'copy', 'transfer'])

onMounted(() => {
    fileStore.setSearchFlag(false)
    fileStore.refreshParentId()
    fileStore.refreshCurDirectory()
    fileStore.setFileTypeList(['IMAGE'])
    fileStore.loadFileList()
})
</script>

<style scoped>

.button-search-content {
    display: flex;
    width: 100%;
}

</style>