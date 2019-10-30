package com.atguigu.gmall.pms;

import com.atguigu.gmall.pms.dao.BrandDao;
import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.QueryChainWrapper;
import com.sun.corba.se.impl.naming.cosnaming.BindingIteratorImpl;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
class GmallPmsApplicationTests {
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private BrandService brandService;

    @Test
    void contextLoads() {
    }
    @Test
    public  void test(){
       /* BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("尚硅谷正好");
        brandEntity.setFirstLetter("s");
        brandEntity.setLogo("www.baidu.com/log.gif");
        brandEntity.setName("反对法");
        brandEntity.setShowStatus(0);
        brandEntity.setSort(1);*/
        // HashMap<String, Object> map = new HashMap<>();
        // map.put("name", "fds发多少");
        //
        //
        // brandDao.deleteByMap(map);
      //  System.out.println(brandDao.selectList(new QueryWrapper<BrandEntity>().eq("name", "反对法")));
        IPage<BrandEntity> page =brandDao.selectPage(new Page<BrandEntity>(21, 2), new QueryWrapper<BrandEntity>());
        IPage<BrandEntity> page2=brandService.page(new Page<BrandEntity>(21, 2), new QueryWrapper<BrandEntity>().eq("sort","1"));

        System.out.println(page2.getRecords());
        System.out.println(page2.getTotal());
        System.out.println(page2.getPages());

    }

}
