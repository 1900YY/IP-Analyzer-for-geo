package com.liyang.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liyang.app.entity.IpEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DomainMapper extends BaseMapper<IpEntity> {
}
