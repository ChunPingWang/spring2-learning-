package com.mes.cloud.material.infrastructure.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * [Spring Cloud Alibaba: Sentinel 流量控制規則配置]
 * [SOLID: SRP - 只負責 Sentinel 流控與熔斷規則的初始化]
 *
 * <h2>教學重點：Sentinel 流控規則</h2>
 *
 * <h3>FlowRule（流控規則）</h3>
 * <ul>
 *   <li><b>QPS 模式 (FLOW_GRADE_QPS)</b>: 每秒查詢數限制。
 *       當 QPS 超過閾值時，後續請求會被拒絕（拋出 FlowException）。
 *       適用於讀取操作或對外暴露的 API。</li>
 *   <li><b>Thread Count 模式 (FLOW_GRADE_THREAD)</b>: 併發執行緒數限制。
 *       當正在處理的執行緒數超過閾值時，後續請求會被拒絕。
 *       適用於耗時較長的寫入操作。</li>
 * </ul>
 *
 * <h3>DegradeRule（熔斷降級規則）</h3>
 * <ul>
 *   <li><b>慢呼叫比例 (SLOW_REQUEST_RATIO)</b>: 當慢呼叫（超過 RT 閾值）的比例
 *       超過設定值時，觸發熔斷。熔斷期間所有請求直接走降級邏輯。</li>
 *   <li><b>異常比例 (ERROR_RATIO)</b>: 當異常比例超過設定值時觸發熔斷。</li>
 *   <li><b>異常數 (ERROR_COUNT)</b>: 當異常數量超過設定值時觸發熔斷。</li>
 * </ul>
 *
 * <h3>生產環境建議</h3>
 * <p>
 * 在生產環境中，通常透過 Nacos 配置中心或 Sentinel Dashboard 來管理規則，
 * 而非硬編碼在程式中。這裡使用程式碼初始化是為了教學目的。
 * </p>
 */
@Configuration
public class SentinelFlowRuleConfig {

    private static final Logger log = LoggerFactory.getLogger(SentinelFlowRuleConfig.class);

    /**
     * 初始化 Sentinel 流控規則與熔斷規則。
     * 使用 @PostConstruct 確保在 Bean 初始化完成後執行。
     */
    @PostConstruct
    public void initRules() {
        initFlowRules();
        initDegradeRules();
    }

    /**
     * 初始化流控規則。
     */
    private void initFlowRules() {
        List<FlowRule> rules = new ArrayList<FlowRule>();

        // 規則 1: getMaterial - QPS 模式，閾值 100
        // 每秒最多處理 100 次查詢請求
        FlowRule getMaterialRule = new FlowRule();
        getMaterialRule.setResource("getMaterial");
        getMaterialRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        getMaterialRule.setCount(100);
        rules.add(getMaterialRule);

        // 規則 2: consumeMaterial - Thread Count 模式，閾值 20
        // 最多同時有 20 個執行緒在處理消耗操作
        // 使用 Thread Count 模式是因為消耗操作涉及庫存扣減，需要控制併發
        FlowRule consumeMaterialRule = new FlowRule();
        consumeMaterialRule.setResource("consumeMaterial");
        consumeMaterialRule.setGrade(RuleConstant.FLOW_GRADE_THREAD);
        consumeMaterialRule.setCount(20);
        rules.add(consumeMaterialRule);

        // 規則 3: registerMaterial - QPS 模式，閾值 50
        FlowRule registerMaterialRule = new FlowRule();
        registerMaterialRule.setResource("registerMaterial");
        registerMaterialRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        registerMaterialRule.setCount(50);
        rules.add(registerMaterialRule);

        FlowRuleManager.loadRules(rules);
        log.info("已初始化 {} 條 Sentinel 流控規則", rules.size());
        for (FlowRule rule : rules) {
            log.info("  流控規則: resource={}, grade={}, count={}",
                    rule.getResource(),
                    rule.getGrade() == RuleConstant.FLOW_GRADE_QPS ? "QPS" : "THREAD",
                    rule.getCount());
        }
    }

    /**
     * 初始化熔斷降級規則。
     */
    private void initDegradeRules() {
        List<DegradeRule> rules = new ArrayList<DegradeRule>();

        // 規則: getMaterial - 慢呼叫比例模式
        // 當超過 500ms 的慢呼叫比例超過 50% 時觸發熔斷
        DegradeRule degradeRule = new DegradeRule();
        degradeRule.setResource("getMaterial");
        degradeRule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        degradeRule.setCount(500);          // RT 閾值 500ms
        degradeRule.setSlowRatioThreshold(0.5);   // 慢呼叫比例閾值 50%
        degradeRule.setTimeWindow(10);       // 熔斷持續時間 10 秒
        degradeRule.setMinRequestAmount(5);  // 最小請求數 5
        degradeRule.setStatIntervalMs(10000); // 統計時間窗口 10 秒
        rules.add(degradeRule);

        DegradeRuleManager.loadRules(rules);
        log.info("已初始化 {} 條 Sentinel 熔斷規則", rules.size());
        for (DegradeRule rule : rules) {
            log.info("  熔斷規則: resource={}, grade=RT, count={}ms, slowRatio={}",
                    rule.getResource(), rule.getCount(), rule.getSlowRatioThreshold());
        }
    }
}
