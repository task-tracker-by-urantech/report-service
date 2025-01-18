package com.urantech.reportservice.schedule;

import com.urantech.reportservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Scheduler {
    private final ReportService reportService;

    @Scheduled(cron = "${spring.scheduler.cron}")
    public void generateDailyReport() {
        reportService.generateAndSendReports();
    }
}
