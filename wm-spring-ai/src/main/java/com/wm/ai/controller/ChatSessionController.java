package com.wm.ai.controller;
import com.wm.ai.model.ChatSession;
import com.wm.ai.model.vo.ChatSessionVO;
import com.wm.ai.service.ChatSessionService;
import org.springframework.web.bind.annotation.*;

import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import java.util.List;

/**
* (ai_session)表控制层
*
* @author xxxxx
*/
@RestController
@RequestMapping("/ai_session")
public class ChatSessionController {
/**
* 服务对象
*/
    @Autowired
    private ChatSessionService chatSessionService;

    @PostMapping("/add")
    public String create(@RequestBody @Valid ChatSession chatSession){
        chatSessionService.create(chatSession);
        return "success";
    }

    @PostMapping("/del")
    public String delete(@RequestParam("sessionId") String sessionId){
        chatSessionService.delete(sessionId);
        return "success";
    }

    @GetMapping("/list")
    public List<ChatSession> list(@RequestParam("userId") String userId){
        return chatSessionService.listUserSession(userId);
    }


    @GetMapping("/detail/{sessionId}")
    public ChatSessionVO detail(@PathVariable("sessionId") String sessionId){
        return chatSessionService.detail(sessionId);
    }


}
