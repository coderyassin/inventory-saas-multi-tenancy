package org.yascode.inventory_saas.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Pattern;

import static org.hibernate.cfg.MultiTenancySettings.MULTI_TENANT_CONNECTION_PROVIDER;

@Component
@RequiredArgsConstructor
@Slf4j
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String>, HibernatePropertiesCustomizer {

    private static final Pattern VALID_SCHEMA_NAME = Pattern.compile("^tenant_[a-z0-9_]{1,56}$");

    private final DataSource dataSource;

    @Value("${app.multitenancy.default-schema:public}")
    private String defaultSchema;

    @Override
    public Connection getAnyConnection() throws SQLException {
        Connection connection = this.dataSource.getConnection();

        try {
            connection.setSchema(validateSchemaName(defaultSchema));
            return connection;
        } catch (SQLException | RuntimeException exception) {
            closeAfterFailure(connection, exception);
            throw exception;
        }
    }

    @Override
    public void releaseAnyConnection(final Connection connection) throws SQLException {
        releaseToPool(connection);
    }

    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {

        if (tenantIdentifier == null || tenantIdentifier.isBlank()) {
            throw new SQLException("Tenant identifier must not be null or blank");
        }

        String schema = validateSchemaName(tenantIdentifier);

        Connection connection = dataSource.getConnection();

        try {
            connection.setSchema(schema);
            return connection;
        } catch (SQLException | RuntimeException exception) {
            closeAfterFailure(connection, exception);
            throw exception;
        }
    }

    @Override
    public void releaseConnection(String tenantIdentifier, Connection connection) throws SQLException {
        releaseToPool(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return unwrapType != null && unwrapType.isInstance(this);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {

        if (isUnwrappableAs(unwrapType)) {
            return unwrapType.cast(this);
        }

        throw new IllegalArgumentException(
                "Cannot unwrap MultiTenantConnectionProvider to "
                        + unwrapType.getName()
        );
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(
                MULTI_TENANT_CONNECTION_PROVIDER,
                this
        );
    }

    /**
     * Always restore the connection to the control/default schema
     * before returning it to the connection pool.
     */
    private void releaseToPool(Connection connection) throws SQLException {
        if (connection == null) {
            return;
        }

        SQLException resetException = null;

        try {
            connection.setSchema(validateSchemaName(defaultSchema));
        } catch (SQLException exception) {
            resetException = exception;
        }

        try {
            connection.close();
        } catch (SQLException closeException) {
            if (resetException != null) {
                resetException.addSuppressed(closeException);
            } else {
                resetException = closeException;
            }
        }

        if (resetException != null) {
            throw resetException;
        }
    }

    private String validateSchemaName(String schemaName)
            throws SQLException {

        if (schemaName == null ||
                (!schemaName.equals("public") && !VALID_SCHEMA_NAME.matcher(schemaName).matches())
        ) {
            throw new SQLException("Invalid tenant schema identifier");
        }

        return schemaName;
    }

    private void closeAfterFailure(Connection connection, Exception originalException) {

        try {
            connection.close();
        } catch (SQLException closeException) {
            originalException.addSuppressed(closeException);
        }
    }
}
