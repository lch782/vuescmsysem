<template>
  <quill-editor
    v-model:value="state.content"
    :options="state.editorOption"
    :disabled="state.disabled"/>
  <!--    @blur="onEditorBlur($event)"-->
  <!--    @focus="onEditorFocus($event)"-->
  <!--    @ready="onEditorReady($event)"-->
  <!--    @change="onEditorChange($event)"-->
</template>

<script>
import { reactive } from 'vue'
import { quillEditor } from 'vue3-quill'
import Quill from 'quill'
// import ImageUploader from "quill.imageUploader.js";
import ImageUploader from 'quill-image-uploader'
import axios from 'axios'

Quill.register('modules/imageUploader', ImageUploader)

export default {
  name: 'test',
  components: {
    quillEditor
  },
  setup () {
    const state = reactive({
      content: '<p>2333</p>',
      _content: '',
      editorOption: {
        placeholder: 'core',
        modules: {
          imageUploader: {
            upload: (file) => {
              return new Promise((resolve, reject) => {
                const uploadfile = file
                const formdata = new FormData()
                formdata.append('file', uploadfile)
                axios({
                  method: 'post',
                  url: '/api/notice/uploadFile',
                  data: formdata
                }).then((res) => {
                  resolve(res.data)
                })
              })
            }
          }
          // toolbars: [
          // custom toolbars options
          // will override the default configuration
          // ],
          // other moudle options here
          // otherMoudle: {}
        }
        // more options
      },
      disabled: false
    })
    return { state, quillEditor }
  }
}
</script>

<style scoped>

</style>
