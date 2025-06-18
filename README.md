# 环境

JDK17，springBoot 3.4.2，spring AI 1.0.0，spring AI alibaba 1.0.0.2，mysql 8.0，mybatis plus，redis_stack

# 执行mysql
安装mysql和redis-stack，可以使用 docker deskTop安装
```java
docker run -d --name redis-stack   -v redis-data:/data -p 6379:6379 -p 8011:8011 -e REDIS_ARGS="--requirepass 123456" redis/redis-stack:latest
```
mysql数据库执行ai-example\wm-spring-ai\src\main\resources\dbscript下的脚本

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