<template>
  <div class="flex">
    <div class="flex-auto w-100% mt-10">
      <div class="mb-2 flex">
        <div class="mr-3 w-20 border rounded-md bg-[#E6E6E6] text-center" style="padding: 5px 0">납품일자</div>
        <date-picker />
      </div>
    <div class="mb-2 flex">
        <div class="w-20 mr-1 border rounded-md bg-[#E6E6E6] text-center" style="padding: 5px 0">상품구분</div>
        <select class="mx-2 w-40 rounded-md border-solid border border-black" v-model="selectType1" @change="searchItem($event.target.value, 'select1')">
          <option class="border-solid border-black" selected value="">대분류</option>
          <option v-for="type in type1option" :key="type.dcode" :value="type.dcode">{{type.dcodename}}</option>
        </select>
        <select class="w-40 rounded-md border-solid border border-black" v-model="selectType2" @change="searchItem($event.target.value, 'select2')">
          <option selected value="">중분류</option>
          <option v-if="selectType1" v-for="type in type2option" :key="type.dcode" :value="type.dcode">{{type.dcodename}}</option>
        </select>
        <select class="mx-2 w-40 rounded-md border-solid border border-black" v-model="selectType3" @change="searchItem($event.target.value, 'select3')">
          <option selected value="">소분류</option>
          <option v-show="selectType2" v-for="type in type3option" :key="type.dcode" :value="type.dcode">{{type.dcodename}}</option>
        </select>
      </div>
      <div class="flex">
        <button class="w-20 mr-3 border rounded-md bg-[#B3B3B3] active:bg-[#E6E6E6] rounded-md text-center" style="padding: 5px 0" @click="selectBtnClick">검색
          <span class="material-symbols-outlined " style="font-size: 15px;">search</span></button>
        <input class="rounded-md border-solid border border-black" style="width: 31rem" placeholder="상품명" v-model="itemName"/>
      </div>
    </div>
  </div>
  <div class="mt-4">
    <ag-grid-vue
      class="ag-theme-alpine"
      style="height: 500px"
      :columnDefs="columnDefs.value"
      :rowData="rowData.value"
      :defaultColDef="defaultColDef"
      :getRowStyle="getRowStyle"
      :suppressCsvExport="true"
      :pinnedBottomRowData="pinnedBottomRowData.value"
      rowSelection="multiple"
      animateRows="true"
      suppressRowClickSelection="true"
    >
    </ag-grid-vue>
  </div>
</template>

<script>
import { onMounted, reactive, ref } from 'vue'
import { AgGridVue } from 'ag-grid-vue3'
import DatePicker from '@/components/DatePicker'
import axios from 'axios'
import store from '@/store'
import { getTypeDiv, getTypeDivInfo } from '@/assets/js/Contract'

export default {
  name: 'ConfirmDeliveryView',
  components: {
    AgGridVue,
    DatePicker
  },
  setup () {
    const getRowStyle = (params) => {
      const rowNo = params.data.rowNo
      if (rowNo === '합계') {
        return { background: 'rgba(148, 198, 229, 0.19)' }
      }
    }
    const itemName = ref(null)
    const state = reactive({
      startYear: '',
      startMonth: '',
      endYear: '',
      endMonth: ''
    })
    const columnDefs = reactive({
      value: [
        {
          headerName: '번호',
          field: 'rowNo',
          suppressMovable: true,
          cellClass: 'textCell'
        },
        {
          headerName: '발주번호',
          field: 'buyCode',
          suppressMovable: true,
          cellClass: 'textCell'
        },
        {
          headerName: '상품코드',
          field: 'itemCode',
          suppressMovable: true,
          cellClass: 'textCell'
        },
        {
          field: 'itemName',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center" role="columnheader">상품명<br>규격</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          cellClass: 'textCell'
        },
        {
          headerName: '브랜드',
          field: 'brand',
          suppressMovable: true,
          cellClass: 'textCell'
        },
        {
          headerName: '유통기한',
          field: 'shelfLife',
          suppressMovable: true,
          maxWidth: 80,
          cellClass: 'textCell'
        },
        {
          headerName: '제조일자',
          field: 'manDate',
          suppressMovable: true,
          maxWidth: 80,
          cellClass: 'textCell'
        },
        {
          headerName: '발주수량',
          field: 'qty',
          suppressMovable: true,
          maxWidth: 70,
          cellClass: 'textCell'
        },
        {
          headerName: '잔여수량',
          field: 'restQty',
          suppressMovable: true,
          maxWidth: 70,
          cellClass: 'textCell'
        },
        {
          field: 'inCode',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center text-sky-700" role="columnheader">납품번호</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          cellClass: 'textCell'
        },
        {
          field: 'inQty',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center text-sky-700" role="columnheader">납품수량</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          maxWidth: 70,
          cellClass: 'textCell'
        },
        {
          field: 'dan',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center text-sky-700" role="columnheader">단가</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          valueFormatter: params => params.data.dan.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 90,
          cellClass: 'numberCell'
        },
        {
          field: 'amount',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center text-sky-700" role="columnheader">공급가액</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          valueFormatter: params => params.data.amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 130,
          cellClass: 'numberCell'
        },
        {
          field: 'vat',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center text-sky-700" role="columnheader">부가세</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          valueFormatter: params => params.data.vat.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 120,
          cellClass: 'numberCell'
        },
        {
          field: 'sumAmt',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center text-sky-700" role="columnheader">납품금액</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          valueFormatter: params => params.data.sumAmt.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 130,
          cellClass: 'numberCell'
        },
        {
          field: 'inSts',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center text-orange-700" role="columnheader">납품상태</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          maxWidth: 70,
          cellClass: 'textCell',
          cellStyle: {
            color: 'red', 'font-weight': 'bold'
          }
        }
      ]
    })
    const rowData = reactive({
      value: []
    })

    const pinnedBottomRowData = reactive({
      value: []
    })

    const defaultColDef = {
      flex: 1
    }
    const type1optionOri = ref(null)
    const type2optionOri = ref(null)
    const type3optionOri = ref(null)
    const type1option = ref(null)
    const type2option = ref(null)
    const type3option = ref(null)
    const itemTypeInfo = ref(null)
    const selectType1 = ref('')
    const selectType2 = ref('')
    const selectType3 = ref('')
    const tradingStatementFlag = ref(false)

    onMounted(() => {
      store.commit('setDate', 'today')
      // 시간지연을 주는 이유는 store에 fromDate,toDate가 변경되는것을 기다리기 위함
      // 아이템 분류를 가져와서 세팅하는 함수
      axios.get('/api/contract/items/typeDiv')
        .then((res) => {
          type1optionOri.value = res.data.TYPE1
          type2optionOri.value = res.data.TYPE2
          type3optionOri.value = res.data.TYPE3
          type1option.value = getTypeDiv(type1optionOri.value)
          type2option.value = getTypeDiv(type2optionOri.value)
          type3option.value = getTypeDiv(type3optionOri.value)
        })
        // 아이템의 분류를 구분하는 데이터를 가져오는 함수
      axios.get('/api/contract/items/typeDivInfo')
        .then((res) => {
          itemTypeInfo.value = res.data
        })
      setTimeout(function () {
        axios.get('/api/order/confirmDeliveryItem', {
          params: {
            bizNo: store.getters.getAccount.bizno,
            fromDate: store.state.fromDate,
            toDate: store.state.toDate,
            itemName: itemName.value,
            type1: selectType1.value,
            type2: selectType2.value,
            type3: selectType3.value,
            tradingStatementFlag: tradingStatementFlag.value
          }
        }).then((res) => {
          rowData.value = res.data
          let totalInQty = 0
          let totalAmount = 0
          let totalVat = 0
          let totalSumAmt = 0
          for (let i = 0; i < res.data.length; i++) {
            totalInQty += res.data[i].inQty
            totalAmount += res.data[i].amount
            totalVat += res.data[i].vat
            totalSumAmt += res.data[i].sumAmt
          }
          pinnedBottomRowData.value = [{
            rowNo: '합계',
            buyCode: '',
            itemCode: '',
            itemName: '',
            brand: '',
            shelfLife: '',
            manDate: '',
            qty: '',
            restQty: '',
            inCode: '',
            inQty: totalInQty,
            dan: '',
            amount: totalAmount,
            vat: totalVat,
            sumAmt: totalSumAmt,
            inSts: ''
          }]
        })
      })
    })
    return {
      state,
      columnDefs,
      rowData,
      defaultColDef,
      getRowStyle,
      pinnedBottomRowData,
      itemName,
      type1option,
      type2option,
      type3option,
      selectType1,
      selectType2,
      selectType3,
      selectBtnClick: () => {
        axios.get('/api/order/confirmDeliveryItem', {
          params: {
            bizNo: store.getters.getAccount.bizno,
            fromDate: store.state.fromDate,
            toDate: store.state.toDate,
            itemName: itemName.value,
            type1: selectType1.value,
            type2: selectType2.value,
            type3: selectType3.value,
            tradingStatementFlag: tradingStatementFlag.value
          }
        }).then((res) => {
          rowData.value = res.data
          let totalInQty = 0
          let totalAmount = 0
          let totalVat = 0
          let totalSumAmt = 0
          for (let i = 0; i < res.data.length; i++) {
            totalInQty += res.data[i].inQty
            totalAmount += res.data[i].amount
            totalVat += res.data[i].vat
            totalSumAmt += res.data[i].sumAmt
          }
          pinnedBottomRowData.value = [{
            rowNo: '합계',
            buyCode: '',
            itemCode: '',
            itemName: '',
            brand: '',
            shelfLife: '',
            manDate: '',
            qty: '',
            restQty: '',
            inCode: '',
            inQty: totalInQty,
            dan: '',
            amount: totalAmount,
            vat: totalVat,
            sumAmt: totalSumAmt,
            inSts: ''
          }]
        })
      },
      searchItem: (event, selectDiv) => {
        // 분류를 재설정 해준다.
        const resTypeInfo = getTypeDivInfo(event, itemTypeInfo.value,
          type1optionOri.value,
          type2optionOri.value,
          type3optionOri.value,
          type2option.value,
          type3option.value,
          selectDiv)
        type2option.value = getTypeDiv(resTypeInfo.type2)
        type3option.value = getTypeDiv(resTypeInfo.type3)
        if (selectDiv === 'select1') {
          selectType2.value = ''
          selectType3.value = ''
        } else if (selectDiv === 'select2') {
          selectType3.value = ''
        }
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
