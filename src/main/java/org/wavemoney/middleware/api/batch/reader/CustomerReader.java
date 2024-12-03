package org.wavemoney.middleware.api.batch.reader;

import jakarta.annotation.PostConstruct;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.core.io.ClassPathResource;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.stereotype.Component;
import org.wavemoney.middleware.api.dto.CustomerRecord;

@Component
@StepScope
public class CustomerReader implements ItemStreamReader<CustomerRecord> {

    private FlatFileItemReader<CustomerRecord> reader;

    @PostConstruct
    public void init() {
        reader = new FlatFileItemReader<>();
        reader.setResource(new ClassPathResource("customers.csv"));
        reader.setLinesToSkip(1);
        reader.setEncoding("UTF-8");
        
        DefaultLineMapper<CustomerRecord> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("msisdn", "balance", "category", "status");
        
        BeanWrapperFieldSetMapper<CustomerRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(CustomerRecord.class);
        
        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        
        reader.setLineMapper(lineMapper);
        try {
            reader.afterPropertiesSet();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize reader", e);
        }
    }

    @Override
    public CustomerRecord read() throws Exception {
        return reader.read();
    }

    @Override
    public void open(ExecutionContext executionContext) {
        reader.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) {
        reader.update(executionContext);
    }

    @Override
    public void close() {
        reader.close();
    }
}

