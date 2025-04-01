package com.liyang.app.utils;

import com.liyang.app.entity.Unknown;
import com.liyang.app.mapper.CitiesMapper;
import com.liyang.app.mapper.UnknownMapper;
import com.liyang.app.service.GetGeo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class WhoisAnalyzer implements GetGeo {

    @Override
    public String analyzeGeo(String domain) {
        String whoisUrl = "https://www.whois.com/whois/" + domain;

        try {
            // 发送 HTTP 请求获取 WHOIS HTML 页面
            Document doc = Jsoup.connect(whoisUrl).get();

            Map<String, String> geoInfo = extractGeoInfo(doc);

            return CityMapping.getStandardCityName(geoInfo.getOrDefault("State", "none"));

        } catch (Exception e) {
            return "none";
        }
    }

    public static void main(String[] args) {
        WhoisAnalyzer w = new WhoisAnalyzer();
        System.out.println(w.analyzeGeo("aliyun.com"));
    }

    /**
     * 从 WHOIS HTML 中提取地理位置信息
     */
    private static Map<String, String> extractGeoInfo(Document doc) {
        Map<String, String> geoInfo = new HashMap<>();

        // 查找 "Registrant Contact" 区块
        Elements blocks = doc.select(".df-block");
        for (Element block : blocks) {
            String heading = block.selectFirst(".df-heading").text();
            if (heading.contains("Registrant Contact")) {  // 找到注册人信息区块
                Elements rows = block.select(".df-row");
                for (Element row : rows) {
                    String label = row.select(".df-label").text();
                    String value = row.select(".df-value").text();

                    if (label.equals("State:")) {
                        geoInfo.put("State", value);
                    }
                }
                break;
            }
        }
        return geoInfo;
    }
}
