package com.techelevator.projects.model.jdbc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import com.techelevator.projects.model.Project;
import com.techelevator.projects.model.ProjectDAO;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JDBCProjectDAO implements ProjectDAO {

	private JdbcTemplate jdbcTemplate;

	public JDBCProjectDAO(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Project> getAllActiveProjects() {
		String sql = "SELECT project_id, name, from_date, to_date FROM project WHERE from_date IS NOT NULL AND to_date IS NULL";
		SqlRowSet rows = jdbcTemplate.queryForRowSet(sql);
		List<Project> projects = new ArrayList<Project>();

		while (rows.next()){
			Project project = mapRowToProject(rows);
			projects.add(project);
		}

		return projects;
	}

	@Override
	public void removeEmployeeFromProject(Long projectId, Long employeeId) {
		String sql = "DELETE FROM project_employee WHERE project_id = ? AND employee_id = ?";
		jdbcTemplate.update(sql, projectId, employeeId);
	}

	@Override
	public void addEmployeeToProject(Long projectId, Long employeeId) {
		String sql = "INSERT INTO project_employee (project_id, employee_id) VALUES (?, ?)";
		jdbcTemplate.update(sql, projectId, employeeId);
	}

	private Project mapRowToProject(SqlRowSet results) {
		Project project;
		project = new Project();
		project.setId(results.getLong("project_id"));
		project.setName(results.getString("name"));

		if(results.getDate("from_date") != null){
			project.setStartDate(results.getDate("from_date").toLocalDate());
		} else project.setStartDate(results.getDate(0000-00-00).toLocalDate());
		if(results.getDate("to_date") != null){
			project.setEndDate(results.getDate("to_date").toLocalDate());
		}

		return project;
	}

}
