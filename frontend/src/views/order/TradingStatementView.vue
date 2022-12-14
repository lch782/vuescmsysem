<template>
  <div class="flex">
    <div class="flex-auto w-100% mt-10">
      <div class="relative mb-2 flex">
        <div class="mr-3 w-20 border rounded-md bg-[#E6E6E6] text-center" style="padding: 5px 0">납품일자</div>
        <date-picker/>
      </div>
      <div class="mb-2 flex">
        <div class="w-20 border mr-1 rounded-md bg-[#E6E6E6] text-center" style="padding: 5px 0">상품구분</div>
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
      <div class="flex ">
        <div class="flex">
          <button class="w-20 mr-3 border rounded-md bg-[#B3B3B3] active:bg-[#E6E6E6] text-center" style="padding: 5px 0" @click="selectBtnClick">검색
            <span class="material-symbols-outlined " style="font-size: 15px;">search</span></button>
          <input class="rounded-md border-solid border border-black" style="width: 31rem" placeholder="상품명" v-model="itemName"/>
        </div>
        <div class="flex justify-end w-full">
          <input class="mr-2 border border-black w-40 rounded-md pl-2" placeholder="납품번호" disabled v-model="getInCode">
          <button class="w-40 rounded-md bg-[#333333] active:bg-[#808080] p-1 text-white " @click="openPopup">거래명세서 발행</button>
        </div>
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
      :suppressCsvExport="true"
      rowSelection="single"
      @cell-clicked="cellWasClicked"
      animateRows="true"
      suppressRowClickSelection="true"
      @grid-ready="onGridReady"
    >
    </ag-grid-vue>
  </div>
  <div class="modal">
    <div class="overlay flex" v-if="!popupView">
      <button class="text-white flex p-10" @click="openPopup">닫기</button>
      <div class="modal-card">
        <trading-statement/>
      </div>
    </div>
  </div>
</template>

<script>
import { onMounted, reactive, ref } from 'vue'
import { AgGridVue } from 'ag-grid-vue3'
import axios from 'axios'
import store from '@/store'
import DatePicker from '@/components/DatePicker'
import TradingStatement from '@/components/popup/TradingStatement'
import { getTypeDiv, getTypeDivInfo } from '@/assets/js/Contract'

export default {
  name: 'TradingStatementView',
  components: {
    AgGridVue,
    DatePicker,
    TradingStatement
  },
  setup () {
    const state = reactive({
      startYear: '',
      startMonth: '',
      endYear: '',
      endMonth: ''
    })

    const popupView = ref(true)
    function openPopup () {
      console.log(popupView.value)
      popupView.value = !popupView.value
    }

    const getInCode = ref()
    function cellWasClicked (params) {
      getInCode.value = params.data.inCode
      console.log(getInCode)
    }

    const columnDefs = reactive({
      value: [
        {
          headerName: '번호',
          field: 'rowNo',
          suppressMovable: true,
          valueGetter: 'node.rowIndex + 1',
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
          cellClass: 'textCell'
        },
        {
          headerName: '제조일자',
          field: 'manDate',
          suppressMovable: true,
          cellClass: 'textCell'
        },
        {
          field: 'qty',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center" role="columnheader">발주<br>수량</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
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
          cellClass: 'textCell'
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
          cellClass: 'textCell'
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
          cellClass: 'textCell'
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
          cellClass: 'textCell'
        }
      ]
    })
    const rowData = reactive({
      value: []
    })
    const defaultColDef = {
      sortable: true,
      flex: 1
    }
    const itemName = ref(null)
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
    const tradingStatementFlag = ref(true)

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
        })
      })
    })

    return {
      state,
      popupView,
      openPopup,
      columnDefs,
      rowData,
      defaultColDef,
      itemName,
      type1option,
      type2option,
      type3option,
      selectType1,
      selectType2,
      selectType3,
      getInCode,
      cellWasClicked,
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
.tradingStatement {
  margin: 0 auto;
}
.modal{
  position: fixed;
  margin: 0 auto;
  width: 100%;
  left: 0;
  top: 0;
}
.overlay{
  opacity: 1;
  height: 100vh;

  background-color: black;
}
.modal-card{
  position: relative;
  max-width: 80%;

  margin-top: 30px;
  padding: 20px;
  background-color: white;
  min-height: 500px;
  z-index: 10;
  opacity: 1;
}
</style>
