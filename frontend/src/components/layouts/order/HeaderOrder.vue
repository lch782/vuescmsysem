<template>
  <div id="orderHeader" class="flex items-end h-20 w-full bg-[#F2F2F2]">
    <div class="logo flex items-end" @click="$router.push('/order/orderHome')">
      <div class="w-20 h-20 p-0 items-center"><img src="/favicon.ico"></div>
        <h3 class="text-6xl font-bold text-[#808080]">SCM</h3>
        <p class="flex text-5xl text-[#808080]">System</p>
    </div>
    <div class="flex w-full gap-3">
      <button class="h-10 w-28 bg-[#B3B3B3] active:bg-[#E6E6E6] rounded-md ml-36" @click="$router.push('/order/checkOrder')">상품발주</button>
      <button class="h-10 w-28 bg-[#B3B3B3] active:bg-[#E6E6E6] rounded-md" @click="$router.push('/order/registDelivery')">납품등록</button>
      <button class="h-10 w-28 bg-[#B3B3B3] active:bg-[#E6E6E6] rounded-md" @click="$router.push('/order/confirmDelivery')">납품조회</button>
      <button class="h-10 w-28 bg-[#B3B3B3] active:bg-[#E6E6E6] rounded-md" @click="$router.push('/order/tradingStatement')">거래명세서</button>
      <button class="h-10 w-28 bg-[#B3B3B3] active:bg-[#E6E6E6] rounded-md" @click="$router.push('/order/salesPerformance')">매출실적</button>
    </div>
    <div class="flex justify-end items-end w-full">
      <p class="mr-3">[ {{ this.$store.state.account.custName }} ]</p>
      <button class="w-24 h-8 bg-[#333333] hover:bg-[#808080] text-white rounded-md" @click="logout()">로그아웃</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import router from '@/router'
import store from '@/store'
import { useRoute } from 'vue-router'
import { watch } from 'vue'

export default {
  name: 'Header',
  data () {
    return {
      bizno: null,
      custName: null
    }
  },
  methods: {
    checkToken () {
      axios.post('/api/account/check').then((res) => {
        if (res.data.bizno === undefined) {
          router.push('/')
        }
        store.commit('setAccount', res.data)
        if (res.data.custType !== '1' && res.data.custType !== '3') {
          localStorage.clear()
          router.push('/')
        }
      })
    },
    unLoadEvent: function (event) {
      // if (this.isLeaveSite) return
      this.checkToken()
      event.preventDefault()
      event.returnValue = ''
    }
  },
  setup () {
    const logout = () => {
      store.commit('setAccount', 0)
      sessionStorage.removeItem('bizno')
      router.push({ path: '/order/login' })
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
.logo:hover {
  cursor: pointer;
}
</style>
