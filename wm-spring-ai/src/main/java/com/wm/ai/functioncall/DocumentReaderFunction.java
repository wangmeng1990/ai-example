package com.wm.ai.functioncall;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.wm.ai.service.impl.DocumentService;
import lombok.AllArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 * 读取文件内容函数
 * @author wm
 * @date 2023/10/12 15:03
 * @Description:
 */

@Component
@Description("读取文件内容")
@AllArgsConstructor
public class DocumentReaderFunction implements Function<DocumentReaderFunction.DocumentReaderRequestParam, String> {

    private final DocumentService documentService;
    @Override
    public String apply(DocumentReaderRequestParam documentReaderRequestParam) {
        List<Document> documents = documentService.document2(documentReaderRequestParam.filePath);
        return documents.get(0).getText();
    }

    public record DocumentReaderRequestParam(@JsonProperty(required = true, value = "filePath") @JsonPropertyDescription(value = "需要读取的文件路径") String filePath) {}
}
