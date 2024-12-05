package org.wavemoney.middleware.api.batch.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.wavemoney.middleware.api.custommapper.kyc.KycMapper;
import org.wavemoney.middleware.api.custommapper.mfs.MfsMapper;
import org.wavemoney.middleware.api.dto.CustomerRecord;
import org.wavemoney.middleware.api.dto.request.kyc.UpdateKycQueryParam;
import org.wavemoney.middleware.api.dto.request.mfs.UpdateMfsQueryParam;

@Component
@StepScope
@Slf4j
@RequiredArgsConstructor
public class CustomerWriter implements ItemWriter<CustomerRecord> {

    private final KycMapper kycMapper;
    private final MfsMapper mfsMapper;
    private final PlatformTransactionManager transactionManager;
    
    @Value("${psql-db-deboard.datasource.dbSchema}")
    private String psqlDbSchema;
    @Value("${oracle-db-deboard.datasource.dbSchema}")
    private String oracleDbSchema;

    @Override
    public void write(Chunk<? extends CustomerRecord> chunk) {
        log.info("Starting batch write for {} records", chunk.size());

        for (CustomerRecord record : chunk) {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

            try {
                template.execute(status -> {
                    try {
                        processRecord(record);
                        return true;
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        log.error("Transaction failed for MSISDN: {}", record.getMsisdn(), e);
                        return false;
                    }
                });
            } catch (Exception e) {
                log.error("Failed to process record: {}", record.getMsisdn(), e);
                // Don't rethrow, if fail will handle transactional for that dedicated one - allow other records to process
            }
        }
    }

    private void processRecord(CustomerRecord record) {
        UpdateKycQueryParam kycParam = UpdateKycQueryParam.builder()
                                        .msisdn(record.getMsisdn())
                                        .status(8)
                                        .dbSchema(psqlDbSchema)
                                        .build();

        int kycResult = kycMapper.updateKyc(kycParam);

        if (kycResult == 0) {
            throw new RuntimeException("Failed to update KYC for MSISDN: " + record.getMsisdn());
        }

        UpdateMfsQueryParam mfsParam = UpdateMfsQueryParam.builder()
                                        .msisdn(record.getMsisdn())
                                        .isDelete(1)
                                        .dbSchema(oracleDbSchema)
                                        .build();
        int mfsResult = mfsMapper.updateMfs(mfsParam);

        if (mfsResult == 0) {
            throw new RuntimeException("Failed to update MFS for MSISDN: " + record.getMsisdn());
        }

        // Simulate failure for rollback test
        if (record.getMsisdn().equals("9123456785")) {
            throw new RuntimeException("Simulated failure for rollback test");
        }

        log.info("Successfully updated KYC and MFS for MSISDN: {}", record.getMsisdn());
    }
}
