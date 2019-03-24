import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

const store = new Vuex.Store({
  // 定义状态
  state: {
    god: '',
    oldgod: '',
    messages: [
      {text: 'hello', id: 0, type: 'all', sender: null}
    ],
    nextTodoId: 1,
    Chat: {
      socket: null
    },
    user: [
    ],
    nextUserId: 0,
    name: '江洋大盗',
    json: {
      function: 0,
      type: 0,
      sender: '',
      addressee: '',
      message: '',
      name: [
        ''
      ]
    },
    singleChat: [
    ],
    nextChatId: 0
  },
  mutations: {
    connect (state) {
      if (window.location.protocol === 'http:') {
        store.commit('initWebSocket', 'ws://' + 'localhost:8080' + '/websocket/chat')
      } else {
        store.commit('initWebSocket', 'wss://' + 'localhost:8080' + '/websocket/chat')
      }
    },
    initWebSocket (state, wsuri) {
      state.Chat.socket = new WebSocket(wsuri)
      state.Chat.socket.onopen = function () {
        store.commit('log', 'Info: WebSocket connection opened.')
      }
      state.Chat.socket.onclose = function () {
        store.commit('log', 'Info: WebSocket closed.')
      }
      state.Chat.socket.onmessage = function (message) {
        var json = JSON.parse(message.data)
        console.log(json)
        switch (json.function) {
          case 2: {
            if (json.type === 0) {
              store.commit('log', json.message)
              break
            } else if (json.type === 1) {
              if (json.addressee.charAt(0) === '客') {
                state.name = json.addressee
              }
              var a = {msg: json.message, to: json.addressee, sender: json.sender}
              console.log('2.1' + a)
              store.commit('singleLog', a)
              break
            }
            console.log('Unknown json')
            break
          } case 0: {
            if (json.type === 1) {
              for (var i = 0; i < state.user.length; i++) {
                if (state.user[i].name === json.sender) {
                  state.user.splice(i, 1)
                  store.commit('log', json.message)
                }
              }
              for (var k = 0; k < state.singleChat.length; k++) {
                if (state.singleChat[k].name === json.sender) {
                  state.singleChat.splice(k, 1)
                }
              }
              break
            } else if (json.type === 0) {
              store.commit('log', json.message)
              state.user.push({
                name: json.sender,
                id: state.nextUserId++
              })
              break
            }
            console.log('Unknown json')
            break
          } case 1: {
            for (var j = 0; j < json.name.length; j++) {
              state.user.push({name: json.name[j], id: state.nextUserId})
              state.nextUserId++
            }
            state.name = json.addressee
            break
          } case 3: {
            if (json.type === 2) {
              state.god = json.sender
              break
            } else if (json.type === 3) {
              if (state.oldgod.length > 2) {
                var msg = '{\n' +
                  '\t"function": 2,\n' +
                  '\t"type": 1,\n' +
                  '\t"sender": "' + store.state.name + '",\n' +
                  '\t"addressee": "' + json.sender + '",\n' +
                  '\t"message": "原' + state.oldgod + '的用户接入",\n' +
                  '\t"name": []\n' +
                  '}'
                store.commit('sendMsg', msg)
                store.commit('addSingleChat', json.sender)
                for (var z = 0; z < state.messages.length; z++) {
                  if (state.messages[z].type === state.oldgod) {
                    state.messages[z].type = json.sender
                    store.commit('sendMsg', '{\n' +
                      '\t"function": 3,\n' +
                      '\t"type": 4,\n' +
                      '\t"sender": "' + state.name + '",\n' +
                      '\t"addressee": "' + json.sender + '",\n' +
                      '\t"message": "' + state.messages[z].text + '",\n' +
                      '\t"name": []\n' +
                      '}')
                  } else if (state.messages[z].sender === state.oldgod) {
                    state.messages[z].sender = json.sender
                    store.commit('sendMsg', '{\n' +
                      '\t"function": 3,\n' +
                      '\t"type": 4,\n' +
                      '\t"sender": "' + state.name + '",\n' +
                      '\t"addressee": "' + json.sender + '",\n' +
                      '\t"message": "' + state.messages[z].text + '",\n' +
                      '\t"name": []\n' +
                      '}')
                  }
                }
              }
              state.user.push({
                name: json.sender,
                id: state.nextUserId++
              })
              break
            } else if (json.type === 0) {
              state.name = json.sender
              break
            } else if (json.type === 1) {
              for (var l = 0; l < state.user.length; l++) {
                if (state.user[l].name === json.sender) {
                  state.oldgod = json.sender
                  state.user.splice(l, 1)
                  store.commit('log', json.message)
                }
              }
              store.commit('applyGod')
            } else if (json.type === 4) {
              var b = {msg: json.message, to: json.addressee, sender: json.sender}
              store.commit('singleLog', b)
            }
            break
          }
        }
      }
    },
    log (state, message) {
      state.messages.push({
        text: message,
        id: state.nextTodoId++,
        type: 'all'
      })
    },
    singleLog (state, message) {
      var a = message
      state.messages.push({
        text: a.msg,
        id: state.nextTodoId++,
        type: a.to,
        sender: a.sender
      })
    },
    sendMsg (state, message) {
      console.log(message)
      state.Chat.socket.send(message)
    },
    setName (state, name) {
      state.name = name
      if (name == null || name === undefined || name === '') {
        store.commit('sendMsg', '{\n' +
          '\t"function": 0,\n' +
          '\t"type": 0,\n' +
          '\t"sender": "",\n' +
          '\t"addressee": "",\n' +
          '\t"message": "",\n' +
          '\t"name": []\n' +
          '}')
      } else {
        store.commit('sendMsg', '{\n' +
        '\t"function": 0,\n' +
        '\t"type": 0,\n' +
        '\t"sender": "' + name + '",\n' +
        '\t"addressee": "",\n' +
        '\t"message": "",\n' +
        '\t"name": []\n' +
        '}')
      }
    },
    godLogin (state) {
      store.commit('sendMsg', '{\n' +
        '\t"function": 3,\n' +
        '\t"type": 0,\n' +
        '\t"sender": "",\n' +
        '\t"addressee": "",\n' +
        '\t"message": "",\n' +
        '\t"name": []\n' +
        '}')
    },
    addSingleChat (state, name) {
      for (var k = 0; k < state.singleChat.length; k++) {
        if (state.singleChat[k].name === state.oldgod) {
          state.singleChat[k].name = name
          return
        }
      }
      state.singleChat.push({name: name, id: state.nextChatId++})
    },
    applyGod (state) {
      store.commit('sendMsg', '{\n' +
        '\t"function": 3,\n' +
        '\t"type": 2,\n' +
        '\t"sender": "' + state.name + '",\n' +
        '\t"addressee": "",\n' +
        '\t"message": "",\n' +
        '\t"name": []\n' +
        '}')
    }
  }
})

export default store
