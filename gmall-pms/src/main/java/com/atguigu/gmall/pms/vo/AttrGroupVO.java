package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

/**
 * @author qjx
 * @create 2019-10-30 20:55
 **/
@Data
public class AttrGroupVO extends AttrGroupEntity {
    private List<AttrEntity> attrEntities;
    private  List<AttrAttrgroupRelationEntity> relations;
}
