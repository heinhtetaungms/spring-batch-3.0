package org.wavemoney.middleware.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import oracle.jdbc.xa.client.OracleXADataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@MapperScan(
        basePackages = "org.wavemoney.middleware.api.custommapper.mfs",
        sqlSessionTemplateRef = "oracleSqlSessionTemplate"
)
public class OracleDataSourceConfig {

    @Value("${oracle-db-deboard.datasource.url}")
    private String dbUrl;

    @Value("${oracle-db-deboard.datasource.username}")
    private String username;

    @Value("${oracle-db-deboard.datasource.password}")
    private String password;

    @Value("${oracle-db-deboard.datasource.maxPoolSize}")
    private int maxPoolSize;

    @Bean("oracleDataSource")
    public DataSource oracleDataSource() throws SQLException {
        // Configure the Oracle XA DataSource
        OracleXADataSource oracleXaDataSource = new OracleXADataSource();
        oracleXaDataSource.setURL(dbUrl);
        oracleXaDataSource.setUser(username);
        oracleXaDataSource.setPassword(password);

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(oracleXaDataSource);
        xaDataSource.setUniqueResourceName("oracleDB");
        xaDataSource.setMaxPoolSize(maxPoolSize);
        xaDataSource.setMinPoolSize(5);
        xaDataSource.setBorrowConnectionTimeout(120);
        xaDataSource.setMaxIdleTime(120);
        xaDataSource.setTestQuery("SELECT 1 FROM DUAL");
        xaDataSource.setLocalTransactionMode(true);

        return xaDataSource;
    }

    @Bean("oracleSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("oracleDataSource")DataSource oracleDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(oracleDataSource);

        // MyBatis settings
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setJdbcTypeForNull(org.apache.ibatis.type.JdbcType.NULL);  // Handle NULL values in MyBatis
        configuration.setMapUnderscoreToCamelCase(true);  // Convert DB column names to camelCase automatically
        configuration.setDefaultStatementTimeout(120);  // Set default statement timeout to 30 seconds
        configuration.setCacheEnabled(true);  // Enable caching for better performance

        sqlSessionFactoryBean.setConfiguration(configuration);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean("oracleSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("oracleSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
