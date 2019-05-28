package top.showtan.commodity_spike_system.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * @Author: sanli
 * @Date: 2019/4/9 13:50
 */
@Configuration
public class DruidConfig {
    @Autowired
    DruidProperties druidProperties;

    @Bean
    @Primary
    public DataSource druidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setUrl(druidProperties.getUrl());
        dataSource.setUsername(druidProperties.getUsername());
        dataSource.setDriverClassName(druidProperties.getDriverClassName());
        dataSource.setPassword(druidProperties.getPassword());

        dataSource.setInitialSize(druidProperties.getInitialSize());
        dataSource.setMinIdle(druidProperties.getMinIdle());
        dataSource.setMaxActive(druidProperties.getMaxActive());
        dataSource.setMaxWait(druidProperties.getMaxWait());
        dataSource.setTimeBetweenConnectErrorMillis(druidProperties.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(druidProperties.getMinEvictableIdleTimeMillis());
        dataSource.setValidationQuery(druidProperties.getValidationQuery());
        dataSource.setTestWhileIdle(druidProperties.isTestWhileIdle());
        dataSource.setTestOnBorrow(druidProperties.isTestOnBorrow());
        dataSource.setTestOnReturn(druidProperties.isTestOnReturn());
        dataSource.setPoolPreparedStatements(druidProperties.isPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(druidProperties.getMaxPoolPreparedStatementPerConnectionSize());
        try {
            dataSource.setFilters(druidProperties.getFilters());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dataSource.setConnectionProperties(druidProperties.getConnectionProperties());
        return dataSource;
    }
}
