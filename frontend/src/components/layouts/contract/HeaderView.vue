<template>
    <div class="flex w-full bg-white pl-52 pr-64">
      <div class="h-28 w-72 flex items-center mt-14 mb-4">
        <div>
          <img src="@/assets/images/layout/더웨이닝커피로고.png" alt="" width="200"/>
        </div>
      </div>
      <div class="flex w-full items-end mb-6">
        <div class="flex w-full text-xl font-noto_santos_r font_letter_spc font_word_spc text-header_welcome">
          <p class="underline underline-offset-4">&nbsp;&nbsp;{{custName}}&nbsp;&nbsp;</p><p>&nbsp; 님 반갑습니다.</p>
        </div>
        <div class="w-full flex">
          <button class="bg-logout_btn text-white w-20 h-8 rounded-lg noto_santos_b font-medium_bold ml-auto" @click="logout()">로그아웃</button>
        </div>
      </div>
    </div>
</template>

<script>
import store from '@/store'
import router from '@/router'
import { useRoute } from 'vue-router'
import { watch } from 'vue'
// import VueCookies from 'vue-cookies'
import axios from 'axios'

export default {
  name: 'HeaderView',
  data () {
    return {
      isLeaveSite: false,
      accountInfo: null,
      bizno: null,
      custName: null
    }
  },
  computed: {
    childAccount () {
      return this.$store.getters.getAccount
    }
  },
  methods: {
    checkToken () {
      axios.post('/api/account/check').then((res) => {
        if (res.data.bizno === undefined) {
          router.push('/')
        }
        store.commit('setAccount', res.data)
        // console.log(res.data.custType)
        if (res.data.custType !== '2' && res.data.custType !== '3') {
          localStorage.clear()
          router.push('/')
        }
      })
    },
    unLoadEvent: function (event) {
      // if (this.isLeaveSite) return
      this.$store.commit('resetAgRidReg')
      this.checkToken()
      event.preventDefault()
      event.returnValue = ''
    }
  },
  setup () {
    const logout = () => {
      store.commit('setAccount', 0)
      sessionStorage.removeItem('bizno')
      router.push({ path: '/contract/login' })
    }
    return { logout }
  },

  mounted () {
    this.bizno = store.state.account.bizno
    this.custName = store.state.account.custName
    window.addEventListener('beforeunload', this.unLoadEvent)
    if (store.state.account.bizno === undefined || store.state.account.bizno === null) {
      this.checkToken()
    }
    // 감시하는 메소드
    const check = () => {
      this.checkToken()
    }
    // 라우트가 바뀔때마다 감시함
    const route = useRoute()
    watch(route, () => {
      check()
    })
    this.accountInfo = this.childAccount
  },
  beforeUnmount () {
    window.removeEventListener('beforeunload', this.unLoadEvent)
  }
}
</script>

<style scoped>

</style>
