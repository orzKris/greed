package com.kris.greed.runner;

import com.kris.greed.service.DataDevelopService;
import com.kris.greed.service.MobileOperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author by Kris
 * @date 2019/08/20
 */
@Component
public class Runner implements ApplicationRunner {

    @Autowired
    private MobileOperatorService mobileOperatorService;
    @Autowired
    private DataDevelopService dataDevelopService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mobileOperatorService.dump();
//        dataDevelopService.dump();
    }
}
