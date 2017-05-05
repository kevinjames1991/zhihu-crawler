package com.crawl.xiyanghui.parser;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.crawl.core.parser.DetailPageParser;
import com.crawl.xiyanghui.XiYangHuiHttpClient;
import com.crawl.zhihu.entity.Page;
import com.crawl.zhihu.entity.User;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

/**
 * https://www.zhihu.com/people/wo-yan-chen-mo/following
 * 新版following页面解析出用户详细信息
 */
public class XiyanghuiNewUserDetailPageParser implements DetailPageParser {
    private volatile static XiyanghuiNewUserDetailPageParser instance;
    public static XiyanghuiNewUserDetailPageParser getInstance(){
        if (instance == null){
            synchronized (XiYangHuiHttpClient.class){
                if (instance == null){
                    instance = new XiyanghuiNewUserDetailPageParser();
                }
            }
        }
        return instance;
    }
    private XiyanghuiNewUserDetailPageParser(){

    }
    @Override
    public User parse(Page page) {
        Document doc = Jsoup.parse(page.getHtml());
        Elements productElements = doc.getElementsByClass("product-item");
        for (Element element : productElements) {
        	Elements hrefElements = element.getElementsByAttribute("href");
        	int i = 1;
        	for (Element element2 : hrefElements) {
				if (i%2 == 0) {
					String productUrl = getProductId(element2.toString());
					System.out.println(productUrl);
				}else {
					i++;
					continue;
				}
			}
		}
        
//        System.out.println("parse: "+doc.select("product-item").first().attr("div"));
        User user = new User();
//        String userToken = getUserToken(page.getUrl());
//        user.setUserToken(userToken);
//        user.setUrl("https://www.zhihu.com/people/" + userToken);//用户主页
//        getUserByJson(user, userToken, doc.select("[data-state]").first().attr("data-state"));
        return user;
    }
    private void getUserByJson(User user, String userToken, String dataStateJson){

        String type = "['" + userToken + "']";//转义
        String commonJsonPath = "$.entities.users." + type;
        try {
            JsonPath.parse(dataStateJson).read(commonJsonPath);
        } catch (PathNotFoundException e){
            commonJsonPath = "$.entities.users.null";
        }
        setUserInfoByJsonPth(user, "username", dataStateJson, commonJsonPath + ".name");//username
        setUserInfoByJsonPth(user, "hashId", dataStateJson, commonJsonPath + ".id");//hashId
        setUserInfoByJsonPth(user, "followees", dataStateJson, commonJsonPath + ".followingCount");//关注人数
        setUserInfoByJsonPth(user, "location", dataStateJson, commonJsonPath + ".locations[0].name");//位置
        setUserInfoByJsonPth(user, "business", dataStateJson, commonJsonPath + ".business.name");//行业
        setUserInfoByJsonPth(user, "employment", dataStateJson, commonJsonPath + ".employments[0].company.name");//公司
        setUserInfoByJsonPth(user, "position", dataStateJson, commonJsonPath + ".employments[0].job.name");//职位
        setUserInfoByJsonPth(user, "education", dataStateJson, commonJsonPath + ".educations[0].school.name");//学校
        setUserInfoByJsonPth(user, "answers", dataStateJson, commonJsonPath + ".answerCount");//回答数
        setUserInfoByJsonPth(user, "asks", dataStateJson, commonJsonPath + ".questionCount");//提问数
        setUserInfoByJsonPth(user, "posts", dataStateJson, commonJsonPath + ".articlesCount");//文章数
        setUserInfoByJsonPth(user, "followers", dataStateJson, commonJsonPath + ".followerCount");//粉丝数
        setUserInfoByJsonPth(user, "agrees", dataStateJson, commonJsonPath + ".voteupCount");//赞同数
        setUserInfoByJsonPth(user, "thanks", dataStateJson, commonJsonPath + ".thankedCount");//感谢数
        try {
            Integer gender = JsonPath.parse(dataStateJson).read(commonJsonPath + ".gender");
            if (gender != null && gender == 1){
                user.setSex("male");
            }
            else if(gender != null && gender == 0){
                user.setSex("female");
            }
        } catch (PathNotFoundException e){
            //没有该属性
        }

    }

    /**
     * jsonPath获取值，并通过反射直接注入到user中
     * @param user
     * @param fieldName
     * @param json
     * @param jsonPath
     */
    private void setUserInfoByJsonPth(User user, String fieldName, String json, String jsonPath){
        try {
            Object o = JsonPath.parse(json).read(jsonPath);
            Field field = user.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(user, o);
        } catch (PathNotFoundException e1) {
            //no results
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 
     * @param url
     * @return
     */
    private String getProductId(String url){
        Pattern pattern = Pattern.compile("http://www.xiyanghui.com/product/[a-zA-Z0-9]+");
        Matcher matcher = pattern.matcher(url);
        String id = null;
        if(matcher.find()){
        	id = matcher.group(0);
            return id;
        }
        throw new RuntimeException("not parse userId");
    }
}