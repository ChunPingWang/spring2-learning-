package com.mes.cloud.material.application;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.mes.common.cqrs.CommandBus;
import com.mes.common.cqrs.QueryBus;
import com.mes.cloud.material.application.command.ConsumeMaterialCommand;
import com.mes.cloud.material.application.command.ReceiveMaterialCommand;
import com.mes.cloud.material.application.command.RegisterMaterialCommand;
import com.mes.cloud.material.application.query.GetLowStockMaterialsQuery;
import com.mes.cloud.material.application.query.GetMaterialQuery;
import com.mes.cloud.material.application.query.ListMaterialsByTypeQuery;
import com.mes.cloud.material.application.query.dto.MaterialView;
import com.mes.cloud.material.application.query.dto.StockAlertView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * [DDD Pattern: Application Service - 物料應用服務]
 * [SOLID: SRP - 負責協調 Command/Query Bus 派送，不包含業務邏輯]
 * [SOLID: DIP - 依賴 CommandBus/QueryBus 抽象介面]
 * [Spring Cloud Alibaba: Sentinel 流量控制整合]
 *
 * <h2>教學重點：Sentinel 流量控制與熔斷降級</h2>
 *
 * <h3>@SentinelResource 註解</h3>
 * <ul>
 *   <li>{@code value}: 資源名稱，Sentinel 以此識別被保護的資源</li>
 *   <li>{@code fallback}: 業務異常時的降級方法（如 EntityNotFoundException）</li>
 *   <li>{@code blockHandler}: Sentinel 觸發流控/熔斷時的處理方法（處理 BlockException）</li>
 * </ul>
 *
 * <h3>fallback vs blockHandler 的差異</h3>
 * <ul>
 *   <li><b>fallback</b>: 處理業務異常（RuntimeException 子類），提供優雅降級的回傳值</li>
 *   <li><b>blockHandler</b>: 處理 Sentinel 的 BlockException（流控、熔斷、系統保護等），
 *       方法必須多一個 BlockException 參數</li>
 * </ul>
 *
 * <h3>為什麼需要 Application Service 層？</h3>
 * <p>
 * 在六角形架構中，Application Service 是連接 Adapter（控制器）與 Domain 的橋樑。
 * 將 Sentinel 註解放在此層而非 Controller 層，可以：
 * </p>
 * <ol>
 *   <li>讓 Controller 專注於 HTTP 協議轉換</li>
 *   <li>讓 Application Service 專注於業務流程編排與跨切面關注點（流控、事務等）</li>
 *   <li>降級邏輯可以在多個入口（REST、RPC、MQ）間共用</li>
 * </ol>
 */
@Service
public class MaterialApplicationService {

    private static final Logger log = LoggerFactory.getLogger(MaterialApplicationService.class);

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public MaterialApplicationService(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    /**
     * 註冊新物料。
     *
     * @param command 註冊物料命令
     * @return 新建物料的 ID
     */
    @SentinelResource(value = "registerMaterial",
            blockHandler = "registerMaterialBlockHandler")
    public String registerMaterial(RegisterMaterialCommand command) {
        return commandBus.dispatch(command);
    }

    /**
     * 查詢單筆物料。
     * fallback 處理業務異常（如物料不存在）。
     *
     * @param materialId 物料 ID
     * @return 物料檢視
     */
    @SentinelResource(value = "getMaterial",
            fallback = "getMaterialFallback")
    public MaterialView getMaterial(String materialId) {
        GetMaterialQuery query = new GetMaterialQuery(materialId);
        return queryBus.dispatch(query);
    }

    /**
     * 物料入庫。
     *
     * @param command 入庫命令
     */
    @SentinelResource(value = "receiveMaterial",
            blockHandler = "receiveMaterialBlockHandler")
    public void receiveMaterial(ReceiveMaterialCommand command) {
        commandBus.dispatch(command);
    }

    /**
     * 物料消耗。
     * blockHandler 處理 Sentinel 流控（如併發過高時拒絕請求）。
     *
     * @param command 消耗命令
     */
    @SentinelResource(value = "consumeMaterial",
            blockHandler = "consumeMaterialBlockHandler")
    public void consumeMaterial(ConsumeMaterialCommand command) {
        commandBus.dispatch(command);
    }

    /**
     * 依類型查詢物料列表。
     *
     * @param materialType 物料類型
     * @return 物料檢視列表
     */
    public List<MaterialView> listMaterialsByType(String materialType) {
        ListMaterialsByTypeQuery query = new ListMaterialsByTypeQuery(materialType);
        return queryBus.dispatch(query);
    }

    /**
     * 查詢低庫存物料。
     *
     * @return 低庫存預警檢視列表
     */
    public List<StockAlertView> getLowStockMaterials() {
        GetLowStockMaterialsQuery query = new GetLowStockMaterialsQuery();
        return queryBus.dispatch(query);
    }

    // ========== Fallback Methods (業務異常降級) ==========

    /**
     * getMaterial 的 fallback 方法。
     * 當發生業務異常時（如物料不存在），回傳空的 MaterialView。
     *
     * <p>教學重點：fallback 方法的簽名必須與原始方法一致（可額外加 Throwable 參數）</p>
     *
     * @param materialId 物料 ID
     * @param ex         異常
     * @return 空的 MaterialView
     */
    public MaterialView getMaterialFallback(String materialId, Throwable ex) {
        log.warn("getMaterial 降級: materialId={}, reason={}", materialId, ex.getMessage());
        MaterialView fallbackView = new MaterialView();
        fallbackView.setId(materialId);
        fallbackView.setName("資料暫時無法取得");
        return fallbackView;
    }

    // ========== Block Handler Methods (Sentinel 流控處理) ==========

    /**
     * registerMaterial 的 blockHandler 方法。
     * 當 Sentinel 觸發流控或熔斷時呼叫。
     *
     * <p>教學重點：blockHandler 方法必須額外接收一個 BlockException 參數</p>
     *
     * @param command   原始命令
     * @param exception Sentinel BlockException
     * @return 降級結果
     */
    public String registerMaterialBlockHandler(RegisterMaterialCommand command,
                                                BlockException exception) {
        log.warn("registerMaterial 被流控: rule={}", exception.getRule());
        throw new RuntimeException("系統繁忙，請稍後重試註冊物料");
    }

    /**
     * receiveMaterial 的 blockHandler 方法。
     *
     * @param command   原始命令
     * @param exception Sentinel BlockException
     */
    public void receiveMaterialBlockHandler(ReceiveMaterialCommand command,
                                             BlockException exception) {
        log.warn("receiveMaterial 被流控: materialId={}, rule={}",
                command.getMaterialId(), exception.getRule());
        throw new RuntimeException("系統繁忙，請稍後重試入庫操作");
    }

    /**
     * consumeMaterial 的 blockHandler 方法。
     * 當併發消耗請求過多時，Sentinel 會觸發此方法。
     *
     * @param command   原始命令
     * @param exception Sentinel BlockException
     */
    public void consumeMaterialBlockHandler(ConsumeMaterialCommand command,
                                             BlockException exception) {
        log.warn("consumeMaterial 被流控: materialId={}, rule={}",
                command.getMaterialId(), exception.getRule());
        throw new RuntimeException("系統繁忙，請稍後重試消耗操作");
    }
}
