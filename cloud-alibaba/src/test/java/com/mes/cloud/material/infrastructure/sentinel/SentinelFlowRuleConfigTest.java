package com.mes.cloud.material.infrastructure.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [Spring Cloud Alibaba: Sentinel 流控規則配置測試]
 *
 * 直接呼叫 @PostConstruct 方法，驗證 FlowRuleManager 中的規則。
 */
@DisplayName("SentinelFlowRuleConfig 測試")
class SentinelFlowRuleConfigTest {

    private SentinelFlowRuleConfig config;

    @BeforeEach
    void setUp() {
        // 清空已有規則避免測試互相影響
        FlowRuleManager.loadRules(null);
        DegradeRuleManager.loadRules(null);
        config = new SentinelFlowRuleConfig();
    }

    @Test
    @DisplayName("應初始化 3 條流控規則")
    void shouldInitializeThreeFlowRules() {
        config.initRules();

        List<FlowRule> rules = FlowRuleManager.getRules();
        assertThat(rules).hasSize(3);
    }

    @Test
    @DisplayName("getMaterial 規則應為 QPS 模式，閾值 100")
    void shouldHaveGetMaterialQpsRule() {
        config.initRules();

        List<FlowRule> rules = FlowRuleManager.getRules();
        FlowRule getMaterialRule = null;
        for (FlowRule rule : rules) {
            if ("getMaterial".equals(rule.getResource())) {
                getMaterialRule = rule;
                break;
            }
        }

        assertThat(getMaterialRule).isNotNull();
        assertThat(getMaterialRule.getGrade()).isEqualTo(RuleConstant.FLOW_GRADE_QPS);
        assertThat(getMaterialRule.getCount()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("consumeMaterial 規則應為 Thread Count 模式，閾值 20")
    void shouldHaveConsumeMaterialThreadRule() {
        config.initRules();

        List<FlowRule> rules = FlowRuleManager.getRules();
        FlowRule consumeRule = null;
        for (FlowRule rule : rules) {
            if ("consumeMaterial".equals(rule.getResource())) {
                consumeRule = rule;
                break;
            }
        }

        assertThat(consumeRule).isNotNull();
        assertThat(consumeRule.getGrade()).isEqualTo(RuleConstant.FLOW_GRADE_THREAD);
        assertThat(consumeRule.getCount()).isEqualTo(20.0);
    }
}
