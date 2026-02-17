package com.mes.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [Spring Boot: Application Entry Point]
 * [Spring Cloud Alibaba: Nacos + Sentinel 整合]
 *
 * MES Spring Cloud Alibaba 模組的啟動類別。
 * 展示 Spring Cloud Alibaba 微服務治理：
 *
 * <h2>模組架構特點</h2>
 * <ul>
 *   <li>DDD 戰術設計模式（Aggregate Root, Value Object, Domain Event, Repository）</li>
 *   <li>六角形架構（Hexagonal Architecture / Ports and Adapters）</li>
 *   <li>完整 CQRS（Command/Query 分離）</li>
 *   <li>Nacos 服務發現與配置中心整合</li>
 *   <li>Sentinel 流量控制與熔斷降級</li>
 * </ul>
 *
 * <h2>Spring Cloud Alibaba 組件</h2>
 * <ul>
 *   <li><b>Nacos Discovery</b>: 服務註冊與發現</li>
 *   <li><b>Nacos Config</b>: 動態配置管理</li>
 *   <li><b>Sentinel</b>: 流量控制、熔斷降級、系統保護</li>
 * </ul>
 */
@SpringBootApplication
public class MesCloudAlibabaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesCloudAlibabaApplication.class, args);
    }
}
