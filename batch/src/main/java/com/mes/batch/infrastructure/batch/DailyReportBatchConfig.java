package com.mes.batch.infrastructure.batch;

import com.mes.batch.domain.model.DailyProductionReport;
import com.mes.batch.domain.model.ProductionRecord;
import com.mes.batch.domain.repository.DailyReportRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DailyReportBatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DailyReportRepository reportRepository;

    public DailyReportBatchConfig(JobBuilderFactory jobBuilderFactory,
                                   StepBuilderFactory stepBuilderFactory,
                                   DailyReportRepository reportRepository) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
        this.reportRepository = reportRepository;
    }

    @Bean
    @StepScope
    public ItemReader<ProductionRecord> dailyReportReader(
            @Value("#{jobParameters['reportDate']}") String reportDateStr) {
        LocalDate reportDate = reportDateStr != null 
                ? LocalDate.parse(reportDateStr) 
                : LocalDate.now().minusDays(1);
        
        List<ProductionRecord> mockData = Arrays.asList(
                new ProductionRecord("WO-001", "LINE-A", reportDate, 100, 5, "COMPLETED"),
                new ProductionRecord("WO-002", "LINE-A", reportDate, 150, 8, "COMPLETED"),
                new ProductionRecord("WO-003", "LINE-B", reportDate, 200, 12, "COMPLETED"),
                new ProductionRecord("WO-004", "LINE-B", reportDate, 80, 3, "COMPLETED")
        );
        
        return new ListItemReader<>(mockData);
    }

    @Bean
    public ItemProcessor<ProductionRecord, DailyProductionReport> dailyReportProcessor() {
        return record -> {
            DailyProductionReport report = new DailyProductionReport(
                    record.getProductionDate(),
                    record.getLineId()
            );
            report.updateMetrics(1, 1, record.getOutputQuantity(), record.getDefectiveQuantity());
            report.setGeneratedAt(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            return report;
        };
    }

    @Bean
    public ItemWriter<DailyProductionReport> dailyReportWriter() {
        return reports -> {
            for (DailyProductionReport report : reports) {
                java.util.Optional<DailyProductionReport> existing = reportRepository.findByReportDateAndLineId(
                        report.getReportDate(), report.getLineId());
                if (existing.isPresent()) {
                    DailyProductionReport existingReport = existing.get();
                    existingReport.updateMetrics(
                            existingReport.getTotalOrders() + report.getTotalOrders(),
                            existingReport.getCompletedOrders() + report.getCompletedOrders(),
                            existingReport.getTotalOutput() + report.getTotalOutput(),
                            existingReport.getDefectiveOutput() + report.getDefectiveOutput()
                    );
                    reportRepository.save(existingReport);
                } else {
                    reportRepository.save(report);
                }
            }
            System.out.println("Wrote " + reports.size() + " daily reports");
        };
    }

    @Bean
    public Step dailyReportGenerationStep() {
        return stepBuilderFactory.get("dailyReportGenerationStep")
                .<ProductionRecord, DailyProductionReport>chunk(10)
                .reader(dailyReportReader(null))
                .processor(dailyReportProcessor())
                .writer(dailyReportWriter())
                .build();
    }

    @Bean
    public Job dailyReportGenerationJob() {
        return jobBuilderFactory.get("dailyReportGenerationJob")
                .start(dailyReportGenerationStep())
                .build();
    }

    public static class ListItemReader<T> implements ItemReader<T> {
        private final List<T> items;
        private int currentIndex = 0;

        public ListItemReader(List<T> items) {
            this.items = items;
        }

        @Override
        public T read() {
            if (currentIndex >= items.size()) {
                return null;
            }
            return items.get(currentIndex++);
        }
    }
}
