package com.mes.batch.domain.repository;

import com.mes.batch.domain.model.DailyProductionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyReportRepository extends JpaRepository<DailyProductionReport, Long> {
    Optional<DailyProductionReport> findByReportDateAndLineId(LocalDate reportDate, String lineId);
    List<DailyProductionReport> findByReportDate(LocalDate reportDate);
}
