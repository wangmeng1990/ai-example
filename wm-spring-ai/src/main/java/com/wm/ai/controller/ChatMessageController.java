package com.wm.ai.controller;
import com.wm.ai.model.ChatMessage;
import com.wm.ai.service.ChatMessageService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

/**
* (ai_message)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/ai_message")
public class ChatMessageController {
/**
* 服务对象
*/
    @Autowired
    private ChatMessageService chatMessageService;

    @PostMapping("/add")
    public String add(@RequestBody ChatMessage chatMessage) {
        chatMessageService.addMessage(chatMessage);
        return "success";
    }


}
