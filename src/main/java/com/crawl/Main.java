package com.crawl;

import org.apache.log4j.Logger;

import com.crawl.core.util.Config;
import com.crawl.core.util.SimpleLogger;
import com.crawl.proxy.ProxyHttpClient;
import com.crawl.xiyanghui.XiYangHuiHttpClient;
import com.crawl.zhihu.ZhiHuHttpClient;

/**
 * 爬虫入口
 */
public class Main {
    private static Logger logger = SimpleLogger.getLogger(Main.class);
    public static void main(String args []){
        String startURL = Config.startURL;
//        ProxyHttpClient.getInstance().startCrawl();
        System.out.println(startURL);
//        ZhiHuHttpClient.getInstance().startCrawl(startURL);
        XiYangHuiHttpClient.getInstance().startCrawl(startURL);
    }
}
