<template>
  <md-card md-with-hover class="md-elevation-16 main-card">
    <md-card-header style="margin-bottom: 5px;background: #448aff" class="md-elevation-1">
      <span class="md-title" style="color: #ffffff;">与 <b>{{chatTo}}</b> 的聊天</span>
    </md-card-header>
    <div>
      <md-content id="console">
        <p style="word-wrap: break-word"
           v-for="message in messages"
           v-bind:key="message.id">{{message.text}}
        </p>
      </md-content>
      <md-field class="input">
        <label>消息</label>
        <md-textarea v-model="textarea" id="chat" @keyup.13="getMsg($event)"></md-textarea>
      </md-field>
    </div>
  </md-card>
</template>

<script>
export default {
  name: 'single-chat',
  data () {
    return {
      textarea: ''
    }
  },
  props: ['chatTo'],
  computed: {
    messages: function () {
      var returnMsg = []
      var msg = this.$store.state.messages
      for (var i = 0; i < this.$store.state.nextTodoId; i++) {
        if ((msg[i].type === this.chatTo && msg[i].sender === this.$store.state.name) || (msg[i].type === this.$store.state.name && msg[i].sender === this.chatTo)) {
          returnMsg.push(msg[i])
        }
      }
      return returnMsg
    }
  },
  methods: {
    getMsg (event) {
      var _this = this
      if (event.keyCode === 13) {
        // var message = document.getElementById('chat').value
        if (_this.textarea !== '') {
          var msg = _this.textarea.replace(/[\r\n]/g, '')
          var a = {msg: this.$store.state.name + ': ' + msg, to: _this.chatTo, sender: this.$store.state.name}
          this.$store.commit('singleLog', a)
          msg = '{\n' +
            '\t"function": 2,\n' +
            '\t"type": 1,\n' +
            '\t"sender": "' + this.$store.state.name + '",\n' +
            '\t"addressee": "' + _this.chatTo + '",\n' +
            '\t"message": "' + this.$store.state.name + ': ' + msg + '",\n' +
            '\t"name": []\n' +
            '}'
          this.$store.commit('sendMsg', msg)
          _this.textarea = ''
        }
      }
    }
  },
  watch: {
    chatTo (curVal, oldVal) {
      if (curVal !== oldVal) {
        this.chatTo = curVal
      }
    }
  }
}
</script>

<style scoped>
  #console {
    text-align: left;
    height: 400px;
    width: auto;
    margin: 15px 20px 0px 20px;
    overflow: auto;
  }
  #console p {
    font-size: 16px;
    padding: 0;
    margin: 4px 0px 4px 0px;
  }
  .input{
    margin: 10px 20px 10px 20px;
    width: auto;
  }
  .main-card{
    margin: 20px;
  }
</style>
