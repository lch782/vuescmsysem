<template>
  <div class="content px-3 py-3">
    <div class="flex items-center my-3">
      <div>
        <button class="flex w-20 h-8 justify-center items-center border rounded-md bg-[#808080] text-white font-bold"
                @click="numseqSetBtnClick" v-if="userId === 'admin'">
          설정
        </button>
      </div>
      <div class="mx-3" v-if="userId === 'admin'">
        <table class="">
          <tr class="flex text-center gap-2">
            <td class="w-24 relative rounded-md bg-[#C7B299] text-white py-1">
              <input type="checkbox" value="theWainning" id="더웨이닝" v-model="searchGroup" class="mr-3" @click="groupBtnClick('theWainning')">
              <label for="더웨이닝" class="">더웨이닝</label>
            </td>
            <td class="w-24 relative rounded-md bg-[#C7B299] text-white py-1">
              <input type="checkbox" value="directing" id="디렉팅" v-model="searchGroup" class="mr-3" @click="groupBtnClick('directing')">
              <label for="디렉팅" class="w-full">디렉팅</label>
            </td>
            <td class="w-24 relative rounded-md bg-[#C7B299] text-white py-1">
              <input type="checkbox" value="tote" id="토트" v-model="searchGroup" class="mr-3" @click="groupBtnClick('tote')">
              <label for="토트" class="w-full">토트</label>
            </td>
            <td class="w-24 relative rounded-md bg-[#C7B299] text-white py-1">
              <input type="checkbox" value="wholeSale" id="도매" v-model="searchGroup" class="mr-3" @click="groupBtnClick('wholeSale')">
              <label for="도매" class="w-full">도매</label>
            </td>
            <td class="w-24 relative rounded-md bg-[#C7B299] text-white py-1">
              <input type="checkbox" value="retailSale" id="소매" v-model="searchGroup" class="mr-3" @click="groupBtnClick('retailSale')">
              <label for="소매" class="w-full">소매</label>
            </td>
          </tr>
        </table>
      </div>
      <div class="flex items-center justify-center border border-black w-20 h-8 mr-3 ml-auto text-center rounded-md bg-[#E6E6E6]">
        <p>기간</p>
      </div>
      <div class="flex">
        <DatePicker/>
      </div>
      <div class="ml-3">
        <button class="w-20 py-1 border rounded-md text-white font-bold bg-[#808080]"
                @click="$router.push(`/contract/notice/createNotice/${currentPage}`)"
                v-if="userId == 'admin'">작성하기</button>
      </div>
    </div>
    <div>
      <table class="w-full border">
        <thead >
        <tr class="border list_header">
          <th class="border w-8 text-center" v-if="userId == 'admin'" >홈화면</th>
          <th class="border w-16 text-center" v-if="userId == 'admin'" >번호</th>
          <th class="border w-28 text-center">날짜</th>
          <th class="border w-72 px-5 text-center">제목</th>
          <th class="border w-10 px-5 text-center " v-if="userId == 'admin'">작성자</th>
          <th class="border w-10 px-5 text-center " v-if="userId == 'admin'">조회수</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="i in noticeRes.list" :key="i">
          <td class="border text-center" v-if="userId == 'admin'">
            <div class="flex justify-center">
<!--              <input type="text" min="1" max="10" class="w-10 h-6 border numseq_case_input" v-bind:value="i.numseq_case"/>-->
              <input type="text" min="1" max="10" class="w-10 h-6 border numseq_case_input"
                     v-bind:value="initNumseqArr(i.numseq_case, i.seq)" v-on:change="inputNumseq($event.target.value, i.seq)"/>
            </div>
          </td>
          <td class="border text-center" v-if="userId == 'admin'">{{i.seq}}</td>
          <td class="border text-center text-center">{{i.regdate}}</td>
          <td class="border px-5 text-center" @click="$router.push(`/contract/notice/noticeDetailView/${i.seq}`)">{{i.title}}</td>
          <td class="border text-center text-center " v-if="userId == 'admin'">{{i.reguser}}</td>
          <td class="border text-center text-center " v-if="userId == 'admin'">{{i.selqty}}</td>
        </tr>
        </tbody>
      </table>
      <div class="mt-8">
        <ul class="text-center flex justify-center">
          <li class="ml-3 w-10" v-if="pagination > 9">
            <button @click="PreBtn()">이전</button>
          </li>
          <li v-for="i in slicePage" :key="i" class="ml-3 w-5" >
            <button @click="linkPageNum(i+pagination)">{{i+pagination}}</button>
          </li>
          <li class="border ml-3 w-10" v-if="pageNum - pagination > 10">
            <button @click="NextBtn()">다음</button>
          </li>
        </ul>
      </div>
      <div class="flex justify-center w-full h-12 mt-6 bg-[#F2F2F2]">
        <div class="mt-2">
          <select class="w-20 h-6" v-model="searchCondition" @change="getSearchOption">
            <option value="title">제목</option>
            <option value="content">내용</option>
          </select>
          <input type="text" placeholder="검색어 입력" v-model="searchWord" class="mx-3"/>
          <button class="w-16 h-8 text-white bg-[#4D4D4D]" @click="searchBtnClick">검색</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import DatePicker from '@/components/DatePicker'
import axios from 'axios'
import { useRoute } from 'vue-router'
import { ref, reactive, onMounted } from 'vue'
import router from '@/router'
import store from '@/store'

// import Content from '@/components/layouts/order/ContentOrder'
// import ModalView from '@/components/modal/ModalView'

export default {
  name: 'ContractNoticeView',

  components: { DatePicker },
  data () {
    return {
      // isModalViewd: false,
      // columnDefs: null,
      // rowData: null
    }
  },
  setup () {
    // datepicker 초기화
    store.commit('setDate', '')
    const route = useRoute()
    const currentPage = ref()
    const pageNum = ref()
    const pagination = ref(0)
    const noticeRes = reactive({ list: [] })
    const searchCondition = ref('title')
    const searchWord = ref('')
    const userId = store.state.account.userId
    // 페이지 출력 갯수 표현
    const slicePage = ref()
    const searchGroup = ref([])
    const numseqArr = ref([])
    const numseqArrUnique = ref([])
    const numseqArrOrigin = ref([])
    const numseqArrOriginUnique = ref([])
    const changedNumseqArr = ref([])
    const confirmStr = ref(null)

    onMounted(() => {
      store.commit('setDate', 'firstDate')
      currentPage.value = route.params.currentPage
      // 관리자일 경우 searchGroup을 기본적으로 더 웨이닝으로 넣어준다.
      if (userId === 'admin') {
        searchGroup.value.push('theWainning')
      } else if (store.state.account.companyCode === '10004') {
        searchGroup.value.push('theWainning')
      } else if (store.state.account.companyCode === '10005') {
        searchGroup.value.push('directing')
      } else if (store.state.account.companyCode === '10006' || store.state.account.companyCode === '10008') {
        searchGroup.value.push('retailSale')
      } else if (store.state.account.companyCode === '10007') {
        searchGroup.value.push('tote')
      } else if (store.state.account.companyCode === '10011') {
        searchGroup.value.push('wholeSale')
      }

      setTimeout(function () {
        axios.get('/api/notice/getNoticeList', {
          params: {
            page: currentPage.value,
            fromDate: store.state.fromDate,
            toDate: store.state.toDate,
            searchWord: searchWord.value,
            searchCondition: searchCondition.value,
            searchGroup: searchGroup.value[0],
            companyCode: store.state.account.companyCode
          }
        }).then((res) => {
          noticeRes.list = res.data
          console.log(res.data)
        })
        axios.get('/api/notice/getNoticeListPage', {
          params: {
            searchWord: searchWord.value,
            searchCondition: searchCondition.value,
            fromDate: store.state.fromDate,
            toDate: store.state.toDate
          }
        }).then((res) => {
          pageNum.value = res.data
          if (Math.floor(pageNum.value / 10) * 10 - currentPage.value >= 0) {
            slicePage.value = 10
          } else {
            slicePage.value = pageNum.value - Math.floor(pageNum.value / 10) * 10
          }

          if (currentPage.value % 10 === 0) {
            pagination.value = Math.floor((currentPage.value / 10)) * 10 - 10
          } else {
            pagination.value = Math.floor((currentPage.value / 10) + 1) * 10 - 10
          }
        })
      })
      store.commit('setNowticeNowPage', currentPage)
    })

    // const moveToDetailPage = () => {
    //   console.log()
    //   router.push('/contract/notice/noticeDetailView/' + i.seq, { params: { currentPage }})
    // }

    const getSearchOption = (res) => {
      searchCondition.value = res.target.value
    }

    const searchBtnClick = () => {
      resetNumseqArr()
      axios.get('/api/notice/getNoticeList', {
        params: {
          page: currentPage.value,
          fromDate: store.state.fromDate,
          toDate: store.state.toDate,
          searchWord: searchWord.value,
          searchCondition: searchCondition.value,
          searchGroup: searchGroup.value[0]
        }
      }).then((res) => {
        noticeRes.list = res.data
      })
      axios.get('/api/notice/getNoticeListPage', {
        params: {
          searchWord: searchWord.value,
          searchCondition: searchCondition.value,
          fromDate: store.state.fromDate,
          toDate: store.state.toDate
        }
      }).then((res) => {
        pageNum.value = res.data
      })
    }

    const linkPageNum = (page) => {
      resetNumseqArr()
      axios.get('/api/notice/getNoticeList', {
        params: {
          page: page,
          searchWord: searchWord.value,
          searchCondition: searchCondition.value,
          fromDate: store.state.fromDate,
          toDate: store.state.toDate,
          searchGroup: searchGroup.value[0]
        }
      }).then((res) => {
        currentPage.value = page
        noticeRes.list = res.data
        router.push(`/contract/notice/contractNotice/${currentPage.value}`)
      })
    }

    const NextBtn = () => {
      resetNumseqArr()
      slicePage.value = 0
      if (currentPage.value % 10 === 0) {
        pagination.value = Math.floor((currentPage.value / 10)) * 10
      } else {
        pagination.value = Math.floor((currentPage.value / 10) + 1) * 10
      }
      axios.get('/api/notice/getNoticeList', {
        params: {
          page: pagination.value + 1,
          searchWord: searchWord.value,
          searchCondition: searchCondition.value,
          fromDate: store.state.fromDate,
          toDate: store.state.toDate,
          searchGroup: searchGroup.value[0]
        }
      }).then((res) => {
        currentPage.value = pagination.value + 1
        noticeRes.list = res.data
        if (pageNum.value - currentPage.value > 10) {
          slicePage.value = 10
        } else {
          slicePage.value = pageNum.value - currentPage.value + 1
        }
        router.replace(`/contract/notice/contractNotice/${pagination.value + 1}`)
      })
    }

    const PreBtn = () => {
      resetNumseqArr()
      if (currentPage.value > 10) {
        pagination.value = Math.floor((currentPage.value / 10)) * 10 - 10
      }
      axios.get('/api/notice/getNoticeList', {
        params: {
          page: pagination.value + 1,
          searchWord: searchWord.value,
          searchCondition: searchCondition.value,
          fromDate: store.state.fromDate,
          toDate: store.state.toDate,
          searchGroup: searchGroup.value[0]
        }
      }).then((res) => {
        noticeRes.list = res.data
        currentPage.value = pagination.value + 1
        slicePage.value = 10
        router.replace(`/contract/notice/contractNotice/${pagination.value + 1}`)
      })
    }

    const groupBtnClick = (value) => {
      if (searchGroup.value[0] === value) {
        searchGroup.value[0] = ''
      } else {
        searchGroup.value[0] = value
      }
      searchBtnClick()
    }
    //
    // const compareLength = function (a, b) {
    //   if (a.value.length === b.value.length) {
    //     return true
    //   } else {
    //     return false
    //   }
    // }

    // 설정 버튼을 클릭 할 시에 원 배열과 비교해서 달라진 배열을 업데이트 해준다.
    const numseqSetBtnClick = () => {
      if (numseqArrOrigin.value.length === 0) {
        return
      }
      changedNumseqArr.value = []

      for (let i = 0; i < numseqArr.value.length; i++) {
        if (numseqArr.value[i][0] !== numseqArrOrigin.value[i][0]) {
          changedNumseqArr.value.push(numseqArr.value[i])
        }
      }
      checkIsSameNumseq(changedNumseqArr)
    }

    // 앞에 설정된 같은 우선순위가 있는지 검사한다.
    const checkIsSameNumseq = (changedNumseqArr) => {
      axios.post('/api/contract/notice/checkSameNumseq', {
        params: {
          changedNumseqArr: changedNumseqArr.value
        }
      }).then((res) => {
        console.log(res.data)
        if (res.data === 'Y') {
          if (confirm('이미 존재하는 우선순위 입니다 바꾸시겠습니까?')) {
            confirmStr.value = 'Y'
            executeNumseqChange()
            confirmStr.value = null
          } else {
            confirmStr.value = 'N'
            searchBtnClick()
            confirmStr.value = null
          }
        } else if (res.data === 'N') {
          executeNumseqChange()
        }
      })
    }

    const executeNumseqChange = () => {
      axios.post('/api/contract/notice/setNoticeNumseq', {
        params: {
          changedNumseqArr: changedNumseqArr.value
        }
      }).then((res) => {
        searchBtnClick()
      })
    }

    const resetNumseqArr = () => {
      numseqArr.value = []
      numseqArrOrigin.value = []
    }

    // 홈화면 우선순위를 담아준다.
    const initNumseqArr = (numseq, seq) => {
      const tmpArr = [numseq, seq]
      numseqArr.value.push(tmpArr)
      // numseqArrOrigin.value.push((tmpArr))

      return numseq
    }

    // 홈화면 공지 숫자가 바뀔때마다 배열을 검색하여 설정해줌 // 그전에 배열을 중복제거하여 깊은복사를 실행해 준다.
    const inputNumseq = (numseq, seq) => {
      if (numseqArrOrigin.value.length === 0) {
        numseqArrOrigin.value = JSON.parse(JSON.stringify(numseqArr.value))
      }
      for (const ns of numseqArr.value) {
        if (ns[1] === seq) {
          ns[0] = numseq
        }
      }
    }

    return {
      userId,
      pageNum,
      slicePage,
      noticeRes,
      currentPage,
      linkPageNum,
      // moveToDetailPage,
      numseqArr,
      numseqArrOrigin,
      numseqArrUnique,
      numseqArrOriginUnique,
      NextBtn,
      PreBtn,
      onMounted,
      initNumseqArr,
      resetNumseqArr,
      inputNumseq,
      executeNumseqChange,
      // compareLength,
      pagination,
      searchBtnClick,
      checkIsSameNumseq,
      getSearchOption,
      groupBtnClick,
      searchWord,
      searchGroup,
      numseqSetBtnClick,
      searchCondition
    }
  },
  computed: {
    getFromDate () {
      return this.$store.state.fromDate
    },
    getToDate () {
      return this.$store.state.toDate
    }
  }
}
</script>

<style scoped>
tbody tr:hover{
  background-color: #C7B299;
  opacity: 0.19;
  cursor: pointer;
}
thead .check { text-align: initial}
thead .subject { text-align: initial}

.list_header {
  background-color: rgb(148, 198, 229, 0.19);
}
.numseq_case_input {
  text-align: center;
}
/* Chrome, Safari, Edge, Opera */
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* Firefox */
input[type=number] {
  -moz-appearance: textfield;
}

</style>
