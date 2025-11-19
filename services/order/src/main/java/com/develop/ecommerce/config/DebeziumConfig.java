//package com.develop.ecommerce.config;
//
//import io.debezium.config.Configuration;
//import io.debezium.engine.ChangeEvent;
//import io.debezium.engine.DebeziumEngine;
//import io.debezium.engine.format.Json;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.kafka.core.KafkaTemplate;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
//@org.springframework.context.annotation.Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class DebeziumConfig {
//
//    private final KafkaTemplate<String, String> kafkaTemplate;
//    private final ExecutorService executor = Executors.newSingleThreadExecutor();
//
//    @PostConstruct
//    public void startDebeziumEngine() {
//        // 1. Cấu hình Debezium Embedded
//        Configuration config = Configuration.create()
//                .with("name", "order-outbox-engine")
//                .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
//                .with("plugin.name", "pgoutput")
//                .with("database.hostname", "postgres")
//                .with("database.port", "5432")
//                .with("database.user", "develop")
//                .with("database.password", "develop")
//                .with("database.dbname", "order")
//                .with("database.server.name", "order_db")
//                .with("slot.name", "order_outbox_slot")
//                .with("publication.name", "order_outbox_pub")
//                .with("table.include.list", "public.order_outbox")
//                .with("tombstones.on.delete", "false")
//                .build();
//
//        // 2. Tạo Debezium Engine
//        DebeziumEngine<ChangeEvent<String, String>> engine = DebeziumEngine.create(Json.class)
//                .using(config.asProperties())
//                .notifying(changeEvent -> {
//                    try {
//                        kafkaTemplate.send("order-events", changeEvent.value());
//                        log.info("Sent order outbox event: {}", changeEvent.value());
//                    } catch (Exception e) {
//                        log.error("Failed to send order outbox event", e);
//                    }
//                })
//                .build();
//
//        // 3. Chạy engine async
//        executor.submit(() -> {
//            try {
//                engine.run();
//            } catch (Exception e) {
//                log.error("Debezium engine stopped unexpectedly", e);
//            }
//        });
//    }
//}
