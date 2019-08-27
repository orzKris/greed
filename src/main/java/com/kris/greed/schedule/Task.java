package com.kris.greed.schedule;

import com.kris.greed.service.MobileOperatorService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author by Kris
 * @date 2019/08/20
 */
@Log4j2
@Component
public class Task {

    @Autowired
    private MobileOperatorService mobileOperatorService;

    @Scheduled(cron = "0 0 12 * * ?")
    private void scheduledTask() throws IOException {
        log.info("SCHEDULED TASK BEGIN----------");
        mobileOperatorService.dump();
    }
}
