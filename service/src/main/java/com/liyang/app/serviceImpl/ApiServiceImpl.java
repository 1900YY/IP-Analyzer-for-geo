package com.liyang.app.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.liyang.app.api.CurdApi;
import com.liyang.app.entity.Cities;
import com.liyang.app.entity.IpEntity;
import com.liyang.app.entity.Unknown;
import com.liyang.app.mapper.CitiesMapper;
import com.liyang.app.mapper.DomainMapper;
import com.liyang.app.mapper.UnknownMapper;
import com.liyang.app.utils.CoreUtils;
import com.liyang.app.utils.IpdataAnalyzer;
import com.liyang.app.utils.SSLCertificateAnalyzer;
import com.liyang.app.utils.WhoisAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApiServiceImpl implements CurdApi {

    @Autowired
    DomainMapper domainMapper;

    @Autowired
    CitiesMapper citiesMapper;

    @Autowired
    UnknownMapper unknownMapper;

    @Autowired
    CoreUtils coreUtils;

    @Autowired
    IpdataAnalyzer ipdataAnalyzer;

    @Autowired
    SSLCertificateAnalyzer sslCertificateAnalyzer;

    @Autowired
    WhoisAnalyzer whoisAnalyzer;


    //实现api设定方法
    @Override
    public long countByGeo(String geo) {
        QueryWrapper<IpEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("province", geo);
        return domainMapper.selectList(queryWrapper).size();
    }

    @Override
    public void dealDomainList(String txtPath) throws IOException {
        List<String> domainsTxt = coreUtils.readTxt(txtPath);
        for(String s: domainsTxt){
            dealIp(s);
        }
    }

    public IpEntity getGeoByVote(String domain) {
        String sslGeo = sslCertificateAnalyzer.analyzeGeo(domain);
        String ipGeo = ipdataAnalyzer.analyzeGeo(domain);
        String whoisGeo = whoisAnalyzer.analyzeGeo(domain);
        List<String> list = new ArrayList<>();
        list.add(sslGeo);
        list.add(ipGeo);
        list.add(whoisGeo);
        list.remove("none");
        IpEntity ipEntity = new IpEntity();
        ipEntity.setDomain(domain);
        if(list.size() != 0){
            String lastGeo = coreUtils.findMostFrequentElements(list).get(0);
            ipEntity.setGeo(lastGeo);
            return ipEntity;
        }
        ipEntity.setGeo("none");
        return ipEntity;
    }

    public void dealIp(String domainName) {
        IpEntity ipEntity = getGeoByVote(domainName);

        if(ipEntity.getGeo() == "none"){
            Unknown u = new Unknown();
            u.setDomain(domainName);
            unknownMapper.insertOrUpdate(u);
            return;
        }
        // 根据 domain 字段查询数据库中是否存在对应的记录
        QueryWrapper<IpEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("domain", domainName);
        IpEntity existingEntity = domainMapper.selectOne(queryWrapper);

        Cities cities = new Cities();
        cities.setCityName(ipEntity.getGeo());

        if (existingEntity != null) {
            // 如果存在，更新记录
            ipEntity.setId(existingEntity.getId());
            if(domainMapper.updateById(ipEntity) == 1){
                QueryWrapper<Cities> cityQuery = new QueryWrapper<>();
                cityQuery.eq("city_name", ipEntity.getGeo());
                cities.setNumber(citiesMapper.selectOne(cityQuery).getNumber() + 1);
                citiesMapper.updateById(cities);
            }
        } else {
            // 如果不存在，插入记录
            if(domainMapper.insert(ipEntity) == 1){
                cities.setNumber(1);
                citiesMapper.insert(cities);
            }
        }
    }


}
