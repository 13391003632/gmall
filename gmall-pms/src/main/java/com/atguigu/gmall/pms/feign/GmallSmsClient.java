package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author qjx
 * @create 2019-11-01 23:54
 **/
@FeignClient("sms-service")
public interface GmallSmsClient extends GmallSmsApi {
}
