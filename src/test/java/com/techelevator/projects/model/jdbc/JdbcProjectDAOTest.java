package com.techelevator.projects.model.jdbc;

import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;
import org.junit.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class JdbcProjectDAOTest {

    private ProjectDAO projectDAO;
    private static SingleConnectionDataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    private long employeeId;

    @BeforeClass
    public static void createDataSource(){

        dataSource = new SingleConnectionDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/projects");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres1");
        dataSource.setAutoCommit(false);

    }

    @AfterClass
    public static void destroyDataSource(){
        dataSource.destroy();
    }

    @After
    public void rollbackTest() throws SQLException{
        dataSource.getConnection().rollback();
    }

    @Before
    public void setup(){
        projectDAO = new JDBCProjectDAO(dataSource);
        jdbcTemplate = new JdbcTemplate(dataSource);

        String sql = "INSERT INTO employee (employee_id, department_id, first_name, last_name, birth_date, hire_date) VALUES (DEFAULT, null, 'test', 'test', '2021-05-10', '2021-05-10') RETURNING employee_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql);
        row.next();
        employeeId = row.getLong("employee_id");

    }

    @Test
    public void get_all_active_projects(){
        //Arrange
        List<Project> currentProjects = projectDAO.getAllActiveProjects();

        LocalDate yesterday = LocalDate.now().minusDays(10);
        // insert 1 active project
        insertProject("activeTest", yesterday, null);
        // Insert 1 non-active project
        insertProject("inactiveTest", null, null);

        //Test
        List<Project> actualProjects = projectDAO.getAllActiveProjects();

        //Assert
        Assert.assertNotNull(actualProjects);
        Assert.assertEquals(currentProjects.size() + 1, actualProjects.size());

    }

    @Test
    public void add_employee_to_project(){
        // Arrange
        long projectId = insertProject("testProject", null, null);

        // Test
        projectDAO.addEmployeeToProject(projectId, employeeId);

        // Assert
        Assert.assertTrue(employeeAddedToProject(projectId));

    }

    @Test
    public void remove_employee_from_project(){
        // Arrange
        long projectId = insertProject("testProject", null, null);
        projectDAO.addEmployeeToProject(projectId, employeeId);
        Assert.assertTrue(employeeAddedToProject(projectId));

        // Test
        projectDAO.removeEmployeeFromProject(projectId, employeeId);

        // Assert
        Assert.assertFalse(employeeAddedToProject(projectId));

    }

    private boolean employeeAddedToProject(long projectId){
        String sql = "SELECT * FROM project_employee WHERE project_id = ? AND employee_id = ?";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, projectId, employeeId);
        return row.next();
    }

    private long insertProject(String projectName, LocalDate toDate, LocalDate fromDate){
        String sql = "INSERT INTO project (project_id, name, from_date, to_date) VALUES (DEFAULT, 'test', ?, ?) RETURNING project_id";
        SqlRowSet row = jdbcTemplate.queryForRowSet(sql, projectName, toDate, fromDate);
        row.next();
        return row.getLong("project_id");
    }

}
