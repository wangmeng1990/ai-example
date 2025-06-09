package com.wm.ai;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;

import java.util.List;
import java.util.Map;

public class TestMCP {

    public static void main(String[] args) {
        McpSyncClient client = McpClient.sync(new HttpClientSseClientTransport("http://localhost:8003")).build();
        McpSchema.ListToolsResult listToolsResult = client.listTools();

        List<McpSchema.Content> listTools = client.callTool(new McpSchema.CallToolRequest("who", Map.of("a", "wm是谁"))).content();

        System.out.println(listTools);
        client.close();
    }
}
