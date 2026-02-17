package com.mes.batch;

import com.mes.batch.domain.model.DailyProductionReport;
import com.mes.batch.domain.repository.DailyReportRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@ContextConfiguration(classes = {MesBatchApplication.class, 
                                 com.mes.batch.infrastructure.batch.DailyReportBatchConfig.class})
@DisplayName("Spring Batch 測試")
class DailyReportBatchTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private DailyReportRepository reportRepository;

    @Test
    @DisplayName("應該成功執行日報告生成 Job")
    void dailyReportJob_shouldCompleteSuccessfully() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("reportDate", LocalDate.now().minusDays(1).toString())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }

    @Test
    @DisplayName("Step 應該正確執行")
    void dailyReportStep_shouldCompleteSuccessfully() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("reportDate", LocalDate.now().toString())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchStep("dailyReportGenerationStep", jobParameters);

        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}
