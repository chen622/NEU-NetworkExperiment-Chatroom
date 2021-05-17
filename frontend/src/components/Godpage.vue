<template>
  <div class="md-layout">
    <!--Main-->
    <div class="md-layout-item">
      <singlechat v-for="chat in singleChats" :key="chat.id" :chatTo="chat.name">
      </singlechat>
    </div>
    <!--Main end-->
    <!--online-->
    <md-card md-with-hover class="md-elevation-16 side-card md-layout-item md-size-20" style="overflow: hidden">
      <div class="viewport">
        <md-toolbar class="md-primary" md-elevation="1" style="min-height: 64px">
          <span class="md-title">求助客户</span>
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
  name: 'godpage',
  data () {
    return {
      users: this.$store.state.user,
      singleChats: this.$store.state.singleChat
    }
  },
  methods: {
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

<style scoped>
  .main-card{
    margin: 20px;
  }

  .side-card{
    margin: 20px;
  }

  .viewport{
     min-height: 100%;
     width: 100%;
     display: inline-block;
     vertical-align: top;
     overflow: hidden;
     border: 1px solid rgba(0, 0, 0, .12)
   }

  .online-list{
    min-height: 400px;
    max-height: 500px;
    overflow: auto;
  }
</style>
