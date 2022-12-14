import { createStore } from 'vuex'
import createPersistedState from 'vuex-persistedstate'
import axios from 'axios'
import commonFunc from '@/service/CommonFunc'

export default createStore({
  state: {
    datetype: null,
    account: {
      userId: null,
      bizno: null,
      custType: null,
      custCode: null,
      custName: null,
      custAddress: null,
      companyCode: null
    },
    fromDate: null,
    toDate: null,
    agGridReg: [
      {
        rowNo: null,
        qtyField: null,
        boxField: null,
        totalQtyField: null,
        qtyPerBox: null
      }
    ],
    agGrid: {
      rowNo: 0,
      qtyField: 0,
      boxField: 0,
      totalQtyField: 0
    },
    nowStoreRowNum: 0,
    selectedMiddleDivCode: '01',
    selectedThirdDivCod: '001',
    nowSelectItem: 0,
    //  공지사항 현재페이지
    nowNoticePage: 1
  },
  getters: {
    getAccount: function (state) {
      return state.account
    },
    getNowMiddleDiv: function (state) {
      return state.selectedMiddleDivCode
    },
    getNowThirdDiv: function (state) {
      return state.selectedThirdDivCode
    },
    getNowStoreRowNum: function (state) {
      return state.nowStoreRowNum
    },
    getGridRowQty: function (state) {
      return state.agGrid.rowNo
    },
    getNoticeNowPage: function (state) {
      return state.nowNoticePage
    }
  },

  mutations: {
    setDate (state, payload) {
      state.datetype = payload
    },
    setAccount (state, payload) {
      state.account.userId = payload.userId
      state.account.bizno = payload.bizno
      state.account.custType = payload.custType
      state.account.custCode = payload.custCode
      state.account.custName = payload.custName
      state.account.custAddress = payload.custAddress
      state.account.companyCode = payload.companyCode
    },
    setFromDate (state, fromDate) {
      state.fromDate = fromDate
    },
    setToDate (state, toDate) {
      state.toDate = toDate
    },
    setRowNo (state, value) {
      state.agGrid.rowNo = value
    },
    setQtyField (state, value) {
      state.agGrid.qtyField = value
    },
    setBoxField (state, value) {
      state.agGrid.boxField = value
    },
    setTotalQtyField (state, value) {
      state.agGrid.totalQtyField = value
    },
    setSelectedMiddleDiv (state, value) {
      state.selectedMiddleDivCode = value
    },
    setSelectedThirdDiv (state, value) {
      state.selectedThirdDivCode = value
    },
    setNowStoreRowNum (state, value) {
      state.nowStoreRowNum = value
    },
    setQtyReg (state, qty) {
      state.agGridReg[state.nowStoreRowNum].qtyField = qty
    },
    setBoxyReg (state, box) {
      state.agGridReg[state.nowStoreRowNum].boxField = box
    },
    setTotalQtyReg (state, totalQty) {
      state.agGridReg[state.nowStoreRowNum].totalQtyField = totalQty
    },
    setNowSelectItem (state, data) {
      state.nowSelectItem = data
    },
    setNowticeNowPage (state, page) {
      state.nowNoticePage = page
    },
    // 주문하기의 선택한 행의 숫자를 셋 한다.
    setRowQty (state, data, isNewItem) {
      let isRowBe = false
      // 이전의 필드 상황과 새로 들어온 정보를 조합
      // 갯수를 구해온 다음 필드에 적용

      for (const row of this.state.agGridReg) {
        //  로우가 있는 경우 (이미 입력된 로우일 경우)
        if (row.rowNo === data.rowNo) {
          // console.log('BeRow')
          isRowBe = true
          state.nowStoreRowNum = data.rowNo
          let reqQty = 0
          let reqBox = 0
          if (data.qty) {
            reqQty = data.qty
          } else if (state.agGridReg[state.nowStoreRowNum].qtyField) {
            reqQty = state.agGridReg[state.nowStoreRowNum].qtyField
          }
          if (data.box) {
            reqBox = data.box
          } else if (state.agGridReg[state.nowStoreRowNum].boxField) {
            reqBox = state.agGridReg[state.nowStoreRowNum].boxField
          }

          const resData = commonFunc.methods.setTotalQty(reqQty, reqBox, state.nowSelectItem.qty)
          console.log(resData)

          const row = {
            rowNo: data.rowNo,
            qtyField: resData.resQty,
            boxField: resData.resBox,
            totalQtyField: resData.totalQty
          }
          this.state.agGridReg[data.rowNo] = row
          break
        }
      }
      // 새로 들어온 로우일 경우 (입력된적이 없는 로우일 경우)
      if (isRowBe === false) {
        state.nowStoreRowNum = data.rowNo
        let reqQty = 0
        let reqBox = 0
        if (data.qty) {
          reqQty = data.qty
        }
        if (data.box) {
          reqBox = data.box
        }

        const resData = commonFunc.methods.setTotalQty(reqQty, reqBox, state.nowSelectItem.qty)
        console.log(resData)

        const row = {
          rowNo: data.rowNo,
          qtyField: resData.resQty,
          boxField: resData.resBox,
          totalQtyField: resData.totalQty
        }
        this.state.agGridReg[data.rowNo] = row
      }
      // console.log(state.agGridReg)
    },
    // 주문하기 페이지 주문 배열을 초기화 시켜준다.
    resetAgRidReg (state) {
      state.nowStoreRowNum = 0
      state.agGridReg = []
      const data = {
        rowNo: 0,
        qtyField: 0,
        boxField: 0,
        totalQtyField: 0
      }
      state.agGridReg.push(data)
    },
    // 주문하기 페이지에서 아이템의 갯수를 받아서 새로운 배열을 생성하여 세팅한다.
    setItemStoreArr (state, length) {
      const arr = []
      const fillData = {
        rowNo: 0,
        qtyField: 0,
        boxField: 0,
        totalQtyField: 0
      }
      arr.length = length
      arr.fill(fillData)
      state.agGridReg = arr
    },

    // 총갯수를 넣으면 박스와 낱개로 환산해주는 함수
    getTotalToQtyBox (state, itemCode, totalQty, qtyPerBox) {
      if (qtyPerBox) {
        const box = parseInt(totalQty) / parseInt(qtyPerBox)
        const qty = parseInt(totalQty) % parseInt(qtyPerBox)
        return {
          qty: qty,
          box: box
        }
      } else {
        axios.get('/api/contract/getTotalToQtyBox', {
          params: {
            itemCode: itemCode,
            totalQty: parseInt(totalQty)
          }
        })
          .then((res) => {
            console.log(res)
          })
      }
    }
  },
  actions: {
  },
  modules: {
  },
  plugins: [
    createPersistedState()
  ]
})
