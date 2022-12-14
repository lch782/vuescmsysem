<template>
  <div class="content">
    <div class="">
      <div class="flex items-center h-12 mt-12">
        <button class="w-32 h-8 mx-2 bg-regist_third_category_button_bg rounded-md border border-regist_third_category_button_border text-white font-sm"
                @click="selectThirdDiv(i.dcode)" v-for="i in thirdDiv" :key="i">{{i.dcodename}}</button>
      </div>
      <div class="flex items-center h-12">
        <div class="flex items-center justify-center rounded-md border border-content_text_box_border bg-content_text_box_bg w-24 h-8 mx-2"
             @click="searchItem">
          <p>검색&nbsp;</p>
          <img src="@/assets/images/돋보기.png" alt="" width="15">
        </div>
        <select class="h-8 mx-3 border w-44" v-model="searchType">
          <option value="itemCode">상품코드</option>
          <option value="itemName">상품명</option>
          <option value="spec">규격</option>
          <option value="dan">단가</option>
          <option value="qty">수량</option>
<!--          <option value="supplyPrice">공급가액</option>-->
          <option value="vat">부가세액</option>
        </select>
        <input type="text" placeholder="검색어 입력" class="rounded-md w-search_box h-8 border"
               @keyup.enter="searchItem" v-model="searchWord"/>
      </div>
      <div class="flex">
        <div class="w-4/5">
          <div>
            <ag-grid-vue
              class="ag-theme-alpine ml-3"
              style="height: 500px"
              :columnDefs="columnDefs.value"
              :rowData="rowData.value"
              :defaultColDef="defaultColDef"
              :suppressCsvExport="true"
              rowSelection="single"
              animateRows="true"
              @cell-clicked="cellWarClicked"
              @grid-ready="onGridReady"
            >
            </ag-grid-vue>
          </div>
        </div>
        <div class="w-1/5 mx-3">
          <div class="w-full h-full rounded-md">
            <div class="border border-gray-300 border-2">
              <img :src="`/img/${selectedItemImage}`" alt="" width="250" height="250"/>
            </div>
            <div class="my-3 h-56 border border-gray-300 border-2 rounded-md px-3 py-3 font-noto_santos_r">
              <div class="flex items-center">
                <p class="py-1 text-basket_text_bottom_contractAmount">상품명 :&nbsp;</p>
                <p class="py-1 text-basket_text_bottom_normal text-sm">{{ selectedItemName }}</p>
              </div>
              <div class="flex items-center">
                <p class="py-1 text-basket_text_bottom_contractAmount">단가 : </p>
                <p class="py-1 text-basket_text_bottom_normal text-sm">{{ selectedItemDan }}</p>
              </div>
              <div class="flex items-center">
                <p class="py-1 text-basket_text_bottom_contractAmount">단위 :&nbsp;</p>
                <p class="py-1 text-basket_text_bottom_normal text-sm">{{ selectedItemUnit }}</p>
              </div>
              <div class="flex items-center">
                <p class="py-1 text-basket_text_bottom_contractAmount">제조일자 :&nbsp;</p>
                <p class="py-1 text-basket_text_bottom_normal text-sm">{{ selectedItemMandate }}</p>
              </div>
              <div class="flex items-center">
                <p class="py-1 text-basket_text_bottom_contractAmount">유통기한 :&nbsp;</p>
                <p class="py-1 text-basket_text_bottom_normal text-sm">{{ selectedItemShelfLife }}</p>
              </div>
              <div class="flex items-center">
                <p class="py-1 text-basket_text_bottom_contractAmount">보관조건 :&nbsp;</p>
                <p class="py-1 text-basket_text_bottom_normal text-sm">{{ selectedItemStorage }}</p>
              </div>
              <div class="flex items-center">
                <p class="py-1 text-basket_text_bottom_contractAmount">재고상황 : </p>
              </div>
            </div>
          </div>
<!--          <div class="flex justify-center items-center py-3">-->
<!--            <button class="border border-black rounded-md w-36 h-8" @click="putInBasket">장바구니 담기</button>-->
<!--          </div>-->
        </div>
      </div>
      <div class="mx-3 my-3 flex items-center">
        <img src="@/assets/images/basket_02.png" alt="" width="50"/>
        <p class="mx-3 font-noto_santos_m text-basket_title text-2xl">장바구니</p>
      </div>
      <div>
        <ag-grid-vue
          class="ag-theme-alpine mx-3"
          style="height: 500px"
          :columnDefs="columnDefsBasket.value"
          :rowData="rowDataBasket.value"
          :defaultColDef="defaultColDef"
          :suppressCsvExport="true"
          rowSelection="single"
          animateRows="true"
          @cell-clicked="cellWarClickedBasket"
          @grid-ready="onGridReady2"
        >
        </ag-grid-vue>
      </div>
    </div>
  </div>
</template>

<script>
import { AgGridVue } from 'ag-grid-vue3' // the AG Grid Vue Component
import { reactive, ref, onBeforeMount } from 'vue'
import 'ag-grid-community/styles/ag-grid.css' // Core grid CSS, always needed
import 'ag-grid-community/styles/ag-theme-alpine.css'
import axios from 'axios' // Optional theme CSS
import store from '@/store'
import numericCell from '@/components/AgGridNumericCell'

export default {
  name: 'RegistContractView',
  components: {
    AgGridVue
  },
  data () {
    return {
      // selectItemPrc: '',
      // selectItemAmt: '',
      // itemQty: '',

      // 2차 개정 이후 변수
      selectedMiddleDiv: 'I',
      selectedThirdDiv: 'P01',
      thirdDiv: [],
      searchWord: '',
      selectType1: '',
      selectType2: '',
      selectType3: '',
      selectedRowNum: 0,
      selectedItemCode: '',
      selectedItemCodeBefore: '',
      selectedItemInfo: '',
      selectedItemName: '',
      selectedItemImage: 'wainingLogo.png',
      selectedItemQty: 0,
      selectedItemDan: 0,
      selectedItemSpec: '',
      selectedItemUnit: '',
      selectedItemStorage: '',
      selectedItemMandate: '',
      selectedItemShelfLife: '',
      itemSummary: '',
      totalQty: 0,
      qtyField: 0,
      boxField: 0,
      itemQtyPerBox: 0,
      isNewPage: true,
      isNewSearch: false,
      isNewItem: true,
      nowStoreRowNum: 0,
      qtyFieldReg: 0,
      boxFieldReg: 0,
      totalQtyReg: 0,
      searchType: 'itemName'
    }
  },
  created () {
    this.$store.commit('resetAgRidReg')
  },

  mounted () {
    this.$store.commit('resetAgRidReg')
    this.selectedMiddleDiv = this.$store.getters.getNowMiddleDiv
    this.getThirdDivType()
    this.getCustInfo()
    this.searchItem()
    this.getContractBasket()
  },
  beforeUnmount () {
  },
  computed: {
    // 중분류의 변경을 스토어에서 가져온다.
    getMidDiv () {
      return this.$store.getters.getNowMiddleDiv
    },
    // 소분류의 변경을 스토어에서 가져온다.
    getThirdDiv () {
      return this.$store.getters.getNowThirdDiv
    },
    getNowStoreRowNum () {
      return this.$store.getters.getNowStoreRowNum
    },
    getQtyField () {
      return this.$store.state.agGridReg[this.getNowStoreRowNum].qtyField
    },
    getBoxField () {
      return this.$store.state.agGridReg[this.getNowStoreRowNum].boxField
    },
    getRowNo () {
      return this.$store.state.agGrid.rowNo
    },
    getNowRowTotalQty () {
      if (this.$store.state.agGridReg.length === 0) {
        store.commit('resetAgRidReg')
      }
      return this.$store.state.agGridReg[this.getNowStoreRowNum].totalQtyField
    },
    getIsNewItem () {
      return this.isNewItem
    }
  },
  // 스토어의 변경을 감시한다.
  watch: {
    getMidDiv () {
      this.selectedMiddleDiv = this.getMidDiv
      // console.log(this.selectedMiddleDiv)
      this.getThirdDivType(this.selectedMiddleDiv)
      this.searchItem()
    },
    getThirdDiv () {
      this.selectedThirdDiv = this.getThirdDiv
      // console.log(this.selectedThirdDiv)
      this.searchItem()
    },
    getNowStoreRowNum () {
      this.nowStoreRowNum = this.getNowStoreRowNum
    },
    getQtyField: function () {
      const data = this.gridApi.getSelectedRows()
      data[0].qtyFieldRegist = this.$store.state.agGridReg[this.$store.state.nowStoreRowNum].qtyField
      this.qtyFieldReg = data[0].qtyFieldRegist
      this.gridApi.applyTransaction({ update: data })
      // data[0].qty = parseInt(this.getQtyField) + parseInt(this.getBoxField * data[0].itemQty)
      // const resField = this.setTotalQty(this.getQtyField, this.getBoxField, this.itemQtyPerBox)
      //
      // store.commit('setQtyReg', resField.resQty)
      // store.commit('setBoxyReg', resField.resBox)
      // store.commit('setTotalQtyReg', resField.totalQty)
      // this.totalQtyReg = resField.totalQty
    },
    getBoxField: function () {
      const data = this.gridApi.getSelectedRows()
      data[0].boxFieldRegist = this.$store.state.agGridReg[this.$store.state.nowStoreRowNum].boxField
      this.boxFieldReg = data[0].boxFieldRegist
      this.gridApi.applyTransaction({ update: data })
      // data[0].qty = parseInt(this.getQtyField) + parseInt(this.getBoxField * data[0].itemQty)
      // this.gridApi.applyTransaction({ update: data })
      // const totalQty = TotalAmountCell.methods.setTotalQty(this.getQtyField, this.getBoxField, this.itemQtyPerBox)
      // const resField = this.setTotalQty(this.getQtyField, this.getBoxField, this.itemQtyPerBox)
      //
      // store.commit('setQtyReg', resField.resQty)
      // store.commit('setBoxyReg', resField.resBox)
      // store.commit('setTotalQtyReg', resField.totalQty)
      // this.totalQtyReg = resField.totalQty
    },
    getRowNo: function () {
      // store.commit('setQtyField', 0)
      // store.commit('setBoxField', 0)
    },
    getNowRowTotalQty () {
      const data = this.gridApi.getSelectedRows()
      data[0].qtyTotalReg = this.$store.state.agGridReg[this.nowStoreRowNum].totalQtyField
      this.totalQtyReg = data[0].qtyTotalReg
      this.gridApi.applyTransaction({ update: data })
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
    // 장바구를 불러온다.
    getContractBasket () {
      console.log('callBasket')
      axios.get('/api/contractBasket/contractBasketList', {
        params: {
          userId: store.getters.getAccount.userId
        }
      }).then((res) => {
        this.rowDataBasket.value = res.data
      })
    },
    // 아이템을 검색하는 함수
    searchItem (event, selectDiv) {
      this.selectType2 = this.selectedMiddleDiv
      this.selectType3 = this.selectedThirdDiv

      // 아이템을 검색한다
      axios.get('/api/contract/items/search', {
        params: {
          word: this.searchWord,
          searchType: this.searchType,
          type2: this.selectType2,
          type3: this.selectType3
        }
      }).then((res) => {
        this.rowData.value = res.data
        this.setItemArr(res.data.length)
        this.isNewSearch = true
      })
    },
    // 아이템 갯수에 따라서 배열을 가변적으로 초기화 세팅한다.
    setItemArr (length) {
      store.commit('setItemStoreArr', length)
    },
    // 소분류를 선택했을때 실행되는 함수
    selectThirdDiv (dcode) {
      this.$store.commit('resetAgRidReg')
      this.$store.commit('setSelectedThirdDiv', dcode)
      this.selectType3 = dcode
      this.isNewPage = true
    },
    resetSearch () {
      // this.selectType1 = ''
      this.selectType2 = ''
      this.selectType3 = ''
      this.searchWord = ''
      this.searchItem()
    },
    // 주문 그리드 클릭시 함수
    cellWarClicked (event) {
      this.$store.commit('setNowSelectItem', event.data)
      if (this.selectedItemCode === '' || this.selectedItemCode !== event.data.itemcode) {
        if (this.selectedItemCode === '') {
          this.isNewPage = true
          this.isNewSearch = true
          this.isNewItem = true
        }
        if (this.selectedItemCode !== '' && this.selectedItemCode !== event.data.itemcode) {
          this.isNewItem = false
        }
        this.selectedRowNum = event.node.rowIndex
        this.selectedItemCode = event.data.itemcode
        this.selectedItemInfo = event.data.iteminfo
        this.selectedItemName = event.data.itemname
        this.selectedItemUnit = event.data.unit
        this.selectedItemSpec = event.data.spec
        this.selectedItemDan = event.data.dan
        this.selectedItemStorage = event.data.storage
        if (event.data.storage === '001') {
          this.selectedItemStorage = '실온'
        } else if (event.data.storage === '002') {
          this.selectedItemStorage = '냉장'
        } else if (event.data.storage === '003') {
          this.selectedItemStorage = '냉동'
        }
        this.selectedItemImage = 'KakaoTalk_20221018_161511709_07.jpg'
        this.getItemPrice(event.data.itemcode)
        this.qtyField = 0
        this.boxField = 0
        this.itemQtyPerBox = event.data.qty
        // const data = {
        //   rowNo: event.node.rowIndex,
        //   qty: 0,
        //   box: 0,
        //   totalQty: 0,
        //   qtyPerBox: 0
        // }
        // store.commit('setRowQty', data, this.isNewItem)
      } else if (this.selectedItemCode && this.selectedItemCode === event.data.itemcode) {
        this.isNewPage = false
        this.isNewSearch = false
        this.isNewItem = false
        this.selectedRowNum = event.node.rowIndex
      }

      // 선택한 상품 장바구니에 담기
      if (event.column.colId === 'ContractBasket') {
        console.log(event.data)
        console.log(this.totalQtyReg)
        console.log(this.gridApi.getSelectedRows())

        const data = this.gridApi.getSelectedRows()
        // 선택한 행의 총 수량 체크
        if (!data[0].qtyTotalReg) {
          alert('수량을 정확히 입력해 주세요')
          return
        }
        // 최소 수량과 최대 수량 체크
        if (data[0].qtyTotalReg < event.data.minqty) {
          alert('최소구매 수량 이상 주문 가능 합니다.')
          return
        }
        if (data[0].qtyTotalReg > event.data.buylimit) {
          alert('최대 구매 가능 수량을 초과하였습니다.')
          return
        }
        axios.post('/api/contractBasket/putContractBasket', {
          userid: store.state.account.userId,
          itemcode: this.selectedItemCode,
          qty: this.totalQtyReg
        }).then(function (res) {
          this.getContractBasket()
        }).catch(function (err) {
          console.log(err)
          this.getContractBasket()
        })
        this.getContractBasket()
        alert('선택하신 상품이 정상적으로 장바구니에 담겼습니다.')
      }
    },
    // 장바구니 그리드 클릭시 함수
    cellWarClickedBasket (event) {
    },
    // 아이템의 가격을 가져온다.
    getItemPrice (itemcode) {
      axios.get('/api/contract/items/getItemPrice', {
        params: {
          custCode: store.state.account.custCode,
          itemcode: itemcode
        }
      }).then((res) => {
        this.selectItemDan = res.data[0].dan
      })
    },
    // 장바구니에 담는다.
    // putInBasket () {
    //   // const sumQty = (this.boxField * this.selectedItemQty) + this.qtyField
    //   axios.post('/api/contractBasket/putContractBasket', {
    //     userid: store.state.account.bizno,
    //     itemcode: this.selectedItemCode,
    //     qty: this.totalQtyReg
    //   }).then(function (res) {
    //     alert('선택하신 상품이 정상적으로 장바구니에 담겼습니다.')
    //   }).catch(function (err) {
    //     console.log(err)
    //   })
    // },
    // 세번째 분류를 가져온다.
    getThirdDivType () {
      this.thirdDiv = []
      axios.get('/api/contract/getThirdDivType', {
        params: {
          middleDiv: this.selectedMiddleDiv
        }
      })
        .then((res) => {
          for (let i = 0; i < res.data.length; i++) {
            this.thirdDiv.push(res.data[i][0])
            this.$store.commit('setSelectedThirdDiv', res.data[0][0].dcode)
          }
        })
    }
  },
  setup () {
    const gridApi = ref(null) // Optional - for accessing Grid's API
    const gridApi2 = ref(null)
    const iteminfo = ref(null)
    const itemname = ref(null)
    const itemcode = ref(null)
    const itemimage = ref(null)
    const qty = ref(null)
    const prc = ref(null)
    const rowData = reactive({
      value: []
    })
    const rowDataBasket = reactive({
      value: []
    })
    itemimage.value = 'wainingLogo.png'
    // Obtain API from grid's onGridReady event
    const onGridReady = (params) => {
      gridApi.value = params.api
    }
    const onGridReady2 = (params) => {
      gridApi2.value = params.api
    }
    // 주문하기 그리드
    const columnDefs = reactive({
      value: [
        {
          headerName: '상품코드',
          field: 'itemcode',
          cellClass: 'textCell',
          valueFormatter: params => params.data.itemcode
        },
        {
          headerName: '이미지',
          field: 'itemimage',
          cellClass: 'textCell',
          cellRenderer: (params) => '<img src="http://appdata.hungryapp.co.kr/data_file/data_img_m/202112/M163979800045320113.png/hungryapp/resize/500" width="40" height="40"/>'
          // cellRenderer: (params) => `<img src=${params.data.itemimage} width="40" height="40"/>`
        },
        { headerName: '상품명', field: 'itemname', cellClass: 'textCell' },
        { headerName: '규격', field: 'spec', cellClass: 'textCell' },
        { headerName: '단위', field: 'unit', cellClass: 'textCell' },
        {
          headerName: '단가',
          field: 'dan',
          valueFormatter: params => localPrice(params.data.dan),
          cellClass: 'numberCell'
        },
        {
          headerName: '최소주문수량',
          field: 'minqty',
          valueFormatter: params => localPrice(params.data.minqty),
          cellClass: 'numberCell'
        },
        {
          headerName: '낱개',
          field: 'qtyFieldRegist',
          cellEditor: numericCell,
          cellClass: 'textCell',
          editable: true
        },
        {
          headerName: '박스',
          field: 'boxFieldRegist',
          cellEditor: numericCell,
          cellClass: 'textCell',
          editable: true
        },
        {
          headerName: '총수량',
          field: 'qtyTotalReg',
          // cellRenderer: TotalAmountCell,
          // valueFormatter: params => (params.data.qtyField * params.data.boxField).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          // valueFormatter: params => (params.data.qtyField * params.data.boxField).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'textCell',
          editable: false
        },
        {
          headerName: '담기',
          field: 'ContractBasket',
          cellRenderer: function (params) {
            return '<div><button class="w-10 flex items-center"><img src="/img/basket.png" alt="" width="20"/></button></div>'
          },
          cellClass: 'textCell',
          editable: 'false'
        }
      ]
    })
    // 장바구니 그리드
    const columnDefsBasket = reactive({
      value: [
        // {
        //   headerCheckboxSelection: true,
        //   checkboxSelection: true,
        //   showDisabledCheckboxes: true
        // },
        { headerName: '상품코드', field: 'itemCode', cellClass: 'textCell' },
        {
          headerName: '이미지',
          field: 'itemimage',
          cellClass: 'textCell',
          cellRenderer: (params) => '<img src="http://appdata.hungryapp.co.kr/data_file/data_img_m/202112/M163979800045320113.png/hungryapp/resize/500" width="40" height="40"/>'
        },
        { headerName: '상품명', field: 'itemName', cellClass: 'textCell' },
        { headerName: '규격', field: 'spec', cellClass: 'textCell' },
        { headerName: '단위', field: 'unit', cellClass: 'textCell' },
        {
          headerName: '단가',
          field: 'dan',
          valueFormatter: params => localPrice(params.data.dan),
          cellClass: 'numberCell'
        },
        // {
        //   headerName: '낱개',
        //   field: 'qtyField',
        //   cellEditor: numericCell,
        //   cellClass: 'textCell',
        //   editable: true
        // },
        // {
        //   headerName: '박스',
        //   field: 'boxField',
        //   cellEditor: numericCell,
        //   cellClass: 'textCell',
        //   editable: true
        // },
        {
          headerName: '총수량',
          field: 'qty',
          // cellRenderer: TotalAmountCell,
          // valueFormatter: params => (params.data.qtyField * params.data.boxField).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          // valueFormatter: params => (params.data.qtyField * params.data.boxField).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell',
          editable: false
        },
        {
          headerName: '공급가',
          field: 'supplyprice',
          valueFormatter: params => (params.data.supplyprice).toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
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
          valueFormatter: params => params.data.amt.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ','),
          cellClass: 'numberCell',
          editable: 'false'
        }
        // {
        //   headerName: '삭제',
        //   field: 'listdelete',
        //   cellRenderer: function (params) {
        //     return '<div><button class="border rounded border-black w-10">삭제</button></button></div>'
        //   },
        //   cellClass: 'textCell',
        //   editable: false
        // }
      ]
    })
    // DefaultColDef sets props common to all Columns
    const defaultColDef = {
      sortable: true,
      // editable: true,
      flex: 1
    }

    onBeforeMount(() => {
      // store.commit('resetAgRidReg')
    })

    return {
      gridApi,
      gridApi2,
      onGridReady,
      onGridReady2,
      columnDefs,
      rowData,
      defaultColDef,
      iteminfo,
      itemname,
      itemcode,
      itemimage,
      qty,
      prc,
      columnDefsBasket,
      rowDataBasket,
      deselectRows: () => {
        gridApi.value.deselectAll()
      }
    }
  }
}
function localPrice (params) {
  if (params != null) {
    return params.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
  }
}

</script>

<style scoped>
.ag-theme-alpine {
  --ag-font-size: 11px;
  --ag-header-background-color: rgb(148, 198, 229, 0.19);
}

</style>
