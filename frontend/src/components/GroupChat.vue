<template>
  <div class="md-layout">
    <!--Main-->
    <div class="md-layout-item">
      <md-card md-with-hover class="md-elevation-16 main-card">
        <md-card-header style="margin-bottom: 5px;background: #448aff" class="md-elevation-1">
          <span class="md-title" style="color: #ffffff;">社群聊天室</span>
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
      <singlechat v-for="chat in singleChats" :key="chat.id" :chatTo.sync="chat.name">
      </singlechat>
    </div>
    <!--Main end-->
    <!--online-->
    <md-card md-with-hover class="md-elevation-16 side-card md-layout-item md-size-20">
      <div class="viewport">
        <md-toolbar class="md-primary" md-elevation="1" style="min-height: 64px">
          <span class="md-title">在线用户</span>
        </md-toolbar>
        <md-list>
          <md-list-item>
            <md-icon class="md-primary" style="margin-right: 22px;margin-left: 9px">perm_identity</md-icon>
            <span class="md-list-item-text">当前用户: {{name}}</span>
          </md-list-item>
          <md-content class="online-list md-scrollbar">
          <md-divider class="md-inset"></md-divider>
          <!--<md-list-item v-for="user in users" :key="user.id">-->
            <!--<md-avatar>-->
              <!--<img src="https://placeimg.com/40/40/people/5" alt="People">-->
            <!--</md-avatar>-->

            <!--<span class="md-list-item-text">{{user.name}}</span>-->

            <!--<md-button class="md-icon-button md-list-action" @click="newChat(user.name)">-->
              <!--<md-icon class="md-primary">chat_bubble</md-icon>-->
            <!--</md-button>-->
          <!--</md-list-item>-->
            <online v-for="user in users" :key="user.id" :online="user.name"></online>
          </md-content>
        </md-list>
      </div>
    </md-card>
    <!--online end-->
  </div>
</template>

<script>
import SingleChat from '@/components/SingleChat'
import Online from '@/components/Online'

export default {
  name: 'GroupChat',
  data () {
    return {
      textarea: '',
      users: this.$store.state.user,
      singleChats: this.$store.state.singleChat
    }
  },
  methods: {
    getMsg (event) {
      var _this = this
      if (event.keyCode === 13) {
        // var message = document.getElementById('chat').value
        if (_this.textarea !== '') {
          var msg = _this.textarea.replace(/[\r\n]/g, '')
          this.$store.commit('log', this.$store.state.name + ': ' + msg)
          msg = '{\n' +
            '\t"function": 2,\n' +
            '\t"type": 0,\n' +
            '\t"sender": "' + this.$store.state.name + '",\n' +
            '\t"addressee": "",\n' +
            '\t"message": "' + this.$store.state.name + ': ' + msg + '",\n' +
            '\t"name": []\n' +
            '}'
          this.$store.commit('sendMsg', msg)
          _this.textarea = ''
        }
      }
    }
  },
  computed: {
    name () {
      return this.$store.state.name
    },
    messages () {
      var msg = this.$store.state.messages
      var returnMsg = []
      for (var i = 0; i < this.$store.state.nextTodoId; i++) {
        if (msg[i].type === 'all') {
          returnMsg.push(msg[i])
        }
      }
      return returnMsg
    }
  },
  components: {
    singlechat: SingleChat,
    online: Online
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
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

.main-card{
  margin: 20px;
  padding-bottom: 20px;
}

.side-card{
  margin: 20px;
  overflow: inherit;
  max-height: 615px;
}

.input{
  margin: 10px 20px 10px 20px;
  width: auto;
}

.viewport{
  min-height: 100%;
  width: 100%;
  display: inline-block;
  vertical-align: top;
  overflow: auto;
  border: 1px solid rgba(0, 0, 0, .12)
}

.online-list{
  max-height: 500px;
  overflow: auto;
}
</style>
