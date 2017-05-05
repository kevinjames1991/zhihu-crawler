package com.crawl;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Test {
	
	public static void main(String[] args) throws Exception {
		
		String html = "<html><head><title>开源中国社区</title></head>"
				+"<body><p>这里是jsoup 项目的相关文章</p></body></html>";
		Document doc = Jsoup.parse(html);
		
		System.out.println(doc.getAllElements().get(1));
		
		doc = Jsoup.connect("http://www.xiyanghui.com/women/2").get();
		
		System.out.println(doc.getAllElements().get(1));
		
		Element title = doc.select("div.title").first();
		
		System.out.println(title);
		
		
	}

}
