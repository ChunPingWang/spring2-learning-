package com.mes.cloud.material.infrastructure.sentinel;

import com.mes.cloud.material.application.query.dto.MaterialView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * [Spring Cloud Alibaba: Sentinel 集中式降級處理器]
 * [SOLID: SRP - 只負責提供 Sentinel 降級的回傳值]
 *
 * <h2>教學重點：集中式 Fallback</h2>
 * <p>
 * 當多個 @SentinelResource 需要共用相同的降級邏輯時，
 * 可以將 fallback 方法提取到獨立的類別中，使用 {@code fallbackClass} 屬性引用。
 * </p>
 *
 * <h3>使用方式</h3>
 * <pre>
 * @SentinelResource(
 *     value = "someResource",
 *     fallback = "fallbackMethod",
 *     fallbackClass = SentinelFallbackHandler.class
 * )
 * </pre>
 *
 * <p>注意：fallbackClass 中的方法必須是 static 方法。</p>
 *
 * <h3>適用場景</h3>
 * <ul>
 *   <li>多個服務呼叫需要相同的降級回傳值</li>
 *   <li>降級邏輯較複雜，不適合內聯在業務類別中</li>
 *   <li>需要統一管理所有降級策略</li>
 * </ul>
 */
public final class SentinelFallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(SentinelFallbackHandler.class);

    private SentinelFallbackHandler() {
        // 工具類別，禁止實例化
    }

    /**
     * 查詢物料的通用降級方法。
     *
     * @param materialId 物料 ID
     * @param ex         異常
     * @return 降級的空檢視
     */
    public static MaterialView getMaterialFallback(String materialId, Throwable ex) {
        log.warn("getMaterial 集中式降級: materialId={}, reason={}", materialId, ex.getMessage());
        MaterialView fallbackView = new MaterialView();
        fallbackView.setId(materialId);
        fallbackView.setName("服務降級中，資料暫時無法取得");
        return fallbackView;
    }

    /**
     * 查詢物料列表的通用降級方法。
     *
     * @param ex 異常
     * @return 空列表
     */
    public static List<MaterialView> listMaterialsFallback(Throwable ex) {
        log.warn("listMaterials 集中式降級: reason={}", ex.getMessage());
        return Collections.emptyList();
    }
}
