package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @author qjx
 * @create 2019-11-01 8:31
 **/
@Data
public class SpuInfoVO extends SpuInfoEntity {
    /**
     * spuInfo扩展对象
     * 包含：spuInfo基本信息、spuImages图片信息、baseAttrs基本属性信息、skus信息
     */


        // 图片信息
        private List<String> spuImages;

        // 基本属性信息
        private List<ProductAttrValueVO> baseAttrs;

        // sku信息
        private List<SkuInfoVO> skus;

}
