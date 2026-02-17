package com.mes.cloud.material.infrastructure.sentinel;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * [Spring Cloud Alibaba: Sentinel 異常分類處理器]
 * [SOLID: SRP - 只負責將 Sentinel BlockException 子類別轉換為可讀的錯誤訊息]
 *
 * <h2>教學重點：Sentinel BlockException 的子類別</h2>
 *
 * <h3>BlockException 的五種子類別</h3>
 * <ul>
 *   <li><b>FlowException</b>: 流量控制異常。
 *       當請求的 QPS 或併發執行緒數超過設定的閾值時觸發。
 *       HTTP 語意: 429 Too Many Requests</li>
 *
 *   <li><b>DegradeException</b>: 熔斷降級異常。
 *       當資源的慢呼叫比例、異常比例或異常數超過閾值時觸發熔斷，
 *       熔斷期間所有請求直接拋出此異常。
 *       HTTP 語意: 503 Service Unavailable</li>
 *
 *   <li><b>ParamFlowException</b>: 熱點參數限流異常。
 *       針對特定參數值進行限流，例如限制某個用戶 ID 的請求頻率。
 *       HTTP 語意: 429 Too Many Requests</li>
 *
 *   <li><b>SystemBlockException</b>: 系統自適應限流異常。
 *       當系統負載（CPU 使用率、平均 RT 等）超過閾值時觸發。
 *       HTTP 語意: 503 Service Unavailable</li>
 *
 *   <li><b>AuthorityException</b>: 授權規則異常。
 *       當請求的來源不在白名單或在黑名單中時觸發。
 *       HTTP 語意: 403 Forbidden</li>
 * </ul>
 */
public final class SentinelExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SentinelExceptionHandler.class);

    private SentinelExceptionHandler() {
        // 工具類別，禁止實例化
    }

    /**
     * 將 BlockException 轉換為可讀的錯誤訊息和 HTTP 狀態碼。
     *
     * @param ex BlockException
     * @return 包含 HTTP 狀態碼和錯誤訊息的陣列 [statusCode, message]
     */
    public static Object[] handleBlockException(BlockException ex) {
        if (ex instanceof FlowException) {
            log.warn("流量控制: resource={}, rule={}", ex.getRule().getResource(), ex.getRule());
            return new Object[]{429, "請求過於頻繁，請稍後重試"};
        }

        if (ex instanceof DegradeException) {
            log.warn("熔斷降級: resource={}, rule={}", ex.getRule().getResource(), ex.getRule());
            return new Object[]{503, "服務暫時不可用（熔斷中），請稍後重試"};
        }

        if (ex instanceof ParamFlowException) {
            log.warn("熱點參數限流: resource={}, rule={}", ex.getRule().getResource(), ex.getRule());
            return new Object[]{429, "該參數的請求過於頻繁，請稍後重試"};
        }

        if (ex instanceof SystemBlockException) {
            log.warn("系統自適應限流: rule={}", ex.getRule());
            return new Object[]{503, "系統負載過高，請稍後重試"};
        }

        if (ex instanceof AuthorityException) {
            log.warn("授權規則攔截: resource={}, rule={}", ex.getRule().getResource(), ex.getRule());
            return new Object[]{403, "無權存取此資源"};
        }

        log.warn("未知的 Sentinel 攔截: {}", ex.getClass().getSimpleName());
        return new Object[]{429, "請求被攔截，請稍後重試"};
    }
}
