package com.kris.greed.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Kris
 * @date 2019/08/25
 */
@FeignClient(name = "prophecy")
public interface ProphecyService {

    @PostMapping(value = "concurrent/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    String call(@PathVariable("id") String id, @RequestParam("request") String param);
}
