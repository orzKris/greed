package com.kris.greed.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Kris
 * @date 2019/2/1
 */
@Component
@ConfigurationProperties(prefix = "call-class")
@Data
public class CallMap {

    /**
     * key: serviceId, value: 调用处理类
     */
    private Map<String, String> map;
}
