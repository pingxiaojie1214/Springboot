package com.ping.student.timeTask;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
@Component
public class MyTimeTask {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

    @Scheduled(cron = "0/10 * * * * *")
    public void task01() {
        System.out.println(Thread.currentThread().getName() + "----> task01============"+dateFormat.format(new Date()));
    }

    @Scheduled(cron = "0/15 * * * * ?")
    public void task02() {
        System.out.println(Thread.currentThread().getName() + "----> task02============"+dateFormat.format(new Date()));
    }
}
