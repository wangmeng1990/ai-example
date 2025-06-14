package com.wm.ai.tools;

import com.wm.ai.service.impl.DocumentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentTools {

    private final DocumentService documentService;

    //@ToolParam(description = "ToolContext",required = false) ToolContext context
    @Tool(description = "读取文件内容")
    public String readDocument(@ToolParam(description = "文件路径",required = true) String path, @ToolParam(description = "ToolContext",required = false) ToolContext context) {
        //工具上下文可在应用程序端的工具调用链中传递信息，既减少了token数有保证了安全性
        log.info("工具上下文context:"+context.getContext().toString());
        return documentService.document2(path).get(0).getText();
    }
}
