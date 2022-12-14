<template>
  <div class="content">
<!--    <p class="text-xl">거래조회</p>-->
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
      <div class="border mx-2 w-48 flex justify-center">
        <input type="radio" name="confirmYn" id="confirmAll" value="" checked="checked" v-model="confirmYn"><label class="leading-9 mx-1" for="confirmAll">전체</label>
        <input type="radio" name="confirmYn" id="confirmY" value="Y" v-model="confirmYn"><label class="leading-9 mx-1" for="confirmY">확정</label>
        <input type="radio" name="confirmYn" id="confirmN" value="N" v-model="confirmYn"><label class="leading-9 mx-1" for="confirmN">미확정</label>
      </div>
    </div>
    <div class="flex items-center mb-1">
        <div class="flex items-center justify-center rounded-md border border-content_text_box_border bg-content_text_box_bg w-24 h-8 mx-2"
             @click="searchContractList">
          <p>검색&nbsp;</p>
          <img src="@/assets/images/돋보기.png" alt="" width="15">
        </div>
      <select class="w-56 h-8 mx-1 border" v-model="searchType">
        <option value="itemName">상품명</option>
        <option value="contractNo">주문번호</option>
        <option value="itemCode">상품코드</option>
        <option value="spec">규격</option>
        <option value="dan">단가</option>
        <option value="qty">수량</option>
        <option value="supplyPrice">공급가액</option>
        <option value="vat">부가세액</option>
        <option value="amount">결제금액</option>
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
    <div class="flex my-3">
      <div class="flex items-center mx-1 ml-auto">
        <p>선택한 주문서 출력하기</p>
      </div>
      <button class="w-20 h-8 mx-1 border">PDF</button>
      <button class="w-20 h-8 mx-1 border">엑셀</button>
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
import store from '@/store'

export default {
  name: 'SearchContractListView',
  components: {
    AgGridVue,
    DatePicker
  },
  setup () {
    // datepicker 초기화
    store.commit('setDate', '')
    const gridApi = ref(null) // Optional - for accessing Grid's API
    let selectedOrderData = reactive(null)
    const searchType = ref('itemName')

    // Obtain API from grid's onGridReady event
    const onGridReady = (params) => {
      gridApi.value = params.api
    }
    const rowData = reactive({
      value: []
    })
    // 주문번호 로우 스팬
    function rowSpanOrdNo (params) {
      const showOrderNo = params.data.showOrderNo
      return rowData.value.filter((e) => e.orderNo === showOrderNo).length
    }

    function rowSpanConfirmYn (params) {
      const showConfirmYn = params.data.showConfirmYn
      return rowData.value.filter((e) => e.confirmYn === showConfirmYn).length
    }

    // Each Column Definition results in one Column.
    const columnDefs = reactive({
      value: [
        {
          headerCheckboxSelection: true,
          showDisabledCheckboxes: true,
          rowSpan: rowSpanOrdNo,
          field: 'checkBox',
          cellRenderer: function (params) {
            if (params.data.checkBox === 'visible') {
              return '<div><input type="checkbox"/></div>'
            } else if (params.data.checkBox === 'invisible') {
              return null
            }
          },
          // checkboxSelection: true,
          maxWidth: 50
        },
        {
          headerName: '주문일자',
          field: 'ordDate',
          cellRenderer: function (params) {
            const ordDate = new Date(params.data.ordDate)
            return ordDate.getFullYear() + '-' + (ordDate.getMonth() + 1) + '-' + ordDate.getDate()
          },
          cellClass: 'textCell'
        },
        {
          headerName: '주문번호',
          field: 'showOrderNo',
          rowSpan: rowSpanOrdNo,
          cellClass: 'textCell'
        },
        { headerName: '주문번호_히든', field: 'orderNo', cellClass: 'textCell', hide: true },
        { headerName: '상품명', field: 'itemName', cellClass: 'textCell' },
        { headerName: '규격', field: 'spec', cellClass: 'textCell', maxWidth: 100 },
        { headerName: '단위', field: 'unit', cellClass: 'textCell', maxWidth: 75 },
        {
          headerName: '단가',
          field: 'dan',
          valueFormatter: params => localPrice(params.data.dan),
          cellClass: 'numberCell'
        },
        { headerName: '주문수량', field: 'qty', cellClass: 'textCell' },
        {
          headerName: '공급가액',
          field: 'amount',
          suppressMovable: true,
          valueFormatter: params => params.data.amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 130,
          cellClass: 'textCell'
        },
        {
          headerName: '부가세액',
          field: 'vat',
          suppressMovable: true,
          valueFormatter: params => {
            if (params.data.vat != null) {
              params.data.vat.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
            } else if (params.data.vat === null) {
              return 0
            }
          },
          maxWidth: 130,
          cellClass: 'textCell'
        },
        {
          headerName: '합계금액',
          field: 'paymentAmt',
          suppressMovable: true,
          valueFormatter: params => params.data.paymentAmt.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 130,
          cellClass: 'textCell'
        },
        {
          headerName: '결제금액',
          field: 'totalAmt',
          // valueFormatter: params => params.data.amt.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell'
        },
        {
          headerName: '확정여부',
          field: 'confirmYn',
          rowSpan: rowSpanOrdNo,
          cellRenderer: function (params) {
            if (params.data.confirmYn === 'Y') {
              return '확정'
            } else if (params.data.confirmYn === 'N') {
              return '미확정'
            } else if (params.data.confirmYn === '') {
              return ''
            }
          },
          cellClass: 'textCell'
        },
        {
          headerName: '수정/취소',
          field: 'modiCancle',
          rowSpan: rowSpanOrdNo,
          cellRenderer: function (params) {
            if (params.data.modiCancle === 'visible') {
              return '<div class="flex items-center"><button class="flex items-center w-14 h-8 bg-[#0071BC] text-white rounded-md">수정/취소</button></div>'
            } else if (params.data.modiCancle === 'inVisible') {
              return null
            }
          },
          cellClass: 'textCell'
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
        // console.log(res.data)
      })
    })

    // 주문 조회
    function searchContractListSetup () {
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
        console.log(res.data)
      })
    }
    // 주문 수정 취소
    function contractOrderCancel () {
      console.log(selectedOrderData)
      console.log(selectedOrderData[0].orderNo)
      axios.post('/api/searchContract/contractOrderCancel', {
        params: {
          ordNo: selectedOrderData[0].orderNo,
          userId: store.state.account.userId
        }
      }).then((res) => {
        console.log(res.status)
        searchContractListSetup()
      })
    }

    return {
      onGridReady,
      columnDefs,
      selectedOrderData,
      rowData,
      searchType,
      searchContractListSetup,
      rowSpanConfirmYn,
      defaultColDef,
      contractOrderCancel,
      cellWasClicked: (event) => { // Example of consuming Grid Event
        // 수정취소 의 경우
        if (event.column.colId === 'modiCancle') {
          selectedOrderData = gridApi.value.getSelectedRows()
          contractOrderCancel()
        }
        // const data = gridApi.value.getSelectedRows()
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
        console.log(res.data)
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

function localPrice (params) {
  if (params != null) {
    return params.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
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
  --ag-font-size: 11px;
  --ag-header-background-color: #F2F2F2;
}
</style>
