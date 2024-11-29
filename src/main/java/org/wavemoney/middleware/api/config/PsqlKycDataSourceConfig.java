package org.wavemoney.middleware.api.config;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = "org.wavemoney.middleware.api.custommapper.kyc", sqlSessionTemplateRef  = "KycSessionTemplate")
public class PsqlKycDataSourceConfig {
	
	@Bean(name = "KycDataSourceProperties")
	@ConfigurationProperties(prefix = "psql-kyc.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }
	
	@Bean(name = "KycDataSource")
    @ConfigurationProperties("psql-kyc.datasource.hikari")
    public HikariDataSource dataSource(@Qualifier("KycDataSourceProperties") DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

	@Bean(name = "KycSessionFactory")
	public SqlSessionFactory KycSessionFactory(@Qualifier("KycDataSource") HikariDataSource hikariDataSource) throws Exception {
		SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
		bean.setDataSource(hikariDataSource);
		return bean.getObject();
	}

	@Bean(name = "KycTransactionManager")
	public DataSourceTransactionManager KycTransactionManager(@Qualifier("KycDataSource") DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	@Bean(name = "KycSessionTemplate")
	public SqlSessionTemplate KycSessionTemplate(@Qualifier("KycSessionFactory") SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
