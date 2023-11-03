import {ref} from 'vue'
import {defineStore} from 'pinia'

export const useUserStore = defineStore('user', () => {

    const username = ref('')
    const avatar = ref('')

    function setUsername(newUsername) {
        username.value = newUsername
    }

    function setAvatar(newAvatar) {
        avatar.value = newAvatar
    }

    function clear() {
        username.value = ''
        avatar.value = ''
    }

    return {username, avatar, setUsername, setAvatar, clear}
})
