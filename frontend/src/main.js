import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import './styles/app.css' // Here
import './styles/custom.css' // Here
import 'tui-grid/dist/tui-grid.css'
import 'tui-date-picker/dist/tui-date-picker.css' // use datepicker
import NumericEditor from './assets/js/NumericEditor'
import VueCookies from 'vue-cookies'
import { quillEditor } from 'vue3-quill'
import vue3Apexcharts from 'vue3-apexcharts'
import jQuery from 'jquery'

createApp(App)
  .use(store)
  .use(router)
  .use(VueCookies)
  .use(NumericEditor)
  .use(jQuery)
  .use(vue3Apexcharts)
  .component('quillEditor', quillEditor)
  .mount('#app')
