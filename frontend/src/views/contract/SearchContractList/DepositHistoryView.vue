<template>
  <div class="content">
<!--    <p class="text-xl">입금 내역서</p>-->
    <div class="flex items-center h-12 mt-10">
      <div class="w-24 h-8 flex justify-center items-center mx-2 border border-content_text_box_border rounded-md bg-content_text_box_bg">
        <p>기간</p>
      </div>
      <date-picker/>
      <button @click="$store.commit('setDate','today'), searchRecentBtnClick()"
              class="w-28 border rounded-md border-date_preset_button_border p-1 m-1 bg-date_preset_button text-white">오늘</button>
      <button @click="$store.commit('setDate','week'), searchRecentBtnClick()"
              class="w-28 border rounded-md border-date_preset_button_border p-1 m-1 bg-date_preset_button text-white">최근 1주일</button>
      <button @click="$store.commit('setDate','month'), searchRecentBtnClick()"
              class="w-28 border rounded-md border-date_preset_button_border p-1 m-1 bg-date_preset_button text-white">최근 3개월</button>
    </div>
    <div class="flex items-center mb-1">
      <div class="flex items-center justify-center rounded-md border border-content_text_box_border bg-content_text_box_bg w-24 h-8 mx-2"
           @click="searchContractList">
        <p>검색</p>
        <img src="@/assets/images/돋보기.png" alt="" width="15">
      </div>
      <select class="w-56 h-8 mx-1 border">
        <option selected>검색옵션</option>
        <option >주문서번호</option>
        <option >상품코드</option>
        <option >상품명</option>
        <option >규격</option>
        <option >단가</option>
        <option >수량</option>
        <option >공급가액</option>
        <option >부가세액</option>
        <option >결제금액</option>
      </select>
      <input type="text" class="border rounded border-black p-1 m-1 w-search_box" placeholder="검색어 입력" @keyup.enter="searchContractList" v-model="searchWord"/>
<!--      <button @click="searchContractList" class="border rounded border-black p-1 m-1">검색</button>-->
    </div>
    <ag-grid-vue
      class="ag-theme-alpine mx-3"
      style="height: 500px"
      :suppressRowTransform="true"
      :columnDefs="columnDefs.value"
      :rowData="rowData.value"
      :defaultColDef="defaultColDef"
      rowSelection="multiple"
      animateRows="true"
      @cell-clicked="cellWasClicked"
      @grid-ready="onGridReady"
    >
    </ag-grid-vue>
    <div class="flex">
      <div class="flex items-center ml-auto">
        <p>선택한 주문서 출력하기</p>
      </div>
      <button class="w-20 h-8 border">PDF</button>
      <button class="w-20 h-8 border">엑셀</button>
    </div>
  </div>
</template>
<script>
import { AgGridVue } from 'ag-grid-vue3' // the AG Grid Vue Component
import { reactive, onMounted, ref } from 'vue'
import 'ag-grid-community/styles/ag-grid.css' // Core grid CSS, always needed
import 'ag-grid-community/styles/ag-theme-alpine.css' // Optional theme CSS
import DatePicker from '@/components/DatePicker'
import axios from 'axios'

export default {
  name: 'DepositHistoryView',
  components: {
    AgGridVue,
    DatePicker
  },
  setup () {
    const gridApi = ref(null) // Optional - for accessing Grid's API

    // Obtain API from grid's onGridReady event
    const onGridReady = (params) => {
      gridApi.value = params.api
    }
    const rowData = reactive({
      value: []
    })

    // Each Column Definition results in one Column.
    const columnDefs = reactive({
      value: [
        {
          headerName: '거래내역',
          field: 'orderHistory',
          suppressMovable: true,
          cellClass: 'textCell',
          children: [
            { headerName: '거래일자', field: 'contractDate', suppressMovable: true },
            { headerName: '주문', field: 'contractAmount', suppressMovable: true },
            { headerName: '출고', field: 'release', suppressMovable: true },
            { headerName: '입금', field: 'deposit', suppressMovable: true },
            { headerName: '입금대상금액', field: 'depositAmount', suppressMovable: true }
          ]
        }
      ]
    })

    // DefaultColDef sets props common to all Columns
    const defaultColDef = {
      sortable: true,
      flex: 1
    }
    onMounted(() => {
      const nowDate = new Date()
      const nowYear = nowDate.getFullYear()
      const nowMonth = nowDate.getMonth() + 1
      const nowDay = nowDate.getDate()
      const nowDateString = String(nowYear) + '-' + nowMonth + '-' + nowDay

      axios.get('/api/searchContract/searchContractList', {
        params: {
          fromDate: nowDateString,
          toDate: nowDateString,
          word: '',
          confirmYn: ''
        }
      }).then((res) => {
        rowData.value = res.data
      })
    })

    return {
      onGridReady,
      columnDefs,
      rowData,
      defaultColDef,
      cellWasClicked: (event) => { // Example of consuming Grid Event
        if (event.event.target.nodeName === 'BUTTON') {
          console.log('배송조회 클릭')
        }
      },
      deselectRows: () => {
        gridApi.value.deselectAll()
      }
    }
  },
  mounted () {
    this.$store.commit('setDate', 'today')
  },
  watch: {
    confirmYn: function (data) {
      this.searchContractList()
    }
  },
  // store에 있는 state에 접근하기위해 선언
  computed: {
    getFromDate () {
      return this.$store.state.fromDate
    },
    getToDate () {
      return this.$store.state.toDate
    },
    setFromToDate () {
      let nowDate = new Date()
      nowDate = nowDate.getFullYear() + '-' +
        ('0' + (1 + nowDate.getMonth())).slice(-2) + '-' +
        ('0' + nowDate.getDate()).slice(-2)
      if (this.$store.state.datetype === 'today') {
        this.$store.commit('setFromDate', nowDate)
        this.$store.commit('setToDate', nowDate)
      } else if (this.$store.state.datetype === 'week') {
        const weekAgo = new Date().getFullYear() + '-' +
          ('0' + (1 + new Date().getMonth())).slice(-2) + '-' +
          ('0' + (new Date().getDate() - 7)).slice(-2)
        this.$store.commit('setFromDate', weekAgo)
        this.$store.commit('setToDate', nowDate)
      } else if (this.$store.state.datetype === 'month') {
        const monthAgo = new Date().getFullYear() + '-' +
          ('0' + (new Date().getMonth() - 2)).slice(-2) + '-' +
          ('0' + new Date().getDate()).slice(-2)
        this.$store.commit('setFromDate', monthAgo)
        this.$store.commit('setToDate', nowDate)
      }
      return 'ok'
    }
  },
  methods: {
    async searchContractList () {
      axios.get('/api/searchContract/searchContractList', {
        params: {
          fromDate: this.getFromDate,
          toDate: this.getToDate,
          word: this.searchWord,
          confirmYn: this.confirmYn
        }
      }).then((res) => {
        this.rowData.value = res.data
      })
    },
    searchRecentBtnClick () {
      const test = this.setFromToDate
      if (test === 'ok') {
        this.searchContractList()
      }
    }
  },
  data () {
    return {
      getDate: new Date(),
      searchWord: '',
      confirmYn: ''
    }
  }
}

</script>
<style scoped>
.numberCell {
  text-align: right
}
.textCell {
  text-align: center
}
.ag-header-cell-label {
  justify-content: center
}
.ag-theme-alpine {
  --ag-header-background-color: rgba(166, 124, 82, 0.15);
}
.ag-header-group-cell-label {
  justify-content: center;
}
</style>
