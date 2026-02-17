package com.mes.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [Spring Boot Application: MES Redis 看板模組]
 *
 * Module 7 - 展示 Redis 快取與即時數據看板：
 * <ul>
 *   <li>Redis Cache - 使用 Redis 作為快取層（Cache-Aside, Write-Through）</li>
 *   <li>Spring Cache Abstraction - @Cacheable, @CachePut, @CacheEvict</li>
 *   <li>Redis Data Types - String, Hash, List, Set, Sorted Set</li>
 *   <li>DDD - Domain-Driven Design（Aggregate Root, Value Object, Domain Event）</li>
 *   <li>CQRS - 命令查詢職責分離</li>
 *   <li>Hexagonal Architecture - 六角形架構</li>
 * </ul>
 */
@SpringBootApplication
public class MesRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesRedisApplication.class, args);
    }
}
