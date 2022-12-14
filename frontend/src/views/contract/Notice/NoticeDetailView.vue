<template>
  <div class="content">
    <table class="flex m-1 w-full" v-for="i in noticeContent" :key="i">
      <tr class="flex text-center">
        <th class="w-28">공지그룹</th>
        <td class="w-52 border relative">
          <div>{{ i.compcode }}</div>
        </td>
<!--        <td class="flex ml-10">-->
<!--          <div class="mr-3 font-bold">우선순위</div>-->
<!--          <div class="border w-16">{{i.numseq}}</div>-->
<!--        </td>-->
      </tr>
    </table>
    <table class="border w-11/12 m-2" v-for="i in noticeContent" :key="i">
      <tr class="border">
        <th class="w-28 border bg-[#C7B299]">제목</th>
        <td class="border" colspan="2">
          <div class="w-full">{{i.title}}</div>
        </td>
      </tr>
      <tr class="border h-40" v-if="userId == 'admin'">
        <th class="border bg-[#C7B299]">홈화면</th>
        <td class="border ">
          <div class="w-full"><span v-html="i.summary"></span></div>
        </td>
      </tr>
      <tr class="border h-96">
        <th class="border bg-[#C7B299]">본문</th>
        <td class="border">
          <div class="w-full"><span v-html="i.contents"></span></div>
        </td>
      </tr>
    </table>
    <div class="w-11/12 m-2 text-right">
      <button class="h-8 border w-16 mr-1 rounded-md bg-blue-300" @click="$router.push(`/contract/notice/updateNotice/${currentPage}/${seq}`)" v-if="userId == 'admin'">수정하기</button>
      <button class="h-8 border w-16 mr-3 rounded-md bg-red-300" @click="noticeDelete" v-if="userId == 'admin'">삭제하기</button>
      <button class="h-8 border w-16 rounded-md bg-gray-300" @click="$router.push(`/contract/notice/contractNotice/${currentPage}`)">목록으로</button>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { useRoute } from 'vue-router'
import router from '@/router'
import store from '@/store'

export default {
  name: 'NoticeDetailView',
  data () {
    return {
      seq: useRoute().params.seq,
      currentPage: store.getters.getNoticeNowPage,
      userId: store.state.account.userId,
      noticeContent: ''
    }
  },
  mounted () {
    console.log(this.seq)
    axios.get('/api/notice/getNoticeContent', {
      params: {
        seq: this.seq
      }
    }).then((res) => {
      this.noticeContent = res.data
      console.log(this.noticeContent)
    })
  },
  methods: {
    noticeDelete () {
      axios.delete('/api/notice/deleteNotice', {
        params: {
          seq: this.seq
        }
      }).then((res) => {
        alert('삭제되었습니다.')
        router.push(`/contract/notice/contractNotice/${this.currentPage}`)
      }).catch((err) => {
        console.log(err)
      })
    }
  }
}
</script>

<style scoped>

</style>
