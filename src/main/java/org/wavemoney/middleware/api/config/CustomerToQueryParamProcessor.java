package org.wavemoney.middleware.api.config;

import org.springframework.batch.item.ItemProcessor;
import org.wavemoney.middleware.api.custommapper.dto.CustomerQueryParam;
import org.wavemoney.middleware.api.entity.Customer;

public class CustomerToQueryParamProcessor implements ItemProcessor<Customer, CustomerQueryParam> {

    @Override
    public CustomerQueryParam process(Customer customer) {
        return new CustomerQueryParam(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getGender(),
                customer.getContactNo(),
                customer.getCountry(),
                customer.getDob()
        );
    }
}

