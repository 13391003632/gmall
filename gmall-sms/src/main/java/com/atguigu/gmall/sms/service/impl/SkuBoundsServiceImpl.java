package com.atguigu.gmall.sms.service.impl;

import com.atguigu.gmall.sms.dao.SkuFullReductionDao;
import com.atguigu.gmall.sms.dao.SkuLadderDao;
import com.atguigu.gmall.sms.entity.SkuFullReductionEntity;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.gmall.sms.vo.SaleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.sms.dao.SkuBoundsDao;
import com.atguigu.gmall.sms.entity.SkuBoundsEntity;
import com.atguigu.gmall.sms.service.SkuBoundsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("skuBoundsService")
public class SkuBoundsServiceImpl extends ServiceImpl<SkuBoundsDao, SkuBoundsEntity> implements SkuBoundsService {
    @Autowired
    private SkuLadderDao skuLadderDao;
    @Autowired
    private SkuFullReductionDao skuFullReductionDao;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SkuBoundsEntity> page = this.page(
                new Query<SkuBoundsEntity>().getPage(params),
                new QueryWrapper<SkuBoundsEntity>()
        );

        return new PageVo(page);
    }
    @Override
    @Transactional
    public void saleSale(SaleVo saleVo) {
        //3.1新增积分：skuBounds
        SkuBoundsEntity skuBoundsEntity = new SkuBoundsEntity();
        skuBoundsEntity.setBuyBounds(saleVo.getBuyBounds());
        skuBoundsEntity.setGrowBounds(saleVo.getGrowBounds());
        skuBoundsEntity.setSkuId(saleVo.getSkuId());
        List<Integer> works = saleVo.getWork();
        if(!CollectionUtils.isEmpty(works)&&works.size()==4){
            skuBoundsEntity.setWork(works.get(3)*1+works.get(2)*2+works.get(1)*4+works.get(0)*8
            );
        }
        this.save(skuBoundsEntity);
        //3.2新增打折消息：skuLadder
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        skuLadderEntity.setFullCount(saleVo.getFullCount());
        skuLadderEntity.setDiscount(saleVo.getDiscount());
        skuLadderEntity.setAddOther(saleVo.getAddOther());
        skuBoundsEntity.setSkuId(saleVo.getSkuId());
        skuLadderDao.insert(skuLadderEntity);

        //3.3新增满减信息
        SkuFullReductionEntity reductionEntity = new SkuFullReductionEntity();
        reductionEntity.setFullPrice(saleVo.getFullPrice());
        reductionEntity.setReducePrice(saleVo.getReducePrice());
        reductionEntity.setAddOther(saleVo.getAddOther());
        reductionEntity.setSkuId(saleVo.getSkuId());
        skuFullReductionDao.insert(reductionEntity);


        
    }


}