<template>
  <div class="content">
    <p class="text-xl">배송조회</p>
    <div class="flex">
      <button @click="$store.commit('setDate','today'), searchRecentBtnClick()" class="border rounded border-black p-1 m-1">오늘</button>
      <button @click="$store.commit('setDate','week'), searchRecentBtnClick()" class="border rounded border-black p-1 m-1">최근 1주일</button>
      <button @click="$store.commit('setDate','month'), searchRecentBtnClick()" class="border rounded border-black p-1 m-1">최근 3개월</button>
      <date-picker :getDate='getDate' />
      <input type="text" class="border rounded border-black p-1 m-1" placeholder="검색" @keyup.enter="searchContractDeliveryList" v-model="searchWord">
      <button @click="searchContractDeliveryList" class="border rounded border-black p-1 m-1">검색</button>
    </div>
    <ag-grid-vue
      class="ag-theme-alpine"
      style="height: 500px"
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
</template>
<script>
import { AgGridVue } from 'ag-grid-vue3' // the AG Grid Vue Component
import { reactive, onMounted, ref } from 'vue'
import DatePicker from '@/components/DatePicker'
import 'ag-grid-community/styles/ag-grid.css' // Core grid CSS, always needed
import 'ag-grid-community/styles/ag-theme-alpine.css'
import axios from 'axios' // Optional theme CSS

export default {
  name: 'App',
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
      value: [
      ]
    })

    // Each Column Definition results in one Column.
    const columnDefs = reactive({
      value: [
        { headerName: 'No', valueGetter: 'node.rowIndex + 1', cellClass: 'textCell' },
        {
          headerName: '주문일자',
          field: 'ordDate',
          cellRenderer: function (params) {
            const ordDate = new Date(params.data.ordDate)
            return ordDate.getFullYear() + '-' + (ordDate.getMonth() + 1) + '-' + ordDate.getDate()
          },
          cellClass: 'textCell'
        }, // field : 'DTO와 매칭'
        { headerName: '주문번호', field: 'orderNo', cellClass: 'textCell' },
        { headerName: '업체명', field: 'custName', cellClass: 'textCell' },
        { headerName: '상품명', field: 'itemName', cellClass: 'textCell' },
        { headerName: '주문수량', field: 'qty', cellClass: 'textCell' },
        {
          headerName: '주문금액',
          field: 'amt',
          valueFormatter: params => params.data.amt.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell'
        },
        {
          headerName: '배송예정일',
          field: 'delDate',
          cellRenderer: function (params) {
            const delDate = new Date(params.data.delDate)
            return delDate.getFullYear() + '-' + (delDate.getMonth() + 1) + '-' + delDate.getDate()
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
    // Example load data from sever
    onMounted(() => {
      const nowDate = new Date()
      const nowYear = nowDate.getFullYear()
      const nowMonth = nowDate.getMonth() + 1
      const nowDay = nowDate.getDate()
      const nowDateString = String(nowYear) + '-' + nowMonth + '-' + nowDay
      axios.get('/api/searchContractDelivery/searchContractDeliveryList', {
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
        console.log(event)
      },
      deselectRows: () => {
        gridApi.value.deselectAll()
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
    async searchContractDeliveryList () {
      axios.get('/api/searchContractDelivery/searchContractDeliveryList', {
        params: {
          fromDate: this.getFromDate,
          toDate: this.getToDate,
          word: this.searchWord
        }
      }).then((res) => {
        this.rowData.value = res.data
      })
    },
    searchRecentBtnClick () {
      const test = this.setFromToDate
      if (test === 'ok') {
        this.searchContractDeliveryList()
      }
    }
  },
  mounted () {
    this.$store.commit('setDate', 'today')
  },
  data () {
    return {
      getDate: new Date(),
      searchWord: ''
    }
  }
}

</script>
<style>
.numberCell {
  text-align: right
}
.textCell {
  text-align: center
}
.ag-header-cell-label {
  justify-content: center
}
</style>
