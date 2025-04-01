package com.liyang.app.utils;

import com.jthinking.common.util.ip.IPInfo;
import com.jthinking.common.util.ip.IPInfoUtils;
import com.liyang.app.entity.Unknown;
import com.liyang.app.mapper.UnknownMapper;
import com.liyang.app.service.GetGeo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class IpdataAnalyzer implements GetGeo {

    @Override
    public String analyzeGeo(String domain) {
        String ip = Domain2IP(domain);
        IPInfo ipInfo = IPInfoUtils.getIpInfo(ip);

        return CityMapping.getStandardCityName(ipInfo.getProvince());
    }

    public String Domain2IP(String domain){
        try {
            // 根据域名获取 InetAddress 对象
            InetAddress address = InetAddress.getByName(domain);
            // 获取 IP 地址字符串
            String ip = address.getHostAddress();
            return ip;
        } catch (UnknownHostException e) {
            return "none";
        }
    }

    public static void main(String[] args){
        IpdataAnalyzer ipdataAnalyzer = new IpdataAnalyzer();
        System.out.println(ipdataAnalyzer.analyzeGeo("qq.com"));
    }
}
