import {computed, ref} from 'vue'
import {defineStore} from 'pinia'
import fileService from '@/api/file'
import {ElMessage} from 'element-plus'

export const useFileStore = defineStore('file', () => {

    const parentId = ref('')
    const curDirectory = ref('')
    const defaultParentId = ref('')
    const defaultCurDirectory = ref('')
    const fileList = ref([])
    const multipleSelection = ref([])
    const fileTypes = ref('-1')
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

    function setFileTypes(newFileTypes) {
        fileTypes.value = newFileTypes
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
        fileTypes.value = '-1'
        searchFlag.value = false
        searchKey.value = ''
        tableLoading.value = true
    }

    function loadFileList() {
        setTableLoading(true)
        if (searchFlag.value) {
            fileService.search({
                keyword: searchKey.value,
                fileTypes: '-1'
            }, res => {
                setFileList(res.data)
                setTableLoading(false)
            }, res => {
                setTableLoading(false)
                ElMessage.error(res.message)
            })
        } else {
            fileService.list({
                parentId: parentId.value,
                curDirectory: curDirectory.value,
                fileTypes: fileTypes.value
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
        fileTypes,
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
        setFileTypes,
        setSearchFlag,
        setSearchKey,
        setTableLoading,
        clear,
        loadFileList
    }
})
