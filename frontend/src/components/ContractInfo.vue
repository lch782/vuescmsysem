<template>
  <div class="p-1">
    <p class="text-xl m-1">주문내역</p>
    <ul class="border border-black w-2/3">
      <li class="flex border-b border-black leading-8">
        <span class="bg-gray-200 border-r border-black w-1/6 p-1 text-center">주문상품</span>
        <span class="w-5/6 p-1">생크림-홉라(1L)-무가당</span>
      </li>
      <li class="flex border-b border-black leading-8">
        <span class="bg-gray-200 border-r border-black w-1/6 p-1 text-center">주문일자</span>
        <span class="w-2/6 p-1 text-center">2022-10-13</span>
        <span class="bg-gray-200 border-l border-r border-black w-1/6 p-1 text-center">주문번호</span>
        <span class="w-2/6 p-1 text-center">2022101382178</span>
      </li>
      <li class="flex border-b border-black leading-8">
        <span class="bg-gray-200 border-r border-black w-1/6 p-1 text-center">요청사항</span>
        <span class="w-5/6 p-1 text-center"></span>
      </li>
      <li class="flex leading-8">
        <span class="bg-gray-200 border-r border-black w-1/6 p-1 text-center">배송지변경</span>
        <span class="w-5/6 p-1">
          <select>
            <option>고고커피 - 부산 금정구 중앙대로 1784(부곡동)</option>
          </select>
          <button class="bg-stone-500 text-white px-1 mx-1">저장</button>
        </span>
      </li>
    </ul>
  </div>
  <div class="w-11/12 p-1">
    <p class="text-xl m-1">상품 상세</p>
    <ag-grid-vue
      class="ag-theme-alpine"
      style="height: 100px"
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
// import { reactive, onMounted, ref } from 'vue'
import { reactive, ref } from 'vue'
import 'ag-grid-community/styles/ag-grid.css' // Core grid CSS, always needed
import 'ag-grid-community/styles/ag-theme-alpine.css' // Optional theme CSS
export default {
  name: 'ContractInfo',
  components: {
    AgGridVue
  },
  setup () {
    const gridApi = ref(null) // Optional - for accessing Grid's API

    // Obtain API from grid's onGridReady event
    const onGridReady = (params) => {
      gridApi.value = params.api
    }
    const rowData = reactive({
      value: [
        {
          itemcode: '221IP012',
          itemname: '생크림-홉라(1L)-무가당',
          itemstandard: '1kg*12ea/box',
          itemunit: 'KG',
          orderquantity: '36',
          itemweight: '36',
          itemprice: '3850',
          sumamount: '',
          orderstate: '배송준비중'
        }
      ]
    })

    // Each Column Definition results in one Column.
    const columnDefs = reactive({
      value: [
        { headerName: '상품코드', field: 'itemcode', cellClass: 'textCell' }, // field : 'DTO와 매칭'
        { headerName: '상품명', field: 'itemname', cellClass: 'textCell' },
        { headerName: '규격', field: 'itemstandard', cellClass: 'textCell' },
        { headerName: '단위', field: 'itemunit', cellClass: 'textCell' },
        { headerName: '주문수량', field: 'orderquantity', cellClass: 'textCell' },
        { headerName: '중량', field: 'itemweight', cellClass: 'textCell' },
        {
          headerName: '단가',
          field: 'itemprice',
          valueFormatter: params => params.data.itemprice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell'
        },
        {
          headerName: '총금액',
          field: 'sumamount',
          valueFormatter: params => (params.data.itemprice * params.data.orderquantity).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell'
        },
        { headerName: '주문상태', field: 'orderstate', cellClass: 'textCell' }
      ]
    })

    // DefaultColDef sets props common to all Columns
    const defaultColDef = {
      sortable: true,
      flex: 1
    }
    /*
    // Example load data from sever
    onMounted(() => {
      fetch('https://www.ag-grid.com/example-assets/row-data.json')
        .then((result) => result.json())
        .then((remoteRowData) => (rowData.value = remoteRowData))
    })
*/
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
  }
}
</script>

<style scoped>

</style>
