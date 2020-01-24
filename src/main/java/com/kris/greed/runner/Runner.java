package com.kris.greed.runner;

import com.kris.greed.config.ApplicationContextRegister;
import com.kris.greed.enums.ServiceCode;
import com.kris.greed.model.CallMap;
import com.kris.greed.model.DumpService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author by Kris
 * @date 2019/08/20
 */
@Log4j2
//@Component
@Deprecated
public class Runner implements ApplicationRunner {

    @Autowired
    private CallMap callMap;

    @Autowired
    private ApplicationContextRegister applicationContextRegister;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, String> serviceMap = callMap.getMap();
        for (Map.Entry<String, String> entry : serviceMap.entrySet()) {
            DumpService dumpService = (DumpService) applicationContextRegister.getApplicationContext()
                    .getBean(entry.getValue());
            log.info("[SERVICE]: " + entry.getValue());
            try {
                dumpService.dump();
            } catch (Exception e) {
                log.error(entry.getValue() + "服务调用失败", e);
            }
        }
    }

    @Scheduled(cron = "0 0 12 * * ?")
    private void scheduledTask() throws IOException {
        log.info("SCHEDULED TASK BEGIN----------");
        DumpService dumpService = (DumpService) applicationContextRegister.getApplicationContext()
                .getBean(ServiceCode.MOBILE_LOCATION);
        dumpService.dump();
    }
}
