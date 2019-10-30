package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.BrandEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

/**
 * 品牌
 * 
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2019-10-28 22:41:45
 */
@Mapper
public interface BrandDao extends BaseMapper<BrandEntity> {

    void page(Page<BrandEntity> brandEntityPage, QueryWrapper<BrandEntity> brandEntityQueryWrapper);
}
