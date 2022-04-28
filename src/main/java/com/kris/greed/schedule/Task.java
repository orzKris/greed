package com.kris.greed.schedule;

import com.kris.greed.dump.DumpData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author by Kris
 * @date 2019/08/20
 */
@Log4j2
@Component
public class Task {

    @Autowired
    private DumpData dumpData;

    @Scheduled(cron = "0 0 12 * * ?")
    private void scheduledTask() throws IOException, ParseException {
        log.info("SCHEDULED TASK BEGIN----------");
        dumpData.dump();
    }
}
