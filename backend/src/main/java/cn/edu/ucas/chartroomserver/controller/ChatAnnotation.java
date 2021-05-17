/*
 * MIT License
 *
 * Copyright (c) 2021 Chenming C
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cn.edu.ucas.chartroomserver.controller;


import cn.edu.ucas.chartroomserver.model.Json;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Controller;


@ServerEndpoint(value = "/websocket/chat")
@Controller
public class ChatAnnotation {

  private static final String GUEST_PREFIX = "Guest";
  private static final String God_PREFIX = "客服";
  private static final AtomicInteger connectionIds = new AtomicInteger(0);
  private static final Set<ChatAnnotation> connections =
      new CopyOnWriteArraySet<ChatAnnotation>();
  private static final Set<ChatAnnotation> godList = new CopyOnWriteArraySet<>();
  private static final Map<String, String> god = new ConcurrentHashMap<>();
  private String nickname;
  private final String godname;
  private int type = 0;//0 客户,1 客服
  private int serverCount = 0;
  private Session session;
  private final Gson gson = new Gson();
  private Json json;

  public ChatAnnotation() {
    nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    godname = God_PREFIX + godList.size();
  }


  @OnOpen
  public void start(Session session) {
    this.session = session;
//        json = new Json(0);
//        json.setType(0);
//        json.setMessage(String.format("* %s %s", nickname, "has joined."));
//        broadcast(gson.toJson(json));
  }


  @OnClose
  public void end() {
    if (type == 0) {
      connections.remove(this);
      json = new Json(0);
      json.setType(1);
      json.setSender(nickname);
      json.setMessage(nickname + "下线了");
      for (ChatAnnotation client : godList) {
        try {
          client.session.getBasicRemote().sendText(new Gson().toJson(json));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      broadcast(gson.toJson(json), nickname);
    } else {
      godList.remove(this);
      json = new Json(3);
      json.setType(1);
      json.setSender(godname);
      json.setMessage(godname + "下线了");
      String str = gson.toJson(json);
      Iterator<Map.Entry<String, String>> iterator = god.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<String, String> entry = iterator.next();
        if (entry.getValue().equals(godname)) {
          for (ChatAnnotation client : connections) {
            try {
              if (client.nickname.equals(entry.getKey())) {
                System.out.println("send");
                client.session.getBasicRemote().sendText(str);
                iterator.remove();
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }


  @OnMessage
  public void incoming(String message) {
    // Never trust the client
    System.out.println(message);
    Json receiveJson = gson.fromJson(message, Json.class);
    switch (receiveJson.getFunction()) {
      case 2: {//消息
        if (receiveJson.getType() == 0) {
          json = new Json(2);
          json.setType(0);
          json.setSender(nickname);
          json.setMessage(receiveJson.getMessage());
          broadcast(gson.toJson(json), nickname);
          break;
        } else if (receiveJson.getType() == 1) {
          singleMessage(receiveJson.getAddressee(), receiveJson.getMessage(), receiveJson.getSender());
          break;
        }
        System.out.println("2- unknown function");
        break;
      }
      case 0: {//用户上下线
        if (receiveJson.getType() == 0) {//上线
          if (!receiveJson.getSender().equals("")) {
            this.nickname = receiveJson.getSender();
          }
          System.out.println(nickname);
          newUser();
          returnAllPeople();
          break;
        }
        System.out.println("0- unknown function");
      }
      case 1: {//所有用户
        break;
      }
      case 3: {
        if (receiveJson.getType() == 0) {//上线
          type = 1;
          godList.add(this);
          json = new Json(3);
          json.setType(0);
          json.setSender(godname);
          System.out.println(111);
          break;
        } else if (receiveJson.getType() == 2) {
          applyGod(receiveJson.getSender());
        } else if (receiveJson.getType() == 4) {
          for (ChatAnnotation client : godList) {
            try {
              if (client.godname.equals(receiveJson.getAddressee())) {
                System.out.println("msg" + new Gson().toJson(receiveJson));
                client.session.getBasicRemote().sendText(new Gson().toJson(receiveJson));
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        }
        break;
      }

    }
//        String filteredMessage = String.format("%s: %s",
//                nickname, HTMLFilter.filter(message.toString()));
//        broadcast(filteredMessage);
  }

  @OnError
  public void onError(Throwable t) throws Throwable {

  }

  private void returnAllPeople() {
    json = new Json(1);
    json.setAddressee(nickname);
    for (ChatAnnotation client : connections) {
      if (client == this) {
        json.addName(client.nickname + " (自己)");
      } else {
        json.addName(client.nickname);
      }
    }
    sendMessage(gson.toJson(json));
  }

  private void newUser() {
    connections.add(this);
    json = new Json(0);
    json.setType(0);
    json.setSender(nickname);
    json.setMessage(nickname + "上线了!");
    broadcast(gson.toJson(json), nickname);
  }

  private void sendMessage(String message) {//1.发送所有用户;2.发送用户上下线消息
    try {
      this.session.getBasicRemote().sendText(message);
    } catch (IOException e) {
//            connections.remove(this);
//            try {
//                client.session.close();
//            } catch (IOException e1) {
//                // Ignore
//            }
//            String message = String.format("* %s %s",
//                    client.nickname, "has been disconnected.");
//            broadcast(message);
    }
  }

  public void applyGod(String applier) {
    ChatAnnotation chat = null;
    int size = 100;
    for (ChatAnnotation client : godList) {
      if (client.serverCount < size) {
        size = client.serverCount;
        chat = client;
      }
    }
    json = new Json(3);
    json.setType(3);
    json.setSender(this.nickname);
    Json newjson = new Json(3);
    newjson.setType(3);
    newjson.setAddressee(this.nickname);

    try {
      if (godList.size() == 0) {
        newjson.setSender("wu");
        this.session.getBasicRemote().sendText(new Gson().toJson(newjson));
      } else {
        newjson.setSender(chat.godname);
        json.setAddressee(chat.nickname);
        this.session.getBasicRemote().sendText(new Gson().toJson(newjson));
        chat.session.getBasicRemote().sendText(new Gson().toJson(json));
        chat.serverCount++;
        god.put(applier, chat.godname);
      }


    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void singleMessage(String addressee, String msg, String sender) {
    Json newjson = new Json(2);
    newjson.setType(1);
    newjson.setAddressee(addressee);
    newjson.setMessage(msg);
    newjson.setSender(sender);
    for (ChatAnnotation client : connections) {
      try {
        if (client.nickname.equals(addressee)) {
          System.out.println("send");
          client.session.getBasicRemote().sendText(new Gson().toJson(newjson));
        }
      } catch (IOException e) {
        connections.remove(client);
        try {
          client.session.close();
        } catch (IOException e1) {
          // Ignore
        }
        Json new1json = new Json(0);
        new1json.setType(1);
        new1json.setSender(client.nickname);
        new1json.setMessage(client.nickname + "下线了");
        broadcast(new Gson().toJson(new1json), client.nickname);
      }
    }
    for (ChatAnnotation client : godList) {
      try {
        if (client.godname.equals(addressee)) {
          client.session.getBasicRemote().sendText(new Gson().toJson(newjson));
        }
      } catch (IOException e) {
      }
    }
  }

  private static void broadcast(String msg, String sender) {
    for (ChatAnnotation client : connections) {
      try {
        synchronized (client) {
          if (client.nickname.equals(sender)) {
            continue;
          }
          client.session.getBasicRemote().sendText(msg);
        }
      } catch (IOException e) {

        connections.remove(client);
        try {
          client.session.close();
        } catch (IOException e1) {
          // Ignore
        }
        Json newjson = new Json(0);
        newjson.setType(1);
        newjson.setSender(client.nickname);
        newjson.setMessage(client.nickname + "下线了");
        broadcast(new Gson().toJson(newjson), client.nickname);
      }
    }
  }
}
