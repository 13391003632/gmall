package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.vo.SaleVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品sku积分设置
 *
 * @author lixianfeng
 * @email lxf@atguigu.com
 * @date 2019-10-28 22:38:23
 */
public interface SkuBoundsService extends IService<SkuBoundsEntity> {

    PageVo queryPage(QueryCondition params);

    void saleSale(SaleVo saleVo);
}

