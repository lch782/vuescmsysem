<template>
  <div class="content">
    <div class="flex h-12 items-center mt-8">
      <div class="w-24 h-8 flex justify-center items-center mx-2 border border-content_text_box_border rounded-md bg-content_text_box_bg">
        <p>기간</p>
      </div>
      <date-picker/>
<!--      <button @click="$store.commit('setDate','today'),searchRecentBtnClick()" class="border rounded-md border-black p-1 m-1">오늘</button>-->
      <button @click="$store.commit('setDate','week'), searchRecentBtnClick()"
              class="w-28 border rounded-md border-date_preset_button_border p-1 m-1 bg-date_preset_button text-white">최근 1주일</button>
      <button @click="$store.commit('setDate','month_1'), searchRecentBtnClick()"
              class="w-28 border rounded-md border-date_preset_button_border p-1 m-1 bg-date_preset_button text-white">최근 1개월</button>
      <button @click="$store.commit('setDate','month_3'), searchRecentBtnClick()"
              class="w-28 border rounded-md border-date_preset_button_border p-1 m-1 bg-date_preset_button text-white">최근 3개월</button>
    </div>
    <div class="flex items-center mb-1">
      <div class="flex items-center justify-center rounded-md border border-content_text_box_border bg-content_text_box_bg w-24 h-8 mx-2"
           @click="searchReContractList">
        <p>검색&nbsp;</p>
        <img src="@/assets/images/돋보기.png" alt="" width="15">
      </div>
      <select class="w-56 h-8 mx-1 border" v-model="searchType">
        <option value="itemName">상품명</option>
        <option value="contractNo">주문번호</option>
        <option value="itemCode">상품코드</option>
        <option value="spec">규격</option>
        <option value="dan">단가</option>
        <option value="qty">이전주문수량</option>
        <option value="supplyPrice">공급가액</option>
        <option value="vat">부가세액</option>
        <option value="amount">결제금액</option>
      </select>
      <input type="text" class="border rounded border-black p-1 m-1 w-search_box" placeholder="검색어 입력" @keyup.enter="searchReContractList" v-model="searchWord"/>
<!--      <button @click="searchReContractList" class="border rounded border-black p-1 m-1">검색</button>-->
<!--      <button class="w-28 rounded-md border ml-auto" @click="putSelectItems">선택상품 담기</button>-->
    </div>
    <div class="px-2">
      <ag-grid-vue
        class="ag-theme-contract"
        style="height: 500px"
        :columnDefs="columnDefs.value"
        :rowData="rowData.value"
        :defaultColDef="defaultColDef"
        :suppressCsvExport="true"
        rowSelection="multiple"
        animateRows="true"
        suppressRowClickSelection="true"
        @cell-clicked="cellWasClicked"
        @grid-ready="onGridReady"
      >
      </ag-grid-vue>
    </div>
<!--    <div class="float-right mr-3">-->
<!--      <button class="w-28 rounded-md border">선택상품 담기</button>-->
<!--    </div>-->
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
  name: 'RecontractView',
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
          headerName: '주문일자',
          field: 'ordDate',
          cellRenderer: function (params) {
            const ordDate = new Date(params.data.ordDate)
            return ordDate.getFullYear() + '-' + (ordDate.getMonth() + 1) + '-' + ordDate.getDate()
          },
          cellClass: 'textCell',
          editable: 'false'
        },
        { headerName: '주문번호', field: 'ordNo', cellClass: 'textCell', editable: 'false' },
        { headerName: '상품코드', field: 'itemCode', cellClass: 'textCell', editable: 'false' },
        {
          headerName: '이미지',
          field: 'itemimage',
          cellClass: 'textCell',
          cellRenderer: (params) => '<img src="http://appdata.hungryapp.co.kr/data_file/data_img_m/202112/M163979800045320113.png/hungryapp/resize/500" width="40" height="40"/>'
          // cellRenderer: (params) => `<img src=${params.data.itemimage} width="40" height="40"/>`
        },
        { headerName: '상품명', field: 'itemName', cellClass: 'textCell', editable: 'false' },
        { headerName: '규격', field: 'spec', cellClass: 'textCell', editable: 'false' },
        { headerName: '단위', field: 'unit', cellClass: 'textCell', editable: 'false' },
        {
          headerName: '단가',
          field: 'dan',
          valueFormatter: params => params.data.dan.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell',
          editable: 'false'
        },
        // {
        //   headerName: '수량',
        //   field: 'ordQty',
        //   valueFormatter: params => params.data.ordQty.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
        //   cellClass: 'numberCell',
        //   editable: 'false'
        // },
        {
          headerName: '공급가',
          field: 'supplyprice',
          valueFormatter: params => (params.data.dan * params.data.ordQty * 0.9).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell',
          editable: 'false'
        },
        {
          headerName: '부가세',
          field: 'surtax',
          valueFormatter: params => (params.data.dan * params.data.ordQty * 0.1).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell',
          editable: 'false'
        },
        {
          headerName: '결제금액',
          field: 'amt',
          valueFormatter: params => params.data.amt.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell',
          editable: 'false'
        },
        {
          headerName: '낱개',
          field: 'qtyField',
          cellClass: 'textCell'
        },
        {
          headerName: '박스',
          field: 'boxField',
          cellClass: 'textCell'
        },
        {
          headerName: '주문수량',
          field: 'allQty',
          valueFormatter: params => parseInt(params.data.qty * params.data.boxField) + parseInt(params.data.qtyField),
          cellClass: 'textCell',
          editable: 'false'
        },
        {
          headerName: '장바구니',
          field: 'ContractBasket',
          cellRenderer: function (params) {
            return '<div><button class="w-10 flex items-center"><img src="/img/basket.png" alt="" width="20"/></button></div>'
          },
          cellClass: 'textCell',
          editable: 'false'
        }
      ]
    })

    // DefaultColDef sets props common to all Columns
    const defaultColDef = {
      sortable: true,
      editable: true,
      flex: 1
    }
    // Example load data from sever
    onMounted(() => {
      // axios.get('/api/reContract/reContractList', {
      //   params: {
      //     fromDate: '2022-07-28',
      //     toDate: '2022-10-28',
      //     searchType: 'itemName',
      //     word: ''
      //   }
      // }).then((res) => {
      //   rowData.value = res.data
      // })
    })

    return {
      onGridReady,
      columnDefs,
      rowData,
      defaultColDef,
      gridApi,
      cellWasClicked: (event) => { // Example of consuming Grid Event
        if (event.column.colId === 'ContractBasket') {
          axios.post('/api/recontract/putContractBasket', {
            params: {
              rowData: event.data,
              userId: store.state.account.userId
            }
          }).then((res) => {
            alert('선택하신 상품이 정상적으로 장바구니에 담겼습니다.')
          })
        }
      },
      deselectRows: () => {
        gridApi.value.deselectAll()
      },
      putSelectItems: () => {
        if (gridApi.value.getSelectedRows().length > 0) {
          axios.post('/api/recontract/putContractBasket', {
            params: {
              rowData: gridApi.value.getSelectedRows(),
              userId: store.state.account.userId
            }
          }).then((res) => {
            alert('선택하신 상품이 정상적으로 장바구니에 담겼습니다.')
          })
        } else {
          alert('장바구니에 담을 상품을 선택해주세요.')
        }
      }
    }
  },
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
      } else if (this.$store.state.datetype === 'month_3') {
        const monthAgo = new Date().getFullYear() + '-' +
          ('0' + (new Date().getMonth() - 2)).slice(-2) + '-' +
          ('0' + new Date().getDate()).slice(-2)
        this.$store.commit('setFromDate', monthAgo)
        this.$store.commit('setToDate', nowDate)
      } else if (this.$store.state.datetype === 'firstDate') {
        const firstDate = new Date().getFullYear() + '-' +
          ('0' + (new Date().getMonth() - 2)).slice(-2) + '-' + '01'
        this.$store.commit('setFromDate', firstDate)
        this.$store.commit('setToDate', nowDate)
      } else if (this.$store.state.datetype === 'month_1') {
        const monthAgo = new Date().getFullYear() + '-' +
          ('0' + (new Date().getMonth())).slice(-2) + '-' +
          ('0' + new Date().getDate()).slice(-2)
        this.$store.commit('setFromDate', monthAgo)
        this.$store.commit('setToDate', nowDate)
      }
      return 'ok'
    }
  },
  mounted () {
    this.$store.commit('setDate', 'month')
    this.searchReContractList()
  },
  methods: {
    async searchReContractList () {
      axios.get('/api/reContract/reContractList', {
        params: {
          fromDate: this.getFromDate,
          toDate: this.getToDate,
          searchType: this.searchType,
          word: this.searchWord
        }
      }).then((res) => {
        this.rowData.value = res.data
        console.log(res.data)
      })
    },
    searchRecentBtnClick () {
      const test = this.setFromToDate
      if (test === 'ok') {
        this.searchReContractList()
      }
    }
  },
  data () {
    return {
      searchType: 'itemName',
      searchWord: ''
    }
  }
}
</script>

<style scoped>

</style>
