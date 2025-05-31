package com.wm.ai.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * 网页抓取
 * prompt :2022款 奥迪A4L 40 TFSI 时尚动感型的价格，可以从网站：https://www.che300.com/buycar/x393153639  获取
 */
public class WebScrapingTool {

    @Tool(description = "抓取网页信息")
    public String scrapeWebPage(@ToolParam(description = "要抓取的网页URL") String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.html();
        } catch (IOException e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }
}
