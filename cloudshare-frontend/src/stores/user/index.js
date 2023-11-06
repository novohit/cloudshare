import {ref} from 'vue'
import {defineStore} from 'pinia'

export const useUserStore = defineStore('user', () => {

    const username = ref('')
    const avatar = ref('')
    const plan = ref('')
    const total = ref(0)
    const used = ref(0)

    function setUsername(newUsername) {
        username.value = newUsername
    }

    function setAvatar(newAvatar) {
        avatar.value = newAvatar
    }

    function setPlan(newPlan,newTotal,newUsed){
        plan.value = newPlan
        total.value = newTotal
        used.value = newUsed
    }

    function clear() {
        username.value = ''
        avatar.value = ''
    }

    return {username, avatar, plan,total,used, setUsername, setAvatar, setPlan, clear}
})
