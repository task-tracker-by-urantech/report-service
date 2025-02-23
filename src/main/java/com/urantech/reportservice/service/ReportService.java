package com.urantech.reportservice.service;

import com.urantech.reportservice.client.feign.UserClient;
import com.urantech.reportservice.model.rest.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final UserClient userClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() - 1);

    public void generateAndSendReports() {
        List<UserResponse> users = userClient.getAllUsersWithUnfinishedTasks();

        List<CompletableFuture<Void>> futures = users.stream()
                .map(user -> CompletableFuture.runAsync(() -> sendReport(user), executor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void sendReport(UserResponse user) {
        String msg = buildMessage(user);
        kafkaTemplate.send("EMAIL_SENDING", msg);
        log.info(">>> Message {} sent to EMAIL_SENDING topic: >>>", msg);
    }

    private String buildMessage(UserResponse user) {
        return "Daily report for %s. You have %d unfinished tasks"
                .formatted(user.email(), user.tasksCount());
    }
}
