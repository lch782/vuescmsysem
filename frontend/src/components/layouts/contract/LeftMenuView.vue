<template>
  <div class="leftMenu bg-category_box font-noto_santos_l text-lg border-t border-category_hover">
    <div class="pl-5 py-2 mb-1 mt-8 hover:bg-category_hover hover:font-noto_santos_b hover:font-semibold">
      <router-link to="/contract/contractHome">HOME</router-link>
      <br/>
    </div>
    <div class="pl-5 py-2 hover:bg-category_hover hover:font-noto_santos_b hover:font-semibold">
      <router-link to="/contract/recontract">재주문</router-link>
      <br/>
    </div>
    <div class="py-2">
      <RegistContractMenu class="">
        <template v-slot:title>
            <div class="">주문하기</div>
        </template>
        <template v-slot:content>
          <div v-for="i in MiddleTypeDiv" :key="i" class="pl-5 py-1 hover:bg-small_category_hover hover:font-noto_santos_b hover:font-semibold">
            <router-link to="/contract/registContract" @click="changeMiddleDiv(i.dcode)">{{'▪ ' + i.dcodename}}</router-link>
          </div>
        </template>
      </RegistContractMenu>
    </div>
    <div class="pl-5 py-2 hover:bg-category_hover hover:font-noto_santos_b hover:font-semibold">
      <router-link to="/contract/contractBasket">장바구니</router-link>
      <br/>
    </div>
    <div class="py-2">
      <SearchContractMenu class="">
        <template v-slot:title>
          <span class="">거래조회</span>
        </template>
        <template v-slot:content>
          <div class="pl-5 py-1 hover:bg-small_category_hover hover:font-noto_santos_b hover:font-semibold">
            <router-link to="/contract/searchContractList">▪ 주문서</router-link>
          </div>
          <div class="pl-5 py-1 hover:bg-small_category_hover hover:font-noto_santos_b hover:font-semibold">
            <router-link to="/contract/depositHistoryView">▪ 입금내역</router-link>
          </div>
          <div class="pl-5 py-1 hover:bg-small_category_hover hover:font-noto_santos_b hover:font-semibold">
            <router-link to="/contract/taxBillViewContractDate">▪ 세금계산서</router-link>
          </div>
        </template>
      </SearchContractMenu>
    </div>
    <div class="pl-5 py-2 hover:bg-category_hover hover:font-noto_santos_b hover:font-semibold">
      <router-link to="/contract/notice/contractNotice/1">공지사항</router-link>
    </div>

  </div>
</template>
<script>
import RegistContractMenu from '@/components/layouts/contract/RegistContractMenu'
import SearchContractMenu from '@/components/layouts/contract/SearchContractMenu'
import axios from 'axios'
export default {
  name: 'LeftMenuView',
  data () {
    return {
      MiddleTypeDiv: null
    }
  },
  components: { SearchContractMenu, RegistContractMenu },
  methods: {
    getMiddleCate () {
      axios.get('/api/contract/getMiddleCategory')
        .then((res) => {
          this.MiddleTypeDiv = res.data
        })
    },
    changeMiddleDiv (div) {
      this.$store.commit('resetAgRidReg')
      this.$store.commit('setSelectedMiddleDiv', div)
    }
  },
  mounted () {
    this.getMiddleCate()
  }
}
</script>

<style scoped>

</style>
