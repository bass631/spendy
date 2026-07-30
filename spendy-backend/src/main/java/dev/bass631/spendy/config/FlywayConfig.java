package dev.bass631.spendy.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class FlywayConfig {

    private final DataSource dataSource;

    @PostConstruct
    public void migrateFlyway() {
        Flyway.configure()
                .dataSource(dataSource)
                .schemas("spendy")
                .defaultSchema("spendy")
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load()
                .migrate();
    }
}
