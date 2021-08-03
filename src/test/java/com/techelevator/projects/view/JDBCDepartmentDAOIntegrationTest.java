package com.techelevator.projects.view;

import com.techelevator.projects.model.DepartmentDAO;
import com.techelevator.projects.model.jdbc.JDBCDepartmentDAO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;

public class JDBCDepartmentDAOIntegrationTest {

    private static SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private DepartmentDAO departmentDAO;

    @BeforeClass
    public static void setupDataSource() {
        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);
    }

    @AfterClass
    public static void destroyDataSource() {
        dataSource.destroy();
    }

    @After
    public void rollback() throws SQLException {
        dataSource.getConnection().rollback();
    }

    @Before
    public void setup() {
        departmentDAO = new JDBCDepartmentDAO(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


}
