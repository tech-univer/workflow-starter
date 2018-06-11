package mcb.demo.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {
    @Bean
    @Primary
    @ConfigurationProperties(prefix="datasource.primary")
    public DataSource provideDatasource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name="camundaBpmDataSource")
    @ConfigurationProperties(prefix="datasource.secondary")
    public DataSource provideCamundaDataSource() {
        return DataSourceBuilder.create().build();
    }
}
