package pe.edu.pucp.tel131lab9.dao;

import pe.edu.pucp.tel131lab9.bean.Job;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobDao extends DaoBase {

    public ArrayList<Job> listJobs() {

        ArrayList<Job> jobs = new ArrayList<>();

        String sql = "SELECT * FROM jobs";

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Job job = new Job();
                job.setJobId(rs.getString(1));
                job.setJobTitle(rs.getString(2));
                job.setMinSalary(rs.getInt(3));
                job.setMaxSalary(rs.getInt(4));
                jobs.add(job);
            }
        } catch (SQLException ex) {
            Logger.getLogger(JobDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return jobs;
    }

    public Job getJob(String jobId) {

        Job job = null;

        String sql = "SELECT * FROM jobs WHERE job_id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, jobId);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    job = new Job();
                    job.setJobId(rs.getString(1));
                    job.setJobTitle(rs.getString(2));
                    job.setMinSalary(rs.getInt(3));
                    job.setMaxSalary(rs.getInt(4));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(JobDao.class.getName()).log(Level.SEVERE, null, ex);
        }

        return job;
    }

    public void createJob(String jobId, String jobTitle, int minSalary, int maxSalary) throws SQLException {

        String sql = "INSERT INTO jobs (job_id,job_title,min_salary,max_salary) "
                + "VALUES (?,?,?,?)";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, jobId);
            statement.setString(2, jobTitle);
            statement.setInt(3, minSalary);
            statement.setInt(4, maxSalary);
            statement.executeUpdate();
        }
    }

    public void updateJob(String jobId, String jobTitle, int minSalary, int maxSalary) throws SQLException {

        String sql = "UPDATE jobs SET job_title = ?, min_salary = ?, max_salary = ? "
                + "WHERE job_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, jobTitle);
            statement.setInt(2, minSalary);
            statement.setInt(3, maxSalary);
            statement.setString(4, jobId);
            statement.executeUpdate();
        }
    }

    public void deleteJob(String jobId) throws SQLException {

        String sql = "DELETE FROM jobs WHERE job_id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, jobId);
            statement.executeUpdate();
        }
    }
}