import Vue from 'vue'
import Router from 'vue-router'
import GroupChat from '@/components/GroupChat'
import SingleChat from '@/components/SingleChat'
import Online from '@/components/Online'
import Godpage from '@/components/Godpage'
// import HelloWorld1 from '@/components/HelloWorld1'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'GroupChat',
      component: GroupChat,
      childern: [
        {
          path: '/SingleChat',
          name: 'SingleChat',
          component: SingleChat
        },
        {
          path: '/Online',
          name: 'Onilne',
          component: Online
        },
        {
          path: '/Godpage',
          name: 'Godpage',
          component: Godpage
        }
      ]
    }
  ]
})
