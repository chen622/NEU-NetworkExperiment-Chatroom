<template>
  <md-list-item v-if="(online=='wu') ? false : true">
    <md-avatar>
      <img src="https://placeimg.com/40/40/people/5" alt="People">
    </md-avatar>

    <span class="md-list-item-text">{{online}}</span>

    <md-button class="md-icon-button md-list-action" @click="newChat(online)">
      <md-icon :class="isMessage">chat_bubble</md-icon>
    </md-button>
  </md-list-item>
</template>

<script>
export default {
  name: 'online',
  props: ['online'],
  methods: {
    newChat (name) {
      var _this = this
      if (name === _this.$store.state.name + ' (自己)') {
        alert('自己和自己聊天并不好玩!')
      } else {
        if (this.$store.state.nextChatId !== 0) {
          var already = this.$store.state.singleChat
          for (var j = 0; j < this.$store.state.nextChatId; j++) {
            if (already[j].name == null || already[j].name === undefined || already[j].name === '') {
              continue
            }
            console.log(name + ' ' + already[j].name)
            if (name === already[j].name) {
              break
            }
          } if (j === this.$store.state.nextChatId) {
            this.$store.commit('addSingleChat', name)
          } else {
            alert('请不要重复发起聊天请求')
          }
        } else {
          this.$store.commit('addSingleChat', name)
        }
      }
    }
  },
  computed: {
    count () {
      var msg = this.$store.state.messages
      var returnMsg = 0
      for (var i = 0; i < this.$store.state.nextTodoId; i++) {
        if ((msg[i].type === this.$store.state.name && msg[i].sender === this.online)) {
          returnMsg++
        }
      }
      return returnMsg
    },
    isMessage () {
      if (this.count === 0) {
        return 'md-primary'
      } else {
        return 'md-accent'
      }
    }
  },
  watch: {
    online (curVal, oldVal) {
      if (curVal !== oldVal) {
        this.online = curVal
      }
    }
  }
}
</script>

<style scoped>

</style>
