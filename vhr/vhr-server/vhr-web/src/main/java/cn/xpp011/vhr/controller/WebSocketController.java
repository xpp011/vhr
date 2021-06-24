package cn.xpp011.vhr.controller;

import cn.xpp011.vhr.model.ChatMsg;
import cn.xpp011.vhr.model.Hr;
import cn.xpp011.vhr.utils.HrUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Date;

@Controller
public class WebSocketController {

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    //收拾消息的URL
    @MessageMapping("/ws/chat")
    //principal当前用户
    public void handleMsg(Authentication authentication, ChatMsg chatMsg){
        if (chatMsg.getTo()==null){
            return;
        }
        Hr hr=(Hr)authentication.getPrincipal();
        chatMsg.setFrom(hr.getUsername());
        chatMsg.setFromNickname(hr.getName());
        chatMsg.setDate(new Date());
        //向/queue/chat地址发送消息
        System.out.println(chatMsg);
        simpMessagingTemplate.convertAndSendToUser(chatMsg.getTo(),"/queue/chat",chatMsg);
    }
}
