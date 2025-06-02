package com.wm.mcp.sseserver.controller;

import com.wm.mcp.sseserver.service.WhoTool;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "mcp-sse")
@RestController
@RequestMapping("/calc")
public class WhoController {

    @Autowired
    private WhoTool whoTool;

    @PostMapping(value = "/c1")
    public String c1(@RequestBody UserRequest request ) {
        return whoTool.who(request.in);
    }

    public record UserRequest(String in) {
    }
}
