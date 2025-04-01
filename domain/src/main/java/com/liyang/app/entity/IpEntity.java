package com.liyang.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("domain_schema")
public class IpEntity {
    @TableId(value = "id",type = IdType.AUTO)
    private long id;
    private String domain;
    private String geo;
}
