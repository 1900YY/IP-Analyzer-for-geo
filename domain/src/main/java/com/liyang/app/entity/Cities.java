package com.liyang.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("geo_count")
public class Cities {
    @TableId(value = "id",type = IdType.AUTO)
    private long id;
    String cityName;
    long number;
}
