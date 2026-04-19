package com.example.library.config;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * Database configuration class that handles Flyway setup and migration strategy.

 * Best practices implemented:
 * - Custom migration strategy for better control
 * - Proper logging for monitoring
 * - Environment-specific configurations
 * - Error handling and validation
 */
@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Autowired
    private Environment environment;

    /**
     * Custom Flyway migration strategy that provides better logging and error handling.
     * This strategy ensures migrations are applied safely and provides detailed feedback.
     */
    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return new FlywayMigrationStrategy() {
            @Override
            public void migrate(Flyway flyway) {
                logger.info("Starting Flyway database migration...");
                
                try {
                    // Get current migration info
                    var info = flyway.info();
                    var current = info.current();
                    var pending = info.pending();
                    
                    if (current != null) {
                        logger.info("Current database version: {}", current.getVersion());
                    } else {
                        logger.info("Database is empty, will apply all migrations");
                    }
                    
                    if (pending.length > 0) {
                        logger.info("Found {} pending migration(s)", pending.length);
                        for (var migration : pending) {
                            logger.info("Pending migration: {} - {}", 
                                migration.getVersion(), migration.getDescription());
                        }
                    } else {
                        logger.info("No pending migrations found");
                    }
                    
                    // Perform the migration
                    var result = flyway.migrate();
                    
                    if (result.migrationsExecuted > 0) {
                        logger.info("Successfully applied {} migration(s)", result.migrationsExecuted);
                        logger.info("Database is now at version: {}", result.targetSchemaVersion);
                    } else {
                        logger.info("Database is already up to date");
                    }
                    
                } catch (Exception e) {
                    logger.error("Flyway migration failed: {}", e.getMessage(), e);
                    throw new RuntimeException("Database migration failed", e);
                }
            }
        };
    }

    /**
     * Bean to validate database configuration on startup.
     * This helps catch configuration issues early in the application lifecycle.
     */
    @Bean
    public DatabaseValidator databaseValidator(DataSource dataSource) {
        return new DatabaseValidator(dataSource);
    }

    /**
     * Inner class to validate database connectivity and configuration.
     */
    public static class DatabaseValidator {
        private static final Logger logger = LoggerFactory.getLogger(DatabaseValidator.class);
        private final DataSource dataSource;

        public DatabaseValidator(DataSource dataSource) {
            this.dataSource = dataSource;
            validateConnection();
        }

        private void validateConnection() {
            try {
                var connection = dataSource.getConnection();
                var metadata = connection.getMetaData();
                
                logger.info("Database connection validated successfully");
                logger.info("Database: {} {}", 
                    metadata.getDatabaseProductName(), 
                    metadata.getDatabaseProductVersion());
                logger.info("JDBC Driver: {} {}", 
                    metadata.getDriverName(), 
                    metadata.getDriverVersion());
                
                connection.close();
            } catch (Exception e) {
                logger.error("Database connection validation failed: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to validate database connection", e);
            }
        }
    }
}