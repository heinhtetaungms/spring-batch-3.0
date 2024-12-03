package org.wavemoney.middleware.api.batch.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import org.wavemoney.middleware.api.batch.processor.CustomerProcessor;
import org.wavemoney.middleware.api.batch.reader.CustomerReader;
import org.wavemoney.middleware.api.batch.writer.CustomerWriter;
import org.wavemoney.middleware.api.dto.CustomerRecord;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;


@Configuration
@EnableBatchProcessing
public class BatchConfig {
    @Bean
    public JobRepository jobRepository(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean factory = getJobRepositoryFactoryBean(dataSource, transactionManager);

        // Check if spring batch tables exist before creating them
        if (!tablesExist(dataSource)) {
            ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
            databasePopulator.addScript(new ClassPathResource("org/springframework/batch/core/schema-postgresql.sql"));
            databasePopulator.setContinueOnError(true);
            databasePopulator.execute(dataSource);
        }

        return factory.getObject();
    }

    private JobRepositoryFactoryBean getJobRepositoryFactoryBean(DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setIsolationLevelForCreate("ISOLATION_DEFAULT");
        factory.setTablePrefix("BATCH_");  // Add table prefix
        factory.setDatabaseType("POSTGRES"); // Specify database type
        factory.setMaxVarCharLength(1000); // Set max varchar length
        factory.setIncrementerFactory(new DefaultDataFieldMaxValueIncrementerFactory(dataSource)); // Add incrementer factory
        factory.afterPropertiesSet();
        return factory;
    }

    private boolean tablesExist(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "batch_job_instance", null);
            return tables.next();
        } catch (SQLException e) {
            return false;
        }
    }

    @Bean
    public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
        TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();
        return jobLauncher;
    }

    @Bean
    public Job customerJob(JobRepository jobRepository, Step customerStep) {
        return new JobBuilder("customerJob", jobRepository)
                .start(customerStep)
                .build();
    }

    @Bean
    public Step customerStep(JobRepository jobRepository,
                             CustomerReader customerReader,
                             CustomerProcessor customerProcessor,
                             CustomerWriter customerWriter,
                             PlatformTransactionManager transactionManager) {
        return new StepBuilder("customerStep", jobRepository)
                .<CustomerRecord, CustomerRecord>chunk(5) // Smaller chunk size for better transaction management
                .reader(customerReader)
                .processor(customerProcessor)
                .writer(customerWriter)
                .transactionManager(transactionManager)
                .build();
    }
}

