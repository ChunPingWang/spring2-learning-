package com.mes.cloud.material.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * [Spring Cloud Alibaba: Nacos 配置中心 - 動態配置監聽]
 * [SOLID: SRP - 只負責監聽與管理 Nacos 動態配置]
 *
 * <h2>教學重點：Nacos 配置中心與 @RefreshScope</h2>
 *
 * <h3>為什麼需要 @RefreshScope？</h3>
 * <p>
 * 一般的 Spring Bean 在初始化時就注入了 @Value 的值，之後不會再更新。
 * 加上 @RefreshScope 後，當 Nacos 推送配置變更時，Spring Cloud 會：
 * </p>
 * <ol>
 *   <li>銷毀該 Bean 的舊實例</li>
 *   <li>重新建立 Bean，注入最新的配置值</li>
 *   <li>這就是「動態配置」的實現原理</li>
 * </ol>
 *
 * <h3>Nacos 配置推送流程</h3>
 * <ol>
 *   <li>在 Nacos Console 修改配置</li>
 *   <li>Nacos Server 透過長輪詢推送變更通知</li>
 *   <li>Spring Cloud Alibaba 的 NacosConfigManager 接收變更</li>
 *   <li>觸發 RefreshEvent，重建 @RefreshScope Bean</li>
 *   <li>Bean 的 @Value 注入最新值</li>
 * </ol>
 *
 * <h3>bootstrap.yml vs application.yml</h3>
 * <ul>
 *   <li><b>bootstrap.yml</b>: 在應用程式上下文啟動之前載入，用於配置 Nacos 配置中心的連線資訊。
 *       這是因為應用程式需要先連到 Nacos 才能取得其他配置。</li>
 *   <li><b>application.yml</b>: 正常的應用程式配置，可以被 Nacos 配置中心的配置覆蓋。
 *       適合放預設值和本地開發配置。</li>
 * </ul>
 */
@Component
@RefreshScope
public class NacosConfigListener {

    private static final Logger log = LoggerFactory.getLogger(NacosConfigListener.class);

    /**
     * 最低庫存閾值。
     * 此值可透過 Nacos 配置中心動態修改，無需重啟應用程式。
     *
     * <p>在 Nacos Console 中修改 mes.material.minimum-stock-threshold 的值，
     * 所有使用此配置的 @RefreshScope Bean 都會自動更新。</p>
     */
    @Value("${mes.material.minimum-stock-threshold:10}")
    private int minimumStockThreshold;

    /**
     * 補貨倍數。
     * 計算補貨數量時使用的倍數因子。
     */
    @Value("${mes.material.reorder-multiplier:2}")
    private int reorderMultiplier;

    /**
     * Bean 初始化後記錄當前配置值。
     * 當 @RefreshScope 觸發重建時，會再次呼叫此方法，
     * 可從日誌觀察配置是否已更新。
     */
    @PostConstruct
    public void init() {
        log.info("=== Nacos 配置已載入 ===");
        log.info("最低庫存閾值 (minimum-stock-threshold): {}", minimumStockThreshold);
        log.info("補貨倍數 (reorder-multiplier): {}", reorderMultiplier);
    }

    public int getMinimumStockThreshold() {
        return minimumStockThreshold;
    }

    public int getReorderMultiplier() {
        return reorderMultiplier;
    }
}
