<template>
  <div class="content">
    <div class="mx-3 my-3 flex items-center">
      <img src="@/assets/images/basket_02.png" alt="" width="50"/>
      <p class="mx-3 font-noto_santos_m text-basket_title text-2xl">장바구니</p>
    </div>
    <div class="flex mx-3 my-3">
      <div class="w-28 h-8 bg-content_text_box_bg border border-content_text_box_border rounded-md flex justify-center items-center">배송지</div>
      <input type="text" readonly class="w-delivery_field mx-3 border border-content_text_box_border" v-model="this.custAddress">
      <div class="w-28 h-8 bg-content_text_box_bg border border-content_text_box_border rounded-md flex justify-center items-center">SMS연락처</div>
      <input type="text" class="w-sms_field mx-3 border border-content_text_box_border" placeholder="연락처 입력" v-model="this.smsNumber">
    </div>
    <div class="flex px-3">
      <button class="w-28 h-8 ml-auto rounded-md text-white bg-delete_selected_btn" @click="deleteSelectedItem">선택항목삭제</button>
    </div>
<!--    <div class="px-3 py-3 mx-3 border border-gray-500 rounded-md flex items-center">-->
<!--      <button class="border rounded-md ml-auto w-28 h-8" @click="deleteBasketAll">전체삭제</button>-->
<!--      <button class="border rounded-md w-28 h-8" @click="contractConfirmation">주문확정</button>-->
<!--    </div>-->
    <div class="mx-3 my-3">
      <ag-grid-vue
        class="ag-theme-alpine"
        style="height: 500px"
        :columnDefs="columnDefs.value"
        :rowData="rowData.value"
        :defaultColDef="defaultColDef"
        :suppressCsvExport="true"
        rowSelection='multiple'
        animateRows="true"
        @cell-clicked="cellWasClicked"
        @grid-ready="onGridReady"
      >
      </ag-grid-vue>
    </div>
    <div class="flex items-center mx-3 my-3 h-20 border border-2">
      <p class="ml-auto text-2xl text-basket_text_bottom_normal">여신한도</p>
      <p class="mx-3 text-3xl text-basket_text_bottom_number">{{creditLimit.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')}}</p>
      <p class="mr-3 text-2xl text-basket_text_bottom_won">원</p>
    </div>
    <div class="flex items-center h-20 mx-3 bg-basket_bottom_box_bg text-2xl">
      <p class="w-32 ml-4 text-3xl text-noto_santos_r text-basket_text_bottom_normal">주문내역</p>
      <div class="flex noto_santos_m ml-96">
        <p class="w-24 text-basket_text_bottom_normal">공급가액</p>
        <p class="mx-2 text-basket_text_bottom_number">{{totalDan.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')}}</p>
        <p class="text-basket_text_bottom_won">원</p>
      </div>
      <div class="flex noto_santos_m mx-3">
        <p class="w-24 text-basket_text_bottom_normal">부가세액</p>
        <p class="mx-2 text-basket_text_bottom_number">{{totalVat.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')}}</p>
        <p class="text-basket_text_bottom_won">원</p>
      </div>
      <div class="flex noto_santos_m ml-auto mr-4">
        <p class="w-24 text-basket_text_bottom_contractAmount">주문금액</p>
        <p class="mx-2 text-basket_text_bottom_number_total">{{totalBill.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')}}</p>
        <p class="text-basket_text_bottom_won">원</p>
      </div>
    </div>
    <div class="flex items-center">
      <button class="w-36 h-10 ml-auto my-3 mx-3 flex justify-center items-center bg-basket_request_select_item border border-2 text-white rounded-md"
      @click="putSelectItems">
        선택상품 주문요청</button>
    </div>
  </div>
</template>

<script>
import { AgGridVue } from 'ag-grid-vue3' // the AG Grid Vue Component
import { onMounted, reactive, ref } from 'vue'
import 'ag-grid-community/styles/ag-grid.css' // Core grid CSS, always needed
import 'ag-grid-community/styles/ag-theme-alpine.css'
import axios from 'axios' // Optional theme CSS
import store from '@/store'
import numericCell from '@/components/AgGridNumericCell'
import commonFunc from '@/service/CommonFunc'

export default {
  name: 'ContractBasketView',
  components: {
    AgGridVue
  },
  data () {
    return {
      // totalDan: 0,
      // totalVat: 0,
      // totalBill: 0,
      // creditLimit: 0,
      // afterContractCredit: 0,
      isUpdated: false,
      custAddress: ''
    }
  },
  setup () {
    const custName = ref(null)
    const gridApi = ref(null) // Optional - for accessing Grid's API
    const columnApi = ref(null)
    const totalDan = ref(0)
    const totalVat = ref(0)
    const totalBill = ref(0)
    const creditLimit = ref(0)
    const afterContractCredit = ref(0)
    const smsNumber = ref(null)

    // Obtain API from grid's onGridReady event
    const onGridReady = (params) => {
      gridApi.value = params.api
      columnApi.value = params.columnApi
    }
    const rowData = reactive({
      value: []
    })

    // Each Column Definition results in one Column.
    const columnDefs = reactive({
      value: [
        {
          headerCheckboxSelection: true,
          checkboxSelection: true,
          showDisabledCheckboxes: true,
          maxWidth: 50
        },
        { headerName: '상품코드', field: 'itemCode', cellClass: 'textCell', editable: false },
        {
          headerName: '상품이미지',
          field: 'itemimage',
          cellClass: 'textCell',
          cellRenderer: (params) => '<img src="http://appdata.hungryapp.co.kr/data_file/data_img_m/202112/M163979800045320113.png/hungryapp/resize/500" width="40" height="40"/>',
          // cellRenderer: (params) => `<img src=${params.data.itemimage} />`
          editable: false
        },
        { headerName: '상품명', field: 'itemName', cellClass: 'textCell', editable: false },
        { headerName: '규격', field: 'spec', cellClass: 'textCell', editable: false },
        { headerName: '단위', field: 'unit', cellClass: 'textCell', editable: false },
        {
          headerName: '단가',
          field: 'dan',
          valueFormatter: params => params.data.dan.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell',
          editable: false
        },
        {
          headerName: '낱개',
          field: 'qtyField',
          cellEditor: numericCell,
          cellClass: 'textCell'
        },
        {
          headerName: '박스',
          field: 'boxField',
          cellEditor: numericCell,
          cellClass: 'textCell'
        },
        {
          headerName: '주문수량',
          field: 'qty',
          cellRenderer: function (params) {
            return '<div><span>' + params.data.qty + '</span></div>'
          },
          // valueFormatter: params => (params.data.qtyField * params.data.boxField).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell',
          editable: false
        },
        // {
        //   headerName: '주문금액',
        //   field: 'amt',
        //   valueFormatter: params => ((params.data.dan * params.data.qty) + (params.data.dan * params.data.qty * 0.1)).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
        //   editable: false,
        //   cellClass: 'numberCell'
        // },
        {
          headerName: '공급가',
          field: 'supplyprice',
          valueFormatter: params => (params.data.dan * params.data.qty).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell',
          editable: 'false'
        },
        {
          headerName: '부가세',
          field: 'surtax',
          valueFormatter: params => (params.data.surtax).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell',
          editable: 'false'
        },
        {
          headerName: '결제금액',
          field: 'amt',
          // valueFormatter: params => params.data.amt.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          valueFormatter: params => (params.data.amt).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell',
          editable: 'false'
        },
        // {
        //   headerName: '삭제',
        //   field: 'listdelete',
        //   cellRenderer: function (params) {
        //     return '<div><button class="border rounded border-black w-10">삭제</button></button></div>'
        //   },
        //   cellClass: 'textCell',
        //   editable: false
        // },
        {
          field: 'userid',
          hide: true
        }
      ]
    })

    // DefaultColDef sets props common to all Columns
    const defaultColDef = {
      sortable: true,
      editable: true,
      flex: 1
    }

    function initRowItemQtyBox () {
      const data = gridApi.value.getSelectedRows()
      data[0].qtyField = 0
      data[0].boxField = 0
      data[0].qty = 0
      gridApi.value.applyTransaction({ update: data })
    }

    function getTotalBillSetup () {
      axios.get('/api/contractBasket/getTotalBill', {
        params: {
          userId: store.state.account.userId
        }
      }).then((res) => {
        creditLimit.value = res.data.creditLimit
        totalDan.value = res.data.totalDan
        totalVat.value = res.data.totalVat
        totalBill.value = res.data.totalBill
        afterContractCredit.value = res.data.afterContractCredit
      })
    }

    // 장바구니의 선택한 아이템 삭제
    function deleteSelectedItem () {
      if (gridApi.value.getSelectedRows().length > 0) {
        axios.post('/api/contractBasket/deleteSelectedItem', {
          params: {
            rowData: gridApi.value.getSelectedRows(),
            userId: store.state.account.userId,
            custCode: store.state.account.custCode
          }
        }).then((res) => {
          console.log(res)
          getContractBasketList()
          getTotalBillSetup()
        })
      } else {
        alert('삭제 할 상품을 선택해 주세요.')
      }
    }

    function getContractBasketList () {
      axios.get('/api/contractBasket/contractBasketList', {
        params: {
          userId: store.getters.getAccount.userId
        }
      }).then((res) => {
        rowData.value = res.data
      })
    }

    onMounted(() => {
      axios.get('/api/contractBasket/contractBasketList', {
        params: {
          userId: store.getters.getAccount.userId
        }
      }).then((res) => {
        rowData.value = res.data
      })
      getTotalBillSetup()
    })

    return {
      gridApi,
      onGridReady,
      columnDefs,
      totalDan,
      totalVat,
      totalBill,
      creditLimit,
      afterContractCredit,
      smsNumber,
      rowData,
      defaultColDef,
      deleteSelectedItem,
      getTotalBillSetup,
      custName,
      cellWasClicked: (event) => { // Example of consuming Grid Event
        if (store.state.agGrid.rowNo !== event.node.rowIndex) {
          initRowItemQtyBox()
        }
        store.commit('setRowNo', event.node.rowIndex)
        store.commit('setNowSelectItem', event.data)
        if (event.event.target.nodeName === 'BUTTON') {
          // 전체삭제 시 그리드 초기화 rowData.value = []
          axios.delete('/api/contractBasket/contractBasketListDelete', {
            params: {
              bizNo: store.state.account.bizno, // 스토어값 들고오기
              itemCode: event.data.itemCode
            }
          }).then(res => {
            // DB에서 데이터를 먼저 삭제한 후 성공 시 그리드에서 행 삭제
            gridApi.value.applyTransaction({ remove: gridApi.value.getSelectedRows() })
          }).catch(err => {
            // handle error
            console.log(err)
          }).then(() => {
            // always executed
          })
        }
        // this.getTotalBill()
      },
      deselectRows: () => {
        gridApi.value.deselectAll()
      },
      // 장바구니 아이템 to 선택항목 주문
      putSelectItems: () => {
        if (gridApi.value.getSelectedRows().length > 0) {
          if (smsNumber.value === null) {
            alert('SMS 연락처를 입력해 주세요.')
            return
          }
          axios.post('/api/contractBasket/putSelectedContractBasket', {
            params: {
              rowData: gridApi.value.getSelectedRows(),
              bizNo: store.state.account.bizno,
              custCode: store.state.account.custCode,
              smsNumber: smsNumber.value
            }
          }).then((res) => {
            // console.log(res.status)
            deleteSelectedItem()
            getTotalBillSetup()
            alert('선택하신 상품이 정상적으로 주문되었습니다.')
          })
        } else {
          alert('주문 할 상품을 선택해 주세요.')
        }
      }
    }
  },
  methods: {
    // 사용자 정보 불러오기
    getCustInfo () {
      axios.get('/api/account/custInfo', {
        params: {
          userId: store.state.account.userId
        }
      }).then((res) => {
        this.custAddress = res.data.address
      })
    },
    async deleteBasketAll () {
      if (confirm('전체삭제 하시겠습니까?')) {
        axios.delete('/api/contractBasket/contractBasketListDeleteAll', {
          params: {
            bizNo: store.state.account.bizno // 스토어값 들고오기
          }
        }).then(res => {
          this.rowData.value = []
          // handle success
        }).catch(err => {
          // handle error
          console.log(err)
        }).then(() => {
          // always executed
        })
      }
      this.getTotalBill()
    },
    // 모든 상품의 주문
    async contractConfirmation () {
      axios.post('/api/contractBasket/contractConfirmation', {
        params: {
          rowData: this.rowData.value,
          custCode: store.state.account.custCode,
          custName: store.state.account.custName
        }
      }).then((res) => {
        if (res.status === 200) {
          axios.delete('/api/contractBasket/contractBasketListDeleteAll', {
            params: {
              bizNo: store.state.account.bizno // 스토어값 들고오기
            }
          }).then(res => {
            this.rowData.value = []
            // handle success
          }).catch(err => {
            // handle error
            console.log(err)
          }).then(() => {
            // always executed
          })
        }
      })
      this.getTotalBill()
    },
    // 주문금액을 불러온다.
    getTotalBill () {
      axios.get('/api/contractBasket/getTotalBill', {
        params: {
          userId: store.state.account.userId
        }
      }).then((res) => {
        this.$data.creditLimit = res.data.creditLimit
        this.$data.totalDan = res.data.totalDan
        this.$data.totalVat = res.data.totalVat
        this.$data.totalBill = res.data.totalBill
        this.$data.afterContractCredit = res.data.afterContractCredit
      })
    }
  },
  computed: {
    getQtyField () {
      return this.$store.state.agGrid.qtyField
    },
    getBoxField () {
      return this.$store.state.agGrid.boxField
    },
    getRowNo () {
      return this.$store.state.agGrid.rowNo
    }
  },
  watch: {
    getQtyField: function () {
      if (this.gridApi !== null) {
        const data = this.gridApi.getSelectedRows()
        data[0].qty = parseInt(this.getQtyField) + parseInt(this.getBoxField * data[0].itemQty)

        const resData = commonFunc.methods.getTotalToQtyBox(
          data[0].itemCode, data[0].qty, data[0].itemQty, true
        )
        store.commit('setQtyField', resData.qty)
        store.commit('setBoxField', resData.box)
        data[0].qtyField = this.$store.state.agGrid.qtyField
        data[0].boxField = this.$store.state.agGrid.boxField
        console.log('qtyfield = ' + data[0].qtyField)
        console.log('boxfield = ' + data[0].boxField)
        this.gridApi.applyTransaction({ update: data })
      }
    },
    getBoxField: function () {
      if (this.gridApi !== null) {
        const data = this.gridApi.getSelectedRows()
        data[0].qty = parseInt(this.getQtyField) + parseInt(this.getBoxField * data[0].itemQty)

        const resData = commonFunc.methods.getTotalToQtyBox(
          data[0].itemCode, data[0].qty, data[0].itemQty, true
        )
        store.commit('setQtyField', resData.qty)
        store.commit('setBoxField', resData.box)
        data[0].qtyField = this.$store.state.agGrid.qtyField
        data[0].boxField = this.$store.state.agGrid.boxField
        console.log('qtyfield = ' + data[0].qtyField)
        console.log('boxfield = ' + data[0].boxField)
        this.gridApi.applyTransaction({ update: data })
      }
    },
    getRowNo: function () {
      store.commit('setQtyField', 0)
      store.commit('setBoxField', 0)
    }
  },
  mounted () {
    this.custAddress = this.$store.state.account.custAddress
    this.getCustInfo()
    this.getTotalBill()
    store.commit('setRowNo', '')
    store.commit('setQtyField', null)
    store.commit('setBoxField', null)
  }
}
</script>

<style scoped>
/* 인풋 넘버 타입 숫자 안보이게 */
/* Chrome, Safari, Edge, Opera */
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}
.ag-theme-alpine {
  --ag-header-background-color: rgb(148, 198, 229, 0.19);
}
</style>
