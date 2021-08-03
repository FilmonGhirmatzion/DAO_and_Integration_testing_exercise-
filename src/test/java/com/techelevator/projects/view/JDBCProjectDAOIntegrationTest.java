package com.techelevator.projects.view;

import com.techelevator.projects.model.EmployeeDAO;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;
import com.techelevator.projects.model.jdbc.JDBCProjectDAO;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCProjectDAOIntegrationTest {

    private static SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private ProjectDAO projectDAO;
    private long testProjectId;

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
        jdbcTemplate = new JdbcTemplate(dataSource);
        projectDAO = new JDBCProjectDAO(dataSource);
        String sql = "INSERT INTO project (project_id, name, from_date, to_date) VALUES(DEFAULT, 'Test', '2021-05-10', null) RETURNING project_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
        row.next();
        testProjectId = row.getLong("project_id");
    }



}
