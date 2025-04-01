package com.liyang.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("unknown_list")
public class Unknown {
    @TableId(value = "id",type = IdType.AUTO)
    private long id;
    private String domain;
}
