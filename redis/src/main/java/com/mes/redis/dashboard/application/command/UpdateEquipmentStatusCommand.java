package com.mes.redis.dashboard.application.command;

import com.mes.common.cqrs.Command;

/**
 * [CQRS Pattern: Command - 更新設備狀態]
 * [SOLID: SRP - 只負責攜帶更新設備狀態所需的資料]
 *
 * 用於更新某條產線下某台設備的狀態快照。
 */
public class UpdateEquipmentStatusCommand implements Command {

    private final String lineId;
    private final String equipmentId;
    private final String equipmentName;
    private final String status;

    public UpdateEquipmentStatusCommand(String lineId, String equipmentId,
                                        String equipmentName, String status) {
        this.lineId = lineId;
        this.equipmentId = equipmentId;
        this.equipmentName = equipmentName;
        this.status = status;
    }

    public String getLineId() {
        return lineId;
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getStatus() {
        return status;
    }
}
