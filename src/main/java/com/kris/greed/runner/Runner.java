package com.kris.greed.runner;

import com.kris.greed.dump.DumpData;
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
    private DumpData dumpData;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        dumpData.dump();
    }
}
