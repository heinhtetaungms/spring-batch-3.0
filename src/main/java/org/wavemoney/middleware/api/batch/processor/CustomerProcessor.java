package org.wavemoney.middleware.api.batch.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wavemoney.middleware.api.custommapper.kyc.KycMapper;
import org.wavemoney.middleware.api.custommapper.mfs.MfsMapper;
import org.wavemoney.middleware.api.dto.CustomerRecord;
import org.wavemoney.middleware.api.dto.request.kyc.SearchKycQueryParam;
import org.wavemoney.middleware.api.dto.request.mfs.SearchMfsQueryParam;
import org.wavemoney.middleware.api.dto.response.kyc.Kyc;
import org.wavemoney.middleware.api.dto.response.mfs.Mfs;

@Component
@StepScope
@Slf4j
@RequiredArgsConstructor
public class CustomerProcessor implements ItemProcessor<CustomerRecord, CustomerRecord> {

    private final KycMapper kycMapper;
    private final MfsMapper mfsMapper;

    @Value("${psql-db-deboard.datasource.dbSchema}")
    private String psqlDbSchema;
    @Value("${oracle-db-deboard.datasource.dbSchema}")
    private String oracleDbSchema;

    @Override
    public CustomerRecord process(CustomerRecord record) {
        log.info("[Before Process] Record from csv => {}", record);
        // Validate KYC
        SearchKycQueryParam kycQueryParam = new SearchKycQueryParam(
                                            record.getMsisdn(),
                                            psqlDbSchema );
        Kyc kyc = kycMapper.getKyc(kycQueryParam);
        log.info("kyc: {}", kyc);
        if (kyc == null || kyc.getStatus() != 3) {
            log.warn("Kyc conditions not met for MSISDN: {}", record.getMsisdn());
            return null; // Skip this record
        }

        // Validate MFS
        SearchMfsQueryParam mfsQueryParam = new SearchMfsQueryParam(
                                            record.getMsisdn(),
                                            oracleDbSchema );
        Mfs mfs = mfsMapper.getMfs(mfsQueryParam);
        log.info("mfs: {}", mfs);
        if (mfs == null || mfs.getBalance() != 0) {
            log.warn("Mfs conditions not met for MSISDN: {}", record.getMsisdn());
            return null; // Skip this record
        }

        log.info("[After Process] Record processed successfully for MSISDN: {}", record.getMsisdn());
        return record;
    }
}

