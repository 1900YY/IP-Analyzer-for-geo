package com.liyang.app.utils;

import com.liyang.app.entity.Unknown;
import com.liyang.app.mapper.UnknownMapper;
import com.liyang.app.service.GetGeo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.List;

@Component
public class SSLCertificateAnalyzer implements GetGeo {

    public String analyzeGeo(String domain) {
        String urlString = "https://" + domain;

        try {
            URL url = new URL(urlString);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.connect();

            Certificate[] certificates = conn.getServerCertificates();
            for (Certificate cert : certificates) {
                if (cert instanceof X509Certificate) {
                    X509Certificate x509Cert = (X509Certificate) cert;
                    String subject = x509Cert.getSubjectX500Principal() + "";
//                    int index = subject.indexOf("L=");
//                    if(index == -1){
//                        int index1 = subject.indexOf("ST=");
//                        return CityMapping.getStandardCityName(substringUntilComma(subject, index1+3));
//                    }else{
//                        return CityMapping.getStandardCityName(substringUntilComma(subject, index+2));
//                    }
                    int index1 = subject.indexOf("ST=");
                    String province = substringUntilComma(subject, index1+3);
                    String[] list = province.split(" ");
                    return CityMapping.getStandardCityName(list[0]);
                }
            }
        } catch (Exception e) {
            return "none";
        }
        return "none";
    }
    public static String substringUntilComma(String str, int startIndex) {
        // 找到从 startIndex 开始第一个逗号的位置
        int commaIndex = str.indexOf(',', startIndex);

        // 如果找不到逗号，则返回从 startIndex 到字符串末尾的子字符串
        if (commaIndex == -1) {
            return str.substring(startIndex);
        }

        // 返回从 startIndex 到逗号位置的子字符串
        return str.substring(startIndex, commaIndex);
    }

    public static void main(String[] args){
        SSLCertificateAnalyzer ss = new SSLCertificateAnalyzer();
        System.out.println(ss.analyzeGeo("qq.com"));
    }
}
