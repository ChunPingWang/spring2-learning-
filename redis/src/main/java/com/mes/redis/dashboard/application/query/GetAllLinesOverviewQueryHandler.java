package com.mes.redis.dashboard.application.query;

import com.mes.common.cqrs.QueryHandler;
import com.mes.redis.dashboard.application.assembler.DashboardAssembler;
import com.mes.redis.dashboard.application.query.dto.LineOverviewView;
import com.mes.redis.dashboard.domain.model.DashboardMetrics;
import com.mes.redis.dashboard.domain.repository.DashboardMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * [CQRS Pattern: Query Handler - 處理查詢所有產線概覽]
 * [SOLID: SRP - 只負責查詢所有產線概覽的流程]
 *
 * 從 Repository 載入所有看板指標，彙總為產線概覽列表。
 */
@Component
public class GetAllLinesOverviewQueryHandler
        implements QueryHandler<GetAllLinesOverviewQuery, List<LineOverviewView>> {

    private static final Logger log = LoggerFactory.getLogger(GetAllLinesOverviewQueryHandler.class);

    private final DashboardMetricsRepository repository;

    public GetAllLinesOverviewQueryHandler(DashboardMetricsRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<LineOverviewView> handle(GetAllLinesOverviewQuery query) {
        log.debug("Handling GetAllLinesOverviewQuery");
        List<DashboardMetrics> allMetrics = repository.findAll();
        return DashboardAssembler.toOverviewList(allMetrics);
    }

    @Override
    public Class<GetAllLinesOverviewQuery> getQueryType() {
        return GetAllLinesOverviewQuery.class;
    }
}
