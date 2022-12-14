<template>
  <div class="content">
    <div class="flex items-center h-12">
      <router-link to="/contract/taxBillViewContractDate"
                   class="w-96 h-10 mx-3 flex justify-center items-center border rounded-md bg-taxBill_header_positive">주문일 기준</router-link>
      <router-link to="/contract/taxBillPublishedDate"
                   class="w-96 h-10 mx-3 flex justify-center items-center border rounded-md bg-taxBill_header_negative">발행일 기준</router-link>
    </div>
    <div class="flex items-center">
      <div class="w-24 h-8 flex justify-center items-center mx-2 border border-content_text_box_border rounded-md bg-content_text_box_bg">
        기간
      </div>
      <date-picker/>
      <button @click="$store.commit('setDate','today'), searchRecentBtnClick()"
              class="w-28 border rounded-md border-date_preset_button_border p-1 m-1 bg-date_preset_button text-white">오늘</button>
      <button @click="$store.commit('setDate','week'), searchRecentBtnClick()"
              class="w-28 border rounded-md border-date_preset_button_border p-1 m-1 bg-date_preset_button text-white">최근 1주일</button>
      <button @click="$store.commit('setDate','month'), searchRecentBtnClick()"
              class="w-28 border rounded-md border-date_preset_button_border p-1 m-1 bg-date_preset_button text-white">최근 3개월</button>
    </div>
    <div>
      <p class="text-2xl ml-10 py-3">주문기간 기준 조회</p>
    </div>
    <div class="flex justify-center">
      <ag-grid-vue
        class="ag-theme-alpine"
        style="height: 500px; width: 1200px"
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
    </div>
    <div class="flex mx-3 my-3">
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
  name: 'TaxBillView',
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

    function rowspanOrdDate (params) {
      const showOrdDate = params.data.showOrdDate
      return rowData.value.filter((e) => e.ordDate === showOrdDate).length
    }
    // Each Column Definition results in one Column.
    const columnDefs = reactive({
      value: [
        {
          headerName: '주문일자',
          field: 'showOrdDate',
          rowSpan: rowspanOrdDate,
          cellRenderer: function (params) {
            const ordDate = new Date(params.data.ordDate)
            return ordDate.getFullYear() + '-' + (ordDate.getMonth() + 1) + '-' + ordDate.getDate()
          },
          cellClass: 'textCell'
        },
        {
          headerName: '주문일자_히든',
          field: 'ordDate',
          hide: true,
          cellRenderer: function (params) {
            const ordDate = new Date(params.data.ordDate)
            return ordDate.getFullYear() + '-' + (ordDate.getMonth() + 1) + '-' + ordDate.getDate()
          },
          cellClass: 'textCell'
        },
        {
          headerName: '주문금액',
          field: 'amt',
          rowSpan: rowspanOrdDate,
          // valueFormatter: params => params.data.amt.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell'
        },
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
          headerName: '과세유형',
          field: 'taxType',
          suppressMovable: true,
          maxWidth: 100,
          cellClass: 'textCell'
        },
        {
          headerName: '발행품목명',
          field: 'taxType',
          suppressMovable: true,
          maxWidth: 130,
          cellClass: 'textCell'
        },
        {
          headerName: '발행일자',
          field: 'publishedDate',
          // cellRenderer: function (params) {
          //   const publishedDate = new Date(params.data.publishedDate)
          //   return publishedDate.getFullYear() + '-' + (publishedDate.getMonth() + 1) + '-' + publishedDate.getDate()
          // },
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
        console.log(res.data)
      })
    })

    return {
      onGridReady,
      columnDefs,
      rowData,
      rowspanOrdDate,
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
  --ag-font-size: 11px;
  /*--ag-header-background-color: rgb(148, 198, 229, 0.19);*/
}
.bg-taxBill_header_positive {
  background-color: rgba(166,124,82,0.15);
}
.bg-taxBill_header_negative {
  background-color: rgba(179,179,179,0.52);
}
</style>
