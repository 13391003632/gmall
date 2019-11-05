package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.service.SpuInfoDescService;
import com.atguigu.gmall.pms.vo.ProductAttrValueVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.gmall.sms.vo.SaleVo;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescDao spuInfoDescDao;
    @Autowired
    private ProductAttrValueDao productAttrValueDao;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SkuImagesDao skuImagesDao;
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private SkuSaleAttrValueDao saleAttrValueDao;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private GmallSmsClient smsClient;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuInfo(QueryCondition condition, Long catId) {
         // 封装分页条件
        IPage<SpuInfoEntity> page = new Query<SpuInfoEntity>().getPage(condition);
        // 封装查询条件
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        // 如果分类id不为0，要根据分类id查，否则查全部
        if(catId!=0){
            wrapper.eq("catalog_id", catId);
        }
        // 如果用户输入了检索条件，根据检索条件查
        String key=condition.getKey();
        if(StringUtils.isNotBlank(key)){
            wrapper.and(t -> t.like("spu_name", key).or().like("id", key));
        }
        return new PageVo(this.page(page,wrapper));
    }

    @Override
    @GlobalTransactional
    public void saveSpuInfoVO(SpuInfoVO spuInfoVO) {
        /// 1.保存spu相关
        // 1.1. 保存spu基本信息 spu_info
        Long spuId = saveSpuInfo(spuInfoVO);
        //1.2新增spuInfoDesc
      spuInfoDescService.saveSpuDesc(spuInfoVO, spuId);
        //int i=1/0;

        //1.3新增基本属性productAttrValue
        saveBaseAttr(spuInfoVO, spuId);

        //2.新增sku相关的3张表 spuId
        saveSku(spuInfoVO, spuId);


    }

    private void saveSku(SpuInfoVO spuInfoVO, Long spuId) {
        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        if(CollectionUtils.isEmpty(skus)){
            return;
        }
        skus.forEach(skuInfoVO -> {
            //2.1新增skuInfo
            SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
            BeanUtils.copyProperties(skuInfoVO,skuInfoEntity);
            skuInfoEntity.setBrandId(spuInfoVO.getBrandId());
            skuInfoEntity.setCatalogId(spuInfoVO.getCatalogId());
            skuInfoEntity.setSkuCode(UUID.randomUUID().toString());
            skuInfoEntity.setSpuId(spuId);
            List<String> images = skuInfoVO.getImages();
            //设置默认图片
            if(!CollectionUtils.isEmpty(images)){
                skuInfoEntity.setSkuDefaultImg(StringUtils.isNotBlank(skuInfoEntity.getSkuDefaultImg())?skuInfoEntity.getSkuDefaultImg():images.get(0));

            }
            skuInfoDao.insert(skuInfoEntity);
            Long skuId = skuInfoEntity.getSkuId();
            //2.2新增sku的图片
            if(!CollectionUtils.isEmpty(images)){
                images.forEach(image ->{
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(StringUtils.equals(image,skuInfoEntity.getSkuDefaultImg())?1:0);
                    skuImagesEntity.setImgSort(0);
                    skuImagesEntity.setImgUrl(image);
                    skuImagesDao.insert(skuImagesEntity);
                });
            }
            //2.3新增销售属性
            List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
            if(!CollectionUtils.isEmpty(saleAttrs)){
                saleAttrs.forEach(saleAttr ->{
                    saleAttr.setSkuId(skuId);
                    saleAttr.setAttrSort(0);
                    saleAttrValueDao.insert(saleAttr);
                });
            }
    //3.新增营销相关的3张表
            SaleVo saleVo = new SaleVo();
            BeanUtils.copyProperties(skuInfoVO,saleVo);
            smsClient.saleSale(saleVo);

        });
    }

    private void saveBaseAttr(SpuInfoVO spuInfoVO, Long spuId) {
        List<ProductAttrValueVO> baseAttrs = spuInfoVO.getBaseAttrs();
        baseAttrs.forEach(baseAttr ->{
            baseAttr.setSpuId(spuId);
            baseAttr.setAttrSort(0);
            baseAttr.setQuickShow(1);
            productAttrValueDao.insert(baseAttr);
        });
    }



    private Long saveSpuInfo(SpuInfoVO spuInfoVO) {
        spuInfoVO.setPublishStatus(1); // 默认是已上架
        spuInfoVO.setCreateTime(new Date());
        spuInfoVO.setUodateTime(spuInfoVO.getCreateTime()); // 新增时，更新时间和创建时间一致
        this.save(spuInfoVO);
        return spuInfoVO.getId();
    }

}