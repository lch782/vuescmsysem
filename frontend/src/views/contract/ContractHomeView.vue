<template>
  <div class="content">
    <div class="flex mx-3 my-5">
      <div class="flex items-center w-24">
        <button @click="preBtnClick()">
          <img src="@/assets/images/left.png"/>
        </button>
      </div>
<!--      <div class="mx-2 border border-2 w-1/3" v-for="(summary, index) in this.homeNoticeSummary" :key="summary" v-show="index < 3">-->
<!--        <router-link :to="'/contract/notice/noticeDetailView/' + summary.seq">-->
<!--          <span v-html="summary.summary"></span>-->
<!--        </router-link>-->
<!--      </div>-->

        <div class="mx-2 border border-2 w-1/3 h-home_notice">
          <router-link :to="'/contract/notice/noticeDetailView/' + notice_seq_1">
            <span v-html="notice_summary_1" class="homeNotice"></span>
          </router-link>
        </div>
        <div class="mx-2 border border-2 w-1/3 h-home_notice">
          <router-link :to="'/contract/notice/noticeDetailView/' + notice_seq_2">
            <span v-html="notice_summary_2" class="homeNotice"></span>
          </router-link>
        </div><div class="mx-2 border border-2 w-1/3 h-home_notice">
        <router-link :to="'/contract/notice/noticeDetailView/' + notice_seq_3">
          <span v-html="notice_summary_3" class="homeNotice"></span>
        </router-link>
      </div>

      <div class="flex items-center w-24">
        <button @click="nextBtnClick()">
          <img src="@/assets/images/right.png"/>
        </button>
      </div>
    </div>
  </div>
</template>

<script>

import axios from 'axios'
import store from '@/store'

export default {
  name: 'ContractHomeView',
  components: {},
  data () {
    return {
      searchGroup: null,
      homeNoticeSummary: [],
      homeNoticeSummaryOrigin: [],
      homeNoticePath: [],
      noticeIndex: 0,
      location: window.location.host,
      iter: 0,
      iterMax: 0,
      notice_seq_1: null,
      notice_seq_2: null,
      notice_seq_3: null,
      notice_summary_1: null,
      notice_summary_2: null,
      notice_summary_3: null
    }
  },
  mounted () {
    if (store.state.account.userId === 'admin') {
      this.searchGroup = 'theWainning'
    } else if (store.state.account.companyCode === '10004') {
      this.searchGroup = 'theWainning'
    } else if (store.state.account.companyCode === '10005') {
      this.searchGroup = 'directing'
    } else if (store.state.account.companyCode === '10006' || store.state.account.companyCode === '10008') {
      this.searchGroup = 'retailSale'
    } else if (store.state.account.companyCode === '10007') {
      this.searchGroup = 'tote'
    } else if (store.state.account.companyCode === '10011') {
      this.searchGroup = 'wholeSale'
    }

    this.getHomeNotice()
  },
  methods: {
    getHomeNotice () {
      axios.get('/api/home/getHomeNotice', {
        params: {
          searchGroup: this.searchGroup
        }
      })
        .then((res) => {
          this.iterMax = res.data.length
          this.homeNoticeSummaryOrigin = res.data
          this.insertHomeViewArr(res.data, this.iter)
        })
    },
    insertHomeViewArr (resArr, iter) {
      for (let i = iter; i < resArr.length; i++) {
        if (i === iter) {
          this.notice_seq_1 = resArr[i].seq
          this.notice_summary_1 = resArr[i].summary
        }
        if (i === iter + 1) {
          this.notice_seq_2 = resArr[i].seq
          this.notice_summary_2 = resArr[i].summary
        }
        if (i === iter + 2) {
          this.notice_seq_3 = resArr[i].seq
          this.notice_summary_3 = resArr[i].summary
        }
      }
    },
    preBtnClick () {
      this.iter = this.iter - 1
      if (this.iter + 2 >= this.iterMax) {
        this.iter = this.iterMax - 4
      }
      if (this.iter < 0) {
        this.iter = 0
      }
      console.log(this.iter)
      this.getHomeNotice()
    },
    nextBtnClick () {
      this.iter = this.iter + 1
      if (this.iter + 2 >= this.iterMax) {
        this.iter = this.iterMax
      }
      console.log(this.iter)
      this.getHomeNotice()
    }
  }
}
</script>

<style scoped>
.homeNotice {
  text-align: center;
}

</style>
