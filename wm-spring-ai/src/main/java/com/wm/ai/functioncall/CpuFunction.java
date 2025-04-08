package com.wm.ai.functioncall;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Description("读取CPU的数量")
@AllArgsConstructor
public class CpuFunction implements Function<CpuFunction.ChatRequestParam, Integer> {

    @Override
    public Integer apply(ChatRequestParam input) {
        return  Runtime.getRuntime().availableProcessors();
    }

    public record ChatRequestParam(@JsonProperty(required = true, value = "input") @JsonPropertyDescription(value = "用户提问") String input) {
    }
}
