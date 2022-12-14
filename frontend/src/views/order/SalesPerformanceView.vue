<template>
  <div class="flex">
    <div class="flex-auto w-100% mt-10">
      <div class="mb-2 flex">
        <button class="w-20 mr-3 border rounded-md bg-[#B3B3B3] active:bg-[#E6E6E6] text-center" style="padding: 5px 0" @click="searchBtnClick">검색
          <span class="material-symbols-outlined " style="font-size: 15px;">search</span></button>
        <date-picker/>
      </div>
    </div>
  </div>
  <div class="mt-24">
    <ag-grid-vue
      class="ag-theme-alpine"
      style="height: 500px"
      :columnDefs="columnDefs.value"
      :rowData="rowData.value"
      :defaultColDef="defaultColDef"
      :suppressCsvExport="true"
      :moveColumn="false"
      :pinnedBottomRowData="pinnedBottomRowData.value"
      :getRowStyle="getRowStyle"
      rowSelection="multiple"
      animateRows="true"
      suppressRowClickSelection="true"
    >
    </ag-grid-vue>
  </div>
</template>

<script>
import { onMounted, reactive } from 'vue'
import { AgGridVue } from 'ag-grid-vue3'
import DatePicker from '@/components/DatePicker'
import axios from 'axios'
import store from '@/store'

export default {
  name: 'SalesPerformanceView',
  components: {
    AgGridVue,
    DatePicker
  },
  setup () {
    // setDate 초기화
    store.commit('setDate', '')
    const getRowStyle = (params) => {
      const rowNo = params.data.rowNo
      if (rowNo === '합계') {
        return { background: 'rgba(148, 198, 229, 0.19)' }
      }
    }

    const columnDefs = reactive({
      value: [
        {
          headerName: '상품',
          field: 'material',
          children: [
            { headerName: '번호', field: 'rowNo', suppressMovable: true },
            { headerName: '상품코드', field: 'salCode', suppressMovable: true },
            { headerName: '상품명', field: 'itemName', suppressMovable: true },
            { headerName: '브랜드', field: 'brand', suppressMovable: true }
          ],
          suppressMovable: true,
          cellClass: 'textCell'
        },
        {
          headerName: '매출',
          children: [
            {
              headerName: '수량',
              field: 'inQty',
              suppressMovable: true
            },
            {
              headerName: '단가',
              field: 'inDan',
              suppressMovable: true,
              valueFormatter: params => {
                let inAmount = ''
                if (params.value !== undefined) {
                  inAmount = params.value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
                }
                return inAmount
              }
            },
            {
              headerName: '금액',
              field: 'inPrice',
              suppressMovable: true,
              valueFormatter: params => params.data.inPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
            }
          ],
          suppressMovable: true,
          cellClass: 'textCell'
        },
        {
          headerName: '세금계산서 발행액',
          children: [
            {
              headerName: '공급가액',
              field: 'taxAmount',
              suppressMovable: true,
              valueFormatter: params => params.data.taxAmount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
            },
            {
              headerName: '부가세',
              field: 'taxVat',
              suppressMovable: true,
              valueFormatter: params => params.data.taxVat.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
            },
            {
              headerName: '합계',
              field: 'taxPrice',
              suppressMovable: true,
              valueFormatter: params => params.data.taxPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
            }
          ],
          suppressMovable: true,
          cellClass: 'textCell'
        }
      ]
    })
    const rowData = reactive({
      value: []
    })
    const defaultColDef = {
      editable: true,
      flex: 1
    }

    const pinnedBottomRowData = reactive({
      value: []
    })

    onMounted(() => {
      store.commit('setDate', 'firstDate')
      setTimeout(function () {
        axios.get('/api/order/getSalesPerformance', {
          params: {
            bizNo: store.getters.getAccount.bizno,
            fromDate: store.state.fromDate,
            toDate: store.state.toDate
          }
        }).then((res) => {
          rowData.value = res.data

          let totalAmount = 0
          let totalVat = 0
          let totalPrice = 0

          let totalTaxAmount = 0
          let totalTaxVat = 0
          let totalTaxPrice = 0

          for (let i = 0; i < res.data.length; i++) {
            totalAmount += res.data[i].inAmount
            totalVat += res.data[i].inVat
            totalPrice += res.data[i].inPrice

            totalTaxAmount += res.data[i].taxAmount
            totalTaxVat += res.data[i].taxVat
            totalTaxPrice += res.data[i].taxPrice
          }

          pinnedBottomRowData.value = [{
            rowNo: '합계',
            inAmount: totalAmount,
            inVat: totalVat,
            inPrice: totalPrice,
            taxAmount: totalTaxAmount,
            taxVat: totalTaxVat,
            taxPrice: totalTaxPrice
          }]
        })
      })
    })

    return {
      getRowStyle,
      columnDefs,
      rowData,
      defaultColDef,
      pinnedBottomRowData,
      searchBtnClick: () => {
        axios.get('/api/order/getSalesPerformance', {
          params: {
            bizNo: store.getters.getAccount.bizno,
            fromDate: store.state.fromDate,
            toDate: store.state.toDate
          }
        }).then((res) => {
          rowData.value = res.data
        })
      }
    }
  }
}
</script>
<style scoped>
.ag-theme-alpine {
  --ag-borders: none;
  --ag-header-column-separator-display: block;
  --ag-header-column-separator-color: white;
  --ag-header-background-color: rgba(148, 198, 229, 0.19);
  --ag-grid-size: 1px;
  --ag-row-hover-color: rgba(148, 198, 229, 0.10);
  --ag-selected-row-background-color: rgba(148, 198, 229, 0.10);
}
</style>
<style scoped>
.ag-theme-alpine .ag-header-group-cell {
  background-color: rgba(148, 198, 229, 0.30);
}
</style>
