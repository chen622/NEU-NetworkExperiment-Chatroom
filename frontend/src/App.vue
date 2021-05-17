<template>
  <div id="app">
    <!--TopBar-->
    <md-toolbar class="md-primary md-elevation-2">
      <div class="md-toolbar-row">
        <div class="md-toolbar-section-start">
          <span class="md-title"><b>轻聊</b> - 简洁而优雅</span>
        </div>
        <div class="md-toolbar-section-end">
          <md-menu md-size="medium" md-align-trigger>
            <md-button class="md-icon-button" md-menu-trigger @click="hello">
              <md-icon>person</md-icon>
            </md-button>
            <md-menu-content class="md-elevation-5">
              <md-menu-item @click="god">
                <span>管理员</span>
                <md-icon>person</md-icon>
              </md-menu-item>
            </md-menu-content>
          </md-menu>
        </div>
      </div>
    </md-toolbar>
    <!--TopBar end-->
    <!--empty-->
    <md-empty-state class="empty-state md-primary" md-rounded md-icon="hourglass_empty" md-label="请畅所欲言"
                    md-description="尚未打开任何聊天窗口." v-if="(!isGroup)&&(!isGod)"></md-empty-state>
    <!--empty end-->
    <!--CardContent-->
    <md-dialog-prompt
      :md-active.sync="active"
      v-model="value"
      md-title="请输入您的昵称?"
      md-input-maxlength="30"
      md-input-placeholder="江洋大盗..."
      md-cancel-text="取消"
      md-confirm-text="确认"/>
    <godpage v-if="isGod"></godpage>
    <singlechat v-if="isHelp" :chatTo="this.$store.state.god"></singlechat>
    <groupchat v-if="isGroup" :nickname="value"></groupchat>
    <!--CardContent end-->
    <!--Welcome-->
    <md-snackbar :md-position="position" :md-active.sync="showSnackbar"
                 md-persistent>
      <span><b>欢迎!</b><br/>
        这是一个基于WebSocket的网络聊天室!</span>
      <md-button class="md-primary" @click="showSnackbar = false">了解</md-button>
    </md-snackbar>
    <!--Welcome end-->
    <!--AddButton-->
    <md-speed-dial class="md-bottom-right" md-direction="top">
      <md-speed-dial-target class="md-primary" @click="showGroup">
        <md-icon class="md-morph-initial">add</md-icon>
        <md-icon class="md-morph-final">supervisor_account</md-icon>
      </md-speed-dial-target>
      <md-speed-dial-content>
        <md-button class="md-icon-button" @click="applyGod">
          <md-icon>help</md-icon>
        </md-button>
        <md-button class="md-icon-button">
          <md-icon>account_circle</md-icon>
        </md-button>
      </md-speed-dial-content>
    </md-speed-dial>
    <!--AddButton end-->
    <!--AddAlert-->
    <md-dialog-alert
      :md-active.sync="groupAlert"
      md-content="仅能拥有一个社群聊天室."
      md-confirm-text="了解!"/>
    <!--AddAlert end-->
    <md-dialog-prompt
      :md-active.sync="godAlert"
      v-model="pass"
      md-title="请输入管理员密码"
      md-input-maxlength="30"
      md-input-placeholder="我是上帝"
      md-cancel-text="取消"
      md-confirm-text="确认"/>
  </div>
</template>

<script>
import GroupChat from '@/components/GroupChat'
import SingleChat from '@/components/SingleChat'
import Godpage from '@/components/Godpage'

export default {
  name: 'App',
  data() {
    return {
      isGroup: false,
      isHelp: false,
      isGod: false,
      godName: this.$store.state.god,
      groupAlert: false,
      godAlert: false,
      showSnackbar: true,
      position: 'center',
      active: false,
      value: '',
      pass: ''
    }
  },
  methods: {
    hello: function () {
    },
    showGroup: function () {
      var _this = this
      if (_this.isGroup === true) {
        _this.groupAlert = true
      } else {
        _this.connect()
        _this.active = true
        _this.isGroup = true
      }
    },
    connect: function () {
      this.$store.commit('connect')
    },
    god: function () {
      this.godAlert = true
    },
    applyGod: function () {
      this.$store.commit('applyGod')
      // setTimeout(function () {
      //   if (this.$store.state.god === '') {
      //     alert('无在线客服')
      //   } else {
      //     this.isHelp = true
      //   }
      // }
      //   , 1000)
    }
  },
  components: {
    groupchat: GroupChat,
    singlechat: SingleChat,
    godpage: Godpage
  },
  watch: {
    active(curVal, oldVal) {
      var _this = this
      if (curVal === false && oldVal === true) {
        this.$store.commit('setName', _this.value)
      }
    },
    godAlert(curVal, oldVal) {
      if (curVal === false && oldVal === true) {
        if (this.pass === 'god') {
          this.connect()
          var _this = this
          setTimeout(function () {
            _this.isGod = true
            _this.$store.commit('godLogin')
            alert('你好上帝')
          }, 2000)
        } else {
          alert('请不要冒充上帝')
          this.pass = ''
        }
      }
    },
    godName(curVal) {
      if (curVal === 'wu') {
        alert('无在线客服')
      } else {
        this.isHelp = true
      }
    }
  }
}
</script>

<style>
@import "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic|Material+Icons";

#app {
  font-family: Roboto, "Microsoft YaHei UI", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

.md-list-item-content {
  padding-left: 12px;
}

.md-list-item-content > .md-icon {
  margin: 0px;
  margin-right: 10px;

}

.md-button {
  z-index: 100;
}

.empty-state {
  margin-top: 100px;
}
</style>
