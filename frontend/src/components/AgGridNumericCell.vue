<template>
  <input :ref="'number'" type="number" class="w-full" @keydown="onKeyDown($event)" min="0"
         @change="onKeyDown($event)" v-model="value"/>
</template>

<script>
import { nextTick } from 'vue'
import store from '@/store'

const KEY_BACKSPACE = 'Backspace'
const KEY_DELETE = 'Delete'
const KEY_ENTER = 'Enter'
const KEY_TAB = 'Tab'

export default {
  name: 'AgGridNumericCell',
  methods: {
    getValue () {
      return this.value
    },

    isCancelBeforeStart () {
      return this.cancelBeforeStart
    },

    setInitialState (params) {
      this.rowData = params.data
      let startValue

      if (params.eventKey === KEY_BACKSPACE || params.eventKey === KEY_DELETE) {
        // if backspace or delete pressed, we clear the cell
        startValue = ''
      } else if (params.charPress) {
        // if a letter was pressed, we start with the letter
        startValue = params.charPress
      } else {
        // otherwise we start with the current value
        startValue = params.value
      }

      this.value = startValue
    },

    // will reject the number if it greater than 1,000,000
    // not very practical, but demonstrates the method.
    isCancelAfterEnd () {
      return this.value > 1000000
    },
    onKeyDown (event) {
      console.log(event.key)
      if (event.key === 'Escape') {
        return
      } else {
        if (isNaN(this.value) || !this.value) {
          this.value = null
          return
        }
        if (!isNaN(this.value) && event.key === 'Enter') {
          // console.log(event.target.parentNode.__agComponent.column.colId)
          // 장바구니 용
          // store.commit('setRowNo', event.target.parentNode.__agComponent.rowCtrl.rowNode.rowIndex)
          if (event.target.parentNode.__agComponent.column.colId === 'qtyField') {
            store.commit('setQtyField', event.target.value)
            return
          } else if (event.target.parentNode.__agComponent.column.colId === 'boxField') {
            store.commit('setBoxField', event.target.value)
            return
          }
          // 주문하기 용

          let itemQty
          let itemBox
          if (event.target.parentNode.__agComponent.column.colId.includes('qtyFieldRegist')) {
            itemQty = event.target.value
          } else if (event.target.parentNode.__agComponent.column.colId.includes('boxFieldRegist')) {
            itemBox = event.target.value
          }
          const data = {
            rowNo: event.target.parentNode.__agComponent.rowCtrl.rowNode.rowIndex,
            qty: itemQty,
            box: itemBox
          }
          store.commit('setRowQty', data, this.isNewItem)
        }
      }
      if (this.isLeftOrRight(event) || this.deleteOrBackspace(event)) {
        event.stopPropagation()
        return
      }
      if (
        !this.finishedEditingPressed(event) &&
        !this.isKeyPressedNumeric(event)
      ) {
        if (event.preventDefault) event.preventDefault()
      }
    },
    isCharNumeric (charStr) {
      return /\d/.test(charStr)
    },
    isKeyPressedNumeric (event) {
      const charStr = event.key
      return this.isCharNumeric(charStr)
    },
    finishedEditingPressed (event) {
      const key = event.key
      return key === KEY_ENTER || key === KEY_TAB
    },
    deleteOrBackspace (event) {
      return [KEY_DELETE, KEY_BACKSPACE].indexOf(event.key) > -1
    },
    isLeftOrRight (event) {
      return ['ArrowLeft', 'ArrowRight'].indexOf(event.key) > -1
    },
    resetField () {
      this.rowData.qtyField = 0
      this.rowData.boxField = 0
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
    },
    getQtyFieldReg () {
      return this.$store.state.agGridReg[this.$store.state.nowStoreRowNum].qtyField
    },
    getBoxFieldReg () {
      return this.$store.state.agGridReg[this.$store.state.nowStoreRowNum].boxField
    }
  },
  watch: {
    getQtyField: function () {
      this.rowData.qtyField = this.$store.state.agGrid.qtyField
      this.rowData.boxField = this.$store.state.agGrid.boxField
      this.value = this.$store.state.agGrid.qtyField
    },
    getBoxField: function () {
      this.rowData.qtyField = this.$store.state.agGrid.qtyField
      this.rowData.boxField = this.$store.state.agGrid.boxField
      this.value = this.$store.state.agGrid.boxField
    },
    getRowNo: function () {
      this.rowNo = this.$store.state.agGrid.rowNo
    },
    getQtyFieldReg: function () {
      this.value = this.$store.state.agGridReg[this.$store.state.nowStoreRowNum].qtyField
    },
    getBoxFieldReg: function () {
      this.value = this.$store.state.agGridReg[this.$store.state.nowStoreRowNum].boxField
    }
  },
  created () {
    this.setInitialState(this.params)

    // only start edit if key pressed is a number, not a letter
    this.cancelBeforeStart =
      this.params.charPress && '1234567890'.indexOf(this.params.charPress) < 0
  },
  mounted () {
    nextTick(() => {
      // need to check if the input reference is still valid - if the edit was cancelled before it started there
      // wont be an editor component anymore
      if (this.$refs.input) {
        this.$refs.input.focus()
      }
    })
  },
  data () {
    return {
      rowNo: 0,
      value: null,
      valueQty: null,
      valueBox: null,
      cancelBeforeStart: true,
      rowData: null
    }
  }
}
</script>

<style scoped>

</style>
