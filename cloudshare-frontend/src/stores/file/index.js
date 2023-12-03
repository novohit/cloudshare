import {computed, ref} from 'vue'
import {defineStore} from 'pinia'
import fileService from '@/api/file'
import {ElMessage} from 'element-plus'

export const useFileStore = defineStore('file', () => {

    const parentId = ref('')
    const curDirectory = ref('')
    const defaultParentId = ref('0')
    const defaultCurDirectory = ref('/')
    const fileList = ref([])
    const multipleSelection = ref([])
    const fileTypeList = ref([])
    const searchFlag = ref(false)
    const searchKey = ref('')
    const tableLoading = ref(true)

    function setParentId(newParentId) {
        parentId.value = newParentId
    }

    function refreshParentId() {
        parentId.value = defaultParentId.value
    }

    function setDefaultParentId(newDefaultParentId) {
        defaultParentId.value = newDefaultParentId
    }

    function setCurDirectory(newCurDirectory) {
        curDirectory.value = newCurDirectory
    }

    function refreshCurDirectory() {
        curDirectory.value = defaultCurDirectory.value
    }

    function setDefaultCurDirectory(newDefaultCurDirectory) {
        defaultCurDirectory.value = newDefaultCurDirectory
    }

    function setFileList(newFileList) {
        fileList.value = newFileList
    }

    function setMultipleSelection(newMultipleSelection) {
        multipleSelection.value = newMultipleSelection
    }

    function setFileTypeList(newFileTypeList) {
        fileTypeList.value = newFileTypeList
    }

    function setSearchFlag(newSearchFlag) {
        if (!newSearchFlag) {
            searchKey.value = ''
        }
        searchFlag.value = newSearchFlag
    }

    function setSearchKey(newSearchKey) {
        searchKey.value = newSearchKey
    }

    function setTableLoading(newTableLoading) {
        tableLoading.value = newTableLoading
    }

    function clear(state) {
        parentId.value = ''
        defaultParentId.value = ''
        curDirectory.value = ''
        defaultCurDirectory.value = ''
        fileList.value = new Array()
        multipleSelection.value = new Array()
        fileTypeList.value = []
        searchFlag.value = false
        searchKey.value = ''
        tableLoading.value = true
    }

    function loadFileList() {
        setTableLoading(true)
        if (searchFlag.value) {
            fileService.search({
                keyword: searchKey.value,
                fileTypeList: [],
                curDirectory: fileStore.curDirectory,
                parentId: fileStore.parentId
            }, res => {
                setFileList(res.data)
                setTableLoading(false)
            }, res => {
                setTableLoading(false)
                ElMessage.error(res.message)
            })
        } else {
            console.log("refresh", curDirectory.value)
            fileService.list({
                parentId: parentId.value,
                curDirectory: curDirectory.value,
                fileTypeList: fileTypeList.value
            }, res => {
                setTableLoading(false)
                setFileList(res.data)
            }, res => {
                setTableLoading(false)
                ElMessage.error(res.message)
            })
        }
    }

    return {
        parentId,
        defaultParentId,
        curDirectory,
        defaultCurDirectory,
        fileList,
        multipleSelection,
        fileTypeList,
        searchFlag,
        searchKey,
        tableLoading,
        setParentId,
        refreshParentId,
        setDefaultParentId,
        setCurDirectory,
        refreshCurDirectory,
        setDefaultCurDirectory,
        setFileList,
        setMultipleSelection,
        setFileTypeList,
        setSearchFlag,
        setSearchKey,
        setTableLoading,
        clear,
        loadFileList
    }
})
