<template>
  <Datepicker v-model="datepicker1"
              autoApply
              :dayNames="['일', '월', '화', '수', '목', '금', '토']"
              :format="format"
              @update:modelValue="fromDate"
  />
  <span class="ml-3 mr-3 mt-2"> ~ </span>
  <Datepicker v-model="datepicker2"
              autoApply
              :dayNames="['일', '월', '화', '수', '목', '금', '토']"
              :format="format"
              @update:modelValue="toDate"
  />
</template>

<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import store from '@/store'

export default {
  name: 'DatePicker',
  components: {
    Datepicker
  },
  setup () {
    // DatePicker로 인해 값이 변했을때
    const fromDate = (modelData) => {
      if (store.state.datetype !== '') {
        store.commit('setDate', '')
      }
      modelData = new Date(modelData)
      modelData = modelData.getFullYear() + '-' +
        ('0' + (1 + modelData.getMonth())).slice(-2) + '-' +
        ('0' + modelData.getDate()).slice(-2)
      store.commit('setFromDate', modelData)
    }
    const toDate = (modelData) => {
      if (store.state.datetype !== '') {
        store.commit('setDate', '')
      }
      modelData = new Date(modelData)
      modelData = modelData.getFullYear() + '-' +
        ('0' + (1 + modelData.getMonth())).slice(-2) + '-' +
        ('0' + modelData.getDate()).slice(-2)
      store.commit('setToDate', modelData)
    }
    return {
      fromDate,
      toDate
    }
  },
  computed: {
    dateBtnClick () {
      return this.$store.state.datetype
    }
  },
  watch: {
    dateBtnClick: function () {
      const nowDate = new Date()
      if (this.dateBtnClick === 'today') {
        this.datepicker1 = nowDate
        this.datepicker2 = nowDate
        store.commit('setFromDate', nowDate)
        store.commit('setToDate', nowDate)
      } else if (this.dateBtnClick === 'week') {
        const weekAgo = nowDate.getFullYear() + '-' + (nowDate.getMonth() + 1) + '-' + (nowDate.getDate() - 7)
        this.datepicker1 = weekAgo
        this.datepicker2 = nowDate
        store.commit('setFromDate', weekAgo)
        store.commit('setToDate', nowDate)
      } else if (this.dateBtnClick === 'month') {
        const monthAgo = nowDate.getFullYear() + '-' + (nowDate.getMonth() - 2) + '-' + nowDate.getDate()
        this.datepicker1 = monthAgo
        this.datepicker2 = nowDate
        store.commit('setFromDate', monthAgo)
        store.commit('setToDate', nowDate)
      } else if (this.dateBtnClick === 'firstDate') {
        const firstDate = nowDate.getFullYear() + '-' + (nowDate.getMonth() + 1) + '-01'
        this.datepicker1 = firstDate
        store.commit('setFromDate', firstDate)
        store.commit('setToDate', nowDate)
      } else if (this.dateBtnClick === 'month_1') {
        const monthAgo = nowDate.getFullYear() + '-' + (nowDate.getMonth()) + '-' + nowDate.getDate()
        this.datepicker1 = monthAgo
        this.datepicker2 = nowDate
        store.commit('setFromDate', monthAgo)
        store.commit('setToDate', nowDate)
      } else if (this.dateBtnClick === 'month_3') {
        const monthAgo = nowDate.getFullYear() + '-' + (nowDate.getMonth() - 2) + '-' + nowDate.getDate()
        this.datepicker1 = monthAgo
        this.datepicker2 = nowDate
        store.commit('setFromDate', monthAgo)
        store.commit('setToDate', nowDate)
      }
    }
  },
  data () {
    return {
      datepicker1: new Date(),
      datepicker2: new Date(),
      format: (date) => date.getFullYear().toString() + '-' +
        (date.getMonth() + 1).toString() + '-' +
        date.getDate().toString()
    }
  }
}

</script>

<style scoped>

</style>
