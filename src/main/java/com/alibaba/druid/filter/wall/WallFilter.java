package com.alibaba.druid.filter.wall;

import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.wall.spi.MySqlWallProvider;
import com.alibaba.druid.filter.wall.spi.OracleWallProvider;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.util.JdbcUtils;

public class WallFilter extends FilterAdapter {

    private boolean      inited = false;

    private WallProvider provider;

    private boolean      loadDefault;

    private boolean      loadExtend;

    @Override
    public void init(DataSourceProxy dataSource) {
        this.dataSource = dataSource;

        provider = createWallProvider(dataSource.getDbType());

        this.inited = true;
    }

    public boolean isLoadDefault() {
        return loadDefault;
    }
    
    public void checkInit() {
        if (inited) {
            throw new DruidRuntimeException("wall filter is inited");
        }
    }

    public void setLoadDefault(boolean loadDefault) {
        checkInit();
        this.loadDefault = loadDefault;
    }

    public boolean isLoadExtend() {
        return loadExtend;
    }

    public void setLoadExtend(boolean loadExtend) {
        checkInit();
        this.loadExtend = loadExtend;
    }

    @Override
    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        return chain.connection_connect(info);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                        throws SQLException {
        check(connection, sql);
        return chain.connection_prepareStatement(connection, sql);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int autoGeneratedKeys) throws SQLException {
        check(connection, sql);
        return chain.connection_prepareStatement(connection, sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency)
                                                                                                                      throws SQLException {
        check(connection, sql);
        return chain.connection_prepareStatement(connection, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency,
                                                              int resultSetHoldability) throws SQLException {
        check(connection, sql);
        return chain.connection_prepareStatement(connection, sql, resultSetType, resultSetConcurrency,
                                                 resultSetHoldability);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int[] columnIndexes) throws SQLException {
        check(connection, sql);
        return chain.connection_prepareStatement(connection, sql, columnIndexes);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, String[] columnNames) throws SQLException {
        check(connection, sql);
        return chain.connection_prepareStatement(connection, sql, columnNames);
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                   throws SQLException {
        check(connection, sql);
        return chain.connection_prepareCall(connection, sql);
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency)
                                                                                                     throws SQLException {
        check(connection, sql);
        return chain.connection_prepareCall(connection, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency,
                                                         int resultSetHoldability) throws SQLException {
        check(connection, sql);
        return chain.connection_prepareCall(connection, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    // //////////////

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        check(statement.getConnectionProxy(), sql);
        return chain.statement_execute(statement, sql);
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                    throws SQLException {
        check(statement.getConnectionProxy(), sql);
        return chain.statement_execute(statement, sql, autoGeneratedKeys);
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                  throws SQLException {
        check(statement.getConnectionProxy(), sql);
        return chain.statement_execute(statement, sql, columnIndexes);
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                   throws SQLException {
        check(statement.getConnectionProxy(), sql);
        return chain.statement_execute(statement, sql, columnNames);
    }

    @Override
    public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql)
                                                                                                         throws SQLException {
        check(statement.getConnectionProxy(), sql);
        return chain.statement_executeQuery(statement, sql);
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        check(statement.getConnectionProxy(), sql);
        return chain.statement_executeUpdate(statement, sql);
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                      throws SQLException {
        check(statement.getConnectionProxy(), sql);
        return chain.statement_executeUpdate(statement, sql, autoGeneratedKeys);
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                    throws SQLException {
        check(statement.getConnectionProxy(), sql);
        return chain.statement_executeUpdate(statement, sql, columnIndexes);
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                     throws SQLException {
        check(statement.getConnectionProxy(), sql);
        return chain.statement_executeUpdate(statement, sql, columnNames);
    }

    public void check(ConnectionProxy connection, String sql) throws SQLException {
        provider.check(sql, true);
    }

    public WallProvider createWallProvider(String dbType) {
        if (JdbcUtils.MYSQL.equals(dbType)) {
            return new MySqlWallProvider(this.loadDefault, this.loadExtend);
        }

        if (JdbcUtils.ORACLE.equals(dbType)) {
            return new OracleWallProvider(this.loadDefault, this.loadExtend);
        }

        throw new IllegalStateException("dbType not support : " + dbType);
    }

}
