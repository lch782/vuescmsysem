<template>
  <div class="flex" >
    <div class="flex-auto w-100% mt-10">
      <div class="mb-2 flex">
        <div class="mr-3 w-20 border rounded-md bg-[#E6E6E6] text-center" style="padding: 5px 0">발주일자</div>
        <date-picker/>
      </div>
      <div class="mb-2 flex">
        <div class="mr-3 w-20 border rounded-md bg-[#E6E6E6] text-center text-center" style="padding: 5px 0">상품구분</div>
        <select class="w-40 rounded-md border-solid border border-black" v-model="selectType1" @change="searchItem($event.target.value, 'select1')">
          <option class="border-solid border-black" selected value="">대분류</option>
          <option v-for="type in type1option" :key="type.dcode" :value="type.dcode">{{type.dcodename}}</option>
        </select>
        <select class="ml-2 w-40 rounded-md border-solid border border-black" v-model="selectType2" @change="searchItem($event.target.value, 'select2')">
          <option selected value="">중분류</option>
          <option v-if="selectType1" v-for="type in type2option" :key="type.dcode" :value="type.dcode">{{type.dcodename}}</option>
        </select>
        <select class="mx-2 w-40 rounded-md border-solid border border-black" v-model="selectType3" @change="searchItem($event.target.value, 'select3')">
          <option selected value="">소분류</option>
          <option v-show="selectType2" v-for="type in type3option" :key="type.dcode" :value="type.dcode">{{type.dcodename}}</option>
        </select>
      </div>
      <div class="flex">
        <div class="flex">
          <button class="w-20 mr-3 border rounded-md bg-[#B3B3B3] active:bg-[#E6E6E6] text-center text-center" style="padding: 5px 0" @click="searchBtnClick">검색
            <span class="material-symbols-outlined " style="font-size: 15px;">search</span></button>
          <input class="rounded-md border-solid border border-black" style="width: 20.6rem" placeholder="상품명" v-model="itemName"/>
          <div class="ml-2 w-20 border rounded-md bg-[#E6E6E6] text-center text-center" style="padding: 5px 0">납품구분</div>
          <select class="ml-3" v-model="buySts" @change="getBuySts">
            <option value="00">전체</option>
            <option value="02">미완료</option>
            <option value="03">완료</option>
          </select>
        </div>
        <div class="flex justify-end w-full">
          <button class="w-40 rounded-md bg-[#333333] hover:bg-[#808080] text-white mr-2 ">엑셀파일로 다운로드</button>
          <button class="w-40 rounded-md bg-[#333333] hover:bg-[#808080] p-1 text-white" @click="openPopup">발주서 출력</button>
        </div>
      </div>
    </div>
  </div>
  <div class="mt-4" >
    <ag-grid-vue
      class="ag-theme-alpine"
      style="height: 500px"
      :rowHeight="80"
      :columnDefs="columnDefs.value"
      :rowData="rowData.value"
      :defaultColDef="defaultColDef"
      :suppressRowTransform="true"
      :suppressCsvExport="true"
      :getRowStyle="getRowStyle"
      :getRowHeight="getRowHeight"
      rowSelection="multiple"
      @grid-ready="onGridReady"
    >
    </ag-grid-vue>
  </div>
  <div class="modal">
    <div class="overlay flex" v-if="!popupView">
      <button class="text-white flex p-10" @click="openPopup">닫기</button>
      <div class="modal-card">
        <div>
          <purchase-order
            :buySts='buySts'
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, onMounted, ref } from 'vue'
import { getTypeDiv, getTypeDivInfo } from '@/assets/js/Contract'
import { AgGridVue } from 'ag-grid-vue3'
import axios from 'axios'
import store from '@/store'
import DatePicker from '@/components/DatePicker'
import PurchaseOrder from '@/components/popup/PurchaseOrder'

export default {
  name: 'CheckOrderView',
  components: {
    AgGridVue,
    DatePicker,
    PurchaseOrder
  },
  setup () {
    // 초기화
    store.commit('setDate', '')
    const gridApi = ref(null) // Optional - for accessing Grid's API
    const columnApi = ref(null)
    const buySts = ref('00')

    const getBuySts = (i) => {
      buySts.value = i.target.value
      console.log(buySts.value)
    }

    const popupView = ref(true)
    function openPopup () {
      console.log(popupView.value)
      popupView.value = !popupView.value
    }

    const getRowStyle = (params) => {
      const showBuyCode = params.data.showBuyCode
      if (showBuyCode === '소계') {
        return { background: 'rgba(148, 198, 229, 0.19)' }
      }
    }
    const getRowHeight = (params) => {
      const showBuyCode = params.data.showBuyCode
      if (showBuyCode === '소계') {
        return 30
      }
    }
    // Obtain API from grid's onGridReady event
    const onGridReady = (params) => {
      gridApi.value = params.api
      columnApi.value = params.columnApi
    }
    const listCount = ref(0)
    // Row Spanning 함수
    function rowSpan (params) {
      const showBuyCode = params.data.showBuyCode
      if (params.data.showBuyCode === '소계') {
        return 1
      } else {
        return rowData.value.filter((e) => e.buyCode === showBuyCode).length
      }
    }
    const columnDefs = reactive({
      value: [
        {
          field: 'rowNo',
          headerName: '총0건',
          suppressMovable: true,
          maxWidth: 70,
          cellClass: 'textCell'
        },
        {
          field: 'buyDate',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center" role="columnheader">발주일자<br>요구납기</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          cellRenderer: function (params) {
            return '<p>' + params.data.buyDate + '</p><p>' + params.data.inDate + '</p>'
          },
          maxWidth: 110,
          cellClass: 'textCell'
        },
        {
          field: 'itemInfo',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center" role="columnheader">상품코드<br>상품명<br>규격</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          cellRenderer: function (params) {
            return '<p>' + params.data.itemCode + '</p><p>' + params.data.itemName + '</p><p>' + params.data.spec + '</p>'
          },
          cellClass: 'textCell'
        },
        {
          field: 'brand',
          headerName: '브랜드',
          suppressMovable: true,
          maxWidth: 100,
          cellClass: 'textCell'
        },
        {
          field: 'showBuyCode',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center" role="columnheader">발주번호' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          rowSpan: rowSpan,
          maxWidth: 110,
          cellClass: 'textCell'
        },
        {
          field: 'buyCode',
          hide: true
        },
        {
          headerName: '단위',
          field: 'unit',
          suppressMovable: true,
          maxWidth: 70,
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
          maxWidth: 90,
          cellClass: 'textCell'
        },
        {
          headerName: '발주단가',
          field: 'dan',
          suppressMovable: true,
          valueFormatter: params => params.data.dan.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 100,
          cellClass: 'textCell'
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
          headerName: '부가세',
          field: 'vat',
          suppressMovable: true,
          valueFormatter: params => params.data.vat.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 120,
          cellClass: 'textCell'
        },
        {
          headerName: '발주금액',
          field: 'sumAmt',
          suppressMovable: true,
          valueFormatter: params => params.data.sumAmt.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 130,
          cellClass: 'textCell'
        },
        {
          field: 'remark',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center" role="columnheader">비고<br>사항</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          maxWidth: 70,
          cellClass: 'textCell'
        }
      ]
    })
    const rowData = reactive({
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
    const itemName = ref('')

    onMounted(() => {
      store.commit('setDate', 'firstDate')
      // 시간지연을 주는 이유는 store에 fromDate,toDate가 변경되는것을 기다리기 위함
      // 아이템 분류를 가져와서 세팅하는 함수
      axios.get('/api/contract/items/typeDiv')
        .then((res) => {
          console.log(res.data)
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
        axios.get('/api/order/getCheckOrder', {
          params: {
            bizNo: store.getters.getAccount.bizno,
            fromDate: store.state.fromDate,
            toDate: store.state.toDate,
            type1: selectType1.value,
            type2: selectType2.value,
            type3: selectType3.value,
            itemName: itemName.value,
            id: store.state.account.userId,
            buySts: buySts.value
          }
        }).then((res) => {
          rowData.value = res.data
          columnApi.value.getColumn('rowNo').colDef.headerName = '총' + res.data.length + '건'
          gridApi.value.refreshHeader()
        })
      })
    })

    return {
      getBuySts,
      popupView,
      openPopup,
      getRowStyle,
      getRowHeight,
      onGridReady,
      columnApi,
      listCount,
      columnDefs,
      rowData,
      defaultColDef,
      type1option,
      type2option,
      type3option,
      selectType1,
      selectType2,
      selectType3,
      itemName,
      buySts,
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
      },
      searchBtnClick: () => {
        axios.get('/api/order/getCheckOrder', {
          params: {
            fromDate: store.state.fromDate,
            toDate: store.state.toDate,
            type1: selectType1.value,
            type2: selectType2.value,
            type3: selectType3.value,
            itemName: itemName.value,
            id: store.state.account.userId,
            buySts: buySts.value
          }
        }).then((res) => {
          rowData.value = res.data
          columnApi.value.getColumn('rowNo').colDef.headerName = '총' + res.data.length + '건'
          gridApi.value.refreshHeader()
        })
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
      } else if (this.$store.state.datetype === 'firstDate') {
        const firstDate = new Date().getFullYear() + '-' +
          ('0' + (new Date().getMonth() - 2)).slice(-2) + '-' + '01'
        this.$store.commit('setFromDate', firstDate)
        this.$store.commit('setToDate', nowDate)
      }
      return 'ok'
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
.purchaseOrder {
  margin: 0 auto;
}
.modal{
  position: fixed;
  margin: auto;
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
  margin: 0 auto;
  margin-top: 30px;
  padding: 20px;
  background-color: white;
  min-height: 500px;
  z-index: 10;
  opacity: 1;
}
</style>
<style>
p {
  line-height: 100%;
}
/* 그리드 수직정렬 */
.ag-row .ag-cell {
  display: flex;
  justify-content: center;
  align-items: center;
}
</style>
