package com.wm.ai.tools;

import com.wm.ai.service.impl.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DocumentTools {

    private final DocumentService documentService;

    @Tool(description = "读取文件内容")
    public String readDocument(@ToolParam(description = "文件路径",required = true) String path, ToolContext context) {
        //工具上下文可在应用程序端的工具调用链中传递信息，既减少了token数有保证了安全性
        System.out.println("userId:"+context.getContext().get("userId"));
        return documentService.document2(path).get(0).getText();
    }
}
