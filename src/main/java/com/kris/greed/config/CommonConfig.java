package com.kris.greed.config;

import com.kris.greed.config.interfaceConfig.DataDevelopment;
import com.kris.greed.config.interfaceConfig.DomesticProduct;
import com.kris.greed.config.interfaceConfig.MobileOperator;
import com.kris.greed.config.interfaceConfig.NationalPopulation;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kris
 * @date 2019/08/23
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "com.kris.greed")
public class CommonConfig {

    private String filePath;

    private String url;

    private DataDevelopment dataDevelopment;

    private MobileOperator mobileOperator;

    private DomesticProduct domesticProduct;

    private NationalPopulation nationalPopulation;

}
