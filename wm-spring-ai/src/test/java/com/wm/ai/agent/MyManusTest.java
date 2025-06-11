package com.wm.ai.agent;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class MyManusTest {

    @Resource
    private MyManus myManus;

    @Test
    public void run() {
        String result = myManus.run("E:\\t.txt里面有一个任务，完成它");
        System.out.println(result);
    }
}