# 环境

JDK17，springBoot 3.4.2，spring AI 1.0.0，spring AI alibaba 1.0.0.2，mysql 8.0，mybatis plus，redis_stack

# 执行mysql
安装mysql和redis-stack，可以使用 docker deskTop安装
```java
docker run -d --name redis-stack   -v redis-data:/data -p 6379:6379 -p 8011:8011 -e REDIS_ARGS="--requirepass 123456" redis/redis-stack:latest
```
mysql数据库执行ai-example\mcp-stdio-server\src\main\resources\dbscript下的脚本

# 源码地址

[GitHub使用game分支](https://github.com/wangmeng1990/ai-example/tree/game)

# 项目配置修改
1.到百炼平台申请api-key 
[百炼平台](https://bailian.console.aliyun.com/)，
    把获取的api-key配置到环境变量,也可以写死api-key: ${DASHSCOPE_APIKEY}
    
2.修改mcp-stdio-server的mysql数据库配置

3.修改wm-spring-ai的redis配置

4.修改\ai-example\wm-spring-ai\src\main\resources\mcp-servers-config.json
 mcp-stdio-server-0.0.1-SNAPSHOT.jar包的路径修改为本地打包的jar包路径

5.打包 mvn clean install mcp-stdio-server

6.打包 mvn clean install wm-spring-ai

7.启动wm-spring-ai

8.执行com.wm.ai.controller.VectorStoreController.userPortraits接口，把用户画像embedding到redis-stack

9.执行com.wm.ai.controller.ChatController 开启体验

# 项目目录结构

### mcp-stdio-server
- 提供产品信息查询MCP STDIO 服务

### wm-spring-ai
- aent 包                             # 封装了rag+会话记忆+tools call 的chatClient
- common 包                           # 接口通用响应实体
  - conf 包                           # 配置会话记忆存储bean
- controller 包
  - ChatController                    # 提供sse和普通对话接口
  - VectorStoreController             # 提供用户画像信息emdedding到向量数据库接口
- exception 包                        # 通用异常处理
- resource/static/index.html          # 前端对话页面 -cursor生成

# 对话流程
1.ETL 

embedding用户画像并添加元数据，生成概要和关键字,用于检索阶段的条件过滤

2.rag

先通过QuestionAnswerAdvisor基于用户提问检索向量数据库，得到最相近的top n条检索片段
再使用RetrievalRerankAdvisor进行精排得到更匹配的结果，
然后把结果放到对话上下文交给LLM处理，LLM会结合上下文和工具调用的结果给出最终的回答输出给用户