<template>
  <div class="ag-input-wrapper custom-date-filter" role="presentation" ref="flatpickr">
    <flat-pickr
      v-model="value"
      :config="config"
      class="form-control"
      placeholder="Select date"
      @on-change="getDate"
      name="date"/>
  </div>
</template>
<script>
import flatPickr from 'vue-flatpickr-component'
import 'flatpickr/dist/flatpickr.css'
export default {
  components: {
    flatPickr
  },
  data () {
    return {
      value: null,
      config: {
        wrap: true,
        altFormat: 'Y-m-d',
        altInput: true,
        dateFormat: 'Y-m-d'
      }
    }
  },
  methods: {
    getDate () {
      return this.value
    }
  }
}
</script>

<style scoped>

</style>
<style>
.form-control {
  width: 75px;
}
</style>
