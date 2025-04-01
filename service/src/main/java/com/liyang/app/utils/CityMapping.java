package com.liyang.app.utils;

import java.util.HashMap;
import java.util.Map;

public class CityMapping {
    public static final Map<String, String> CITY_MAPPING = new HashMap<>();

    static {
        // 北京
        CITY_MAPPING.put("北京市", "北京");
        CITY_MAPPING.put("北京", "北京");
        CITY_MAPPING.put("Beijing", "北京");
        CITY_MAPPING.put("beijing", "北京");

        // 广东
        CITY_MAPPING.put("广东市", "广东");
        CITY_MAPPING.put("广东", "广东");
        CITY_MAPPING.put("Guangdong", "广东");
        CITY_MAPPING.put("guangdong", "广东");

        // 浙江
        CITY_MAPPING.put("浙江市", "浙江");
        CITY_MAPPING.put("浙江", "浙江");
        CITY_MAPPING.put("Zhejiang", "浙江");
        CITY_MAPPING.put("zhejiang", "浙江");

    }

    public static String getStandardCityName(String input) {
        input = input.trim(); // 去掉前后空格
        return CITY_MAPPING.getOrDefault(input, "");
    }
}