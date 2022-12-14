<template>
  <div class="flex">
    <div class="flex-auto w-100% mt-10">
      <div class="mb-2 flex">
        <div class="mr-3 w-20 border rounded-md bg-[#E6E6E6] text-center" style="padding: 5px 0">발주일자</div>
        <date-picker/>
      </div>
      <div class="mb-2 flex">
        <div class="mr-3 w-20 border rounded-md bg-[#E6E6E6] text-center" style="padding: 5px 0">상품구분</div>
        <select class="mr-2 w-40 rounded-md border-solid border border-black" v-model="selectType1" @change="searchItem($event.target.value, 'select1')">
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
        <button class="w-20 mr-3 border rounded-md bg-[#B3B3B3] active:bg-[#E6E6E6] text-center" style="padding: 5px 0" @click="searchBtnClick">검색
          <span class="material-symbols-outlined " style="font-size: 15px;">search</span></button>
        <input class="rounded-md border-solid border border-black" style="width: 31rem" placeholder="상품명" v-model="itemName"/>
      </div>
    </div>
  </div>
  <div class="mt-4">
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
      :singleClickEdit="true"
      :editType="editType"
      rowSelection="multiple"
      @grid-ready="onGridReady"
    >
    </ag-grid-vue>
    <div class="flex justify-end mt-4">
      <div class="flex items-center">납품금액 합계 : </div>
      <input class="ml-2 p-3 h-7 w-36 border border-black" v-model="getInAmount">
      <button class="ml-2 p-0 h-7 w-24 rounded-md bg-[#333333] hover:bg-[#808080] p-1 text-white" @click="saveBtnClick">저장하기</button>
    </div>
  </div>
</template>

<script>
import { onMounted, reactive, ref } from 'vue'
import { AgGridVue } from 'ag-grid-vue3'
import axios from 'axios'
import store from '@/store'
import DatePicker from '@/components/DatePicker'
import TestDate from '@/components/TestDate'
import { getTypeDiv, getTypeDivInfo } from '@/assets/js/Contract'

export default {
  name: 'RegistDeliveryView',
  components: {
    AgGridVue,
    DatePicker
  },
  setup () {
    // 초기화
    store.commit('setDate', '')
    const state = reactive({
      startYear: '',
      startMonth: '',
      endYear: '',
      endMonth: ''
    })
    const gridApi = ref(null) // Optional - for accessing Grid's API
    const columnApi = ref(null)
    const getInAmount = ref(0)

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

    const getInAmountValue = () => {
      const rowData = gridApi.value.alignedGridsService.gridOptionsWrapper.gridOptions.rowData

      let tmpInAmount = 0
      for (let i = 0; i < rowData.length; i++) {
        if (rowData[i].buyCode === '소계') {
          if (rowData[i].inAmount !== undefined) {
            tmpInAmount += rowData[i].inAmount
          }
        }
      }
      getInAmount.value = tmpInAmount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }

    // Row Spanning 함수
    function rowSpan (params) {
      const showBuyCode = params.data.showBuyCode
      if (params.data.showBuyCode === '소계') {
        return 1
      } else {
        return rowData.value.filter((e) => e.buyCode === showBuyCode).length
      }
    }

    function getNumericCellEditor () {
      function isCharNumeric (charStr) {
        return !!/\d/.test(charStr)
      }
      function isKeyPressedNumeric (event) {
        const charCode = getCharCodeFromEvent(event)
        const charStr = String.fromCharCode(charCode)
        return isCharNumeric(charStr)
      }
      function getCharCodeFromEvent (event) {
        event = event || window.event
        return typeof event.which === 'undefined' ? event.keyCode : event.which
      }
      function NumericCellEditor () {}
      NumericCellEditor.prototype.init = function (params) {
        this.focusAfterAttached = params.cellStartedEdit
        this.eInput = document.createElement('input')
        this.eInput.setAttribute('type', 'number')
        this.eInput.setAttribute('min', '0')
        this.eInput.setAttribute('max', params.data.restQty)
        this.eInput.style.width = '100%'
        this.eInput.style.height = '100%'
        this.eInput.value = isCharNumeric(params.charPress) ? params.charPress : params.value
        let totalDataRowIndex = 0
        const groupArr = []
        for (let i = params.rowIndex; i >= 0; i--) {
          if (params.column.gridOptionsWrapper.gridOptions.rowData[i].buyCode === '소계') {
            break
          } else {
            groupArr.push(i)
          }
        }
        for (let j = params.rowIndex + 1; j < params.column.gridOptionsWrapper.gridOptions.rowData.length; j++) {
          if (params.column.gridOptionsWrapper.gridOptions.rowData[j].buyCode === '소계') {
            totalDataRowIndex = j
            break
          } else {
            groupArr.push(j)
          }
        }
        const totalData = params.column.gridOptionsWrapper.gridOptions.rowData[totalDataRowIndex]
        this.eInput.addEventListener('change', (event) => {
          const data = gridApi.value.getSelectedRows()
          data[0].inQty = event.target.value
          data[0].inAmount = data[0].dan * event.target.value
          let totalDataInQty = 0
          let totalDataInAmount = 0
          for (let x = 0; x < groupArr.length; x++) {
            let inQty = params.column.gridOptionsWrapper.gridOptions.rowData[groupArr[x]].inQty
            let inAmount = params.column.gridOptionsWrapper.gridOptions.rowData[groupArr[x]].inAmount
            if (inQty === undefined) {
              inQty = 0
            }
            if (inAmount === undefined) {
              inAmount = 0
            }
            totalDataInQty += parseInt(inQty)
            totalDataInAmount += parseInt(inAmount)
          }
          totalData.inQty = totalDataInQty
          totalData.inAmount = totalDataInAmount
          data.push(totalData)
          gridApi.value.applyTransaction({ update: data })

          getInAmountValue()
        })
        this.eInput.addEventListener('keyup', (event) => {
          if (!isKeyPressedNumeric(event)) {
            if (event.preventDefault) event.preventDefault()
          } else {
            const data = gridApi.value.getSelectedRows()
            if (data[0].restQty < event.target.value) {
              alert('납품 수량이 발주수량보다 많습니다.')
              event.target.value = data[0].restQty
            }
            data[0].inQty = event.target.value
            data[0].inAmount = data[0].dan * event.target.value
            let totalDataInQty = 0
            let totalDataInAmount = 0
            for (let x = 0; x < groupArr.length; x++) {
              let inQty = params.column.gridOptionsWrapper.gridOptions.rowData[groupArr[x]].inQty
              let inAmount = params.column.gridOptionsWrapper.gridOptions.rowData[groupArr[x]].inAmount
              if (inQty === undefined || inQty === '') {
                inQty = 0
              }
              if (inAmount === undefined || inAmount === '') {
                inAmount = 0
              }
              totalDataInQty += parseInt(inQty)
              totalDataInAmount += parseInt(inAmount)
            }
            totalData.inQty = totalDataInQty
            totalData.inAmount = totalDataInAmount
            data.push(totalData)
            gridApi.value.applyTransaction({ update: data })
          }
        })
      }
      NumericCellEditor.prototype.getGui = function () {
        return this.eInput
      }
      NumericCellEditor.prototype.afterGuiAttached = function () {
        if (this.focusAfterAttached) {
          this.eInput.focus()
          this.eInput.select()
        }
      }
      NumericCellEditor.prototype.getValue = function () {
        return this.eInput.value
      }
      return NumericCellEditor
    }

    const onGridReady = (params) => {
      gridApi.value = params.api
      columnApi.value = params.columnApi
    }

    const columnDefs = reactive({
      value: [
        {
          field: 'rowNo',
          headerName: '총0건',
          suppressMovable: true,
          maxWidth: 70,
          editable: false,
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
          editable: false,
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
          editable: false,
          cellClass: 'textCell'
        },
        {
          field: 'brand',
          headerName: '브랜드',
          suppressMovable: true,
          maxWidth: 100,
          editable: false,
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
          editable: false,
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
          editable: false,
          cellClass: 'textCell'
        },
        {
          field: 'qty',
          headerName: '발주수량',
          suppressMovable: true,
          maxWidth: 90,
          editable: false,
          cellClass: 'textCell'
        },
        {
          headerName: '발주단가',
          field: 'dan',
          suppressMovable: true,
          valueFormatter: params => params.data.dan.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 100,
          editable: false,
          cellClass: 'textCell'
        },
        {
          headerName: '공급가액',
          field: 'amount',
          suppressMovable: true,
          valueFormatter: params => params.data.amount.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 130,
          editable: false,
          cellClass: 'textCell'
        },
        {
          headerName: '부가세',
          field: 'vat',
          suppressMovable: true,
          valueFormatter: params => params.data.vat.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 120,
          editable: false,
          cellClass: 'textCell'
        },
        {
          headerName: '발주금액',
          field: 'sumAmt',
          suppressMovable: true,
          valueFormatter: params => params.data.sumAmt.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          maxWidth: 130,
          editable: false,
          cellClass: 'textCell'
        },
        {
          headerName: '납품일자',
          field: 'itemInDate',
          suppressMovable: true,
          editable: false,
          cellRenderer: (params) => {
            if (params.data.restQty !== 0 && params.data.restQty !== null) {
              const nowDate = new Date()
              const nowYear = nowDate.getFullYear()
              const nowMonth = ('0' + (1 + nowDate.getMonth())).slice(-2)
              const nowDay = ('0' + nowDate.getDate()).slice(-2)
              return nowYear + '-' + nowMonth + '-' + nowDay
            }
          },
          cellClass: 'textCell'
        },
        {
          field: 'restQty',
          headerName: '잔여수량',
          suppressMovable: true,
          maxWidth: 90,
          editable: false,
          cellClass: 'textCell'
        },
        {
          field: 'manDate',
          headerName: '제조일자',
          /*
          *   특정 상황에만 edit를 허용하고싶을떄 사용
          * */
          cellEditor: TestDate,
          editable: true
        },
        {
          field: 'inQty',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center" role="columnheader">납품<br>수량</span>' +
              '  </div>' +
              '</div>'
          },
          cellEditor: getNumericCellEditor(),
          suppressMovable: true,
          /*
          *   특정 상황에만 edit를 허용하고싶을떄 사용
          * */
          editable: function (params) {
            let editInQty = ''
            if (params.data.restQty !== 0 && params.data.restQty !== null) {
              editInQty = true
            }
            return editInQty === true
          },
          cellClass: 'textCell'
        },
        {
          field: 'inAmount',
          headerComponentParams: {
            template:
              '<div class="ag-cell-label-container" role="presentation">' +
              '  <div ref="eLabel" class="ag-header-cell-label" role="presentation">' +
              '    <span class="ag-header-cell-text text-center" role="columnheader">납품<br>금액</span>' +
              '  </div>' +
              '</div>'
          },
          suppressMovable: true,
          editable: false,
          valueFormatter: params => {
            let inAmount = ''
            if (params.value !== undefined) {
              inAmount = params.value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
            }
            return inAmount
          },
          cellClass: 'textCell',
          rowSpan: getInAmountValue
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
    const editType = 'fullRow'

    onMounted(() => {
      store.commit('setDate', 'firstDate')
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
        axios.get('/api/order/getCheckOrder', {
          params: {
            id: store.state.account.userId,
            fromDate: store.state.fromDate,
            toDate: store.state.toDate,
            type1: selectType1.value,
            type2: selectType2.value,
            type3: selectType3.value,
            itemName: itemName.value,
            buySts: '00'
          }
        }).then((res) => {
          rowData.value = res.data
          columnApi.value.getColumn('rowNo').colDef.headerName = '총' + res.data.length + '건'
          gridApi.value.refreshHeader()
        })
      })
    })
    return {
      getInAmount,
      getRowStyle,
      getRowHeight,
      state,
      columnDefs,
      rowData,
      defaultColDef,
      onGridReady,
      editType,
      gridApi,
      type1option,
      type2option,
      type3option,
      selectType1,
      selectType2,
      selectType3,
      itemName,
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
            id: store.state.account.userId,
            fromDate: store.state.fromDate,
            toDate: store.state.toDate,
            type1: selectType1.value,
            type2: selectType2.value,
            type3: selectType3.value,
            itemName: itemName.value,
            buySts: '00'
          }
        }).then((res) => {
          rowData.value = res.data
          columnApi.value.getColumn('rowNo').colDef.headerName = '총' + res.data.length + '건'
          gridApi.value.refreshHeader()
        })
      },
      saveBtnClick: () => {
        axios.post('/api/order/buyOrderInsert', {
          params: {
            rowData: rowData.value,
            bizNo: store.state.account.bizno
          }
        }).then((res) => {
          alert('정상적으로 등록되었습니다.')
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
<style>
.ag-cell-inline-editing {
  height:100%;
}
</style>
