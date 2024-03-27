package com.demo.xihu.serverwebsocket;

//import com.demo.xihu.listener.BigModelNew;
import com.demo.xihu.listener.BigModelNew;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
@ServerEndpoint("/dev-api/websocket/{userid}") //暴露的ws应用的路径
public class WebSocket2 {

    private static final ConcurrentMap<String, Session> sessionMap = new ConcurrentHashMap<>();

    private BigModelNew bigModelNew;
    private String  userid;
    /**
     * 客户端与服务端连接成功
     * @param session
     * @param userid
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userid") String userid){
        /*
            do something for onOpen
            与当前客户端连接成功时
         */
        this.userid=userid;
        sessionMap.put(userid, session); // 保存用户ID和对应的Session对象
        bigModelNew =new BigModelNew(userid,false,session);
        System.out.println(userid+"连接成功！");
    }

    /**
     * 客户端与服务端连接关闭
     * @param session
     * @param userid
     */
    @OnClose
    public void onClose(Session session,@PathParam("userid") String userid){
        /*
            do something for onClose
            与当前客户端连接关闭时
         */
        sessionMap.remove(userid); // 当连接关闭时移除对应的映射关系

        System.out.println(userid+"关闭了");

    }

    /**
     * 客户端与服务端连接异常
     * @param error
     * @param session
     * @param userid
     */
    @OnError
    public void onError(Throwable error,Session session,@PathParam("userid") String userid) {
    }

    /**
     * 客户端向服务端发送消息
     * @param message
     * @param userid
     * @throws IOException
     */
    @OnMessage
    public void onMessage(Session session,String message,@PathParam("userid") String userid) throws Exception {
        /*
            do something for onMessage
            收到来自当前客户端的消息时
         */
        System.out.println("客户端:"+message);
        bigModelNew.getReply(message,userid);
//
//        session.getBasicRemote().sendText("服务端收到"+userid+"回答："+BigModelNew.totalAnswer);
    }


}



/*        Session senderSession = sessionMap.get(userid);
        if (senderSession != null) {
            // 假设要向用户发送消息
            String targetUserId = "1"; // 假设要发送给指定用户
            Session targetSession = sessionMap.get(targetUserId);
            if (targetSession != null) {
                targetSession.getBasicRemote().sendText("来自" + userid + "的消息：" + message);
            } else {
                senderSession.getBasicRemote().sendText("目标用户不存在或不在线");
            }
        }*/
