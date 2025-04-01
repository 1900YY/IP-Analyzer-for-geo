package com.liyang.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.liyang.app.entity.Cities;
import com.liyang.app.mapper.CitiesMapper;
import com.liyang.app.serviceImpl.ApiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/web")
public class webController {
    @Autowired
    ApiServiceImpl apiService;

    @Autowired
    CitiesMapper citiesMapper;

    @CrossOrigin("*")
    @PostMapping("/init")
    public void init(String path) throws IOException {
        apiService.dealDomainList(path);
    }

    @CrossOrigin("*")
    @GetMapping("/cities")
    public List<Cities> returnCity(){
        QueryWrapper<Cities> queryWrapper = new QueryWrapper<>();
        queryWrapper.select();
        List<Cities> list = citiesMapper.selectList(queryWrapper);
        return list;
    }
}
