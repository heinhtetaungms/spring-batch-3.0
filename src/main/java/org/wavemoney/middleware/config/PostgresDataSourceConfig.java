package org.wavemoney.middleware.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.postgresql.xa.PGXADataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@MapperScan(
        basePackages = "org.wavemoney.middleware.api.custommapper.kyc",
        sqlSessionTemplateRef = "postgresSqlSessionTemplate"
)
public class PostgresDataSourceConfig {

    @Value("${psql-db-deboard.datasource.url}")
    private String dbUrl;

    @Value("${psql-db-deboard.datasource.username}")
    private String username;

    @Value("${psql-db-deboard.datasource.password}")
    private String password;

    @Value("${psql-db-deboard.datasource.maxPoolSize}")
    private int maxPoolSize;

    @Bean(name = {"postgresDataSource", "dataSource"})
    @Primary
    public DataSource postgresDataSource() throws SQLException {
        // Configure the PostgreSQL XA DataSource
        PGXADataSource pgXaDataSource = new PGXADataSource();
        pgXaDataSource.setUrl(dbUrl);
        pgXaDataSource.setUser(username);
        pgXaDataSource.setPassword(password);
        //pgXaDataSource.setPrepareThreshold(0);  // Disable server-side prepare

        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSource(pgXaDataSource);
        xaDataSource.setUniqueResourceName("postgresDB");
        xaDataSource.setMaxPoolSize(maxPoolSize);
        xaDataSource.setMinPoolSize(5);
        xaDataSource.setBorrowConnectionTimeout(120);
        xaDataSource.setMaxIdleTime(120);
        xaDataSource.setTestQuery("SELECT 1");
        xaDataSource.setLocalTransactionMode(true);

        return xaDataSource;
    }

    @Bean("postgresSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("postgresDataSource")DataSource postgresDataSource) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(postgresDataSource);

        // MyBatis settings
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setJdbcTypeForNull(org.apache.ibatis.type.JdbcType.NULL);  // Handle NULL values in MyBatis
        configuration.setMapUnderscoreToCamelCase(true);  // Convert DB column names to camelCase automatically
        configuration.setDefaultStatementTimeout(120);  // Set default statement timeout to 30 seconds
        configuration.setCacheEnabled(true);  // Enable caching for better performance

        sqlSessionFactoryBean.setConfiguration(configuration);
        return sqlSessionFactoryBean.getObject();
    }

    @Bean("postgresSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("postgresSqlSessionFactory")SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}
