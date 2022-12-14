<template>
  <div>
    <button
      @click="toggleAccordion()"
      class="flex items-center w-full pl-5 py-2 hover:bg-category_hover hover:font-noto_santos_b hover:font-semibold"
      :aria-expanded="isOpen"
      :aria-controls="`collapseB`"
    >
      <slot name="title" />
    </button>
    <div v-show="isOpen" :id="`collapseB`" @click="toggleAccordion()">
      <slot name="content" />
    </div>
  </div>
</template>

<script>
export default {
  name: 'SearchContractMenu',
  data () {
    return {
      isOpen: false
    }
  },
  methods: {
    toggleAccordion () {
      this.isOpen = !this.isOpen
    }
  }
}
</script>
