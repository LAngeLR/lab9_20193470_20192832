package pe.edu.pucp.tel131lab9.dao;

import pe.edu.pucp.tel131lab9.bean.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommentDao extends DaoBase{
    public ArrayList<Comment> listComments() {

        ArrayList<Comment> comments = new ArrayList<>();

        String sql = "SELECT * FROM comments c";

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Comment comment = new Comment();
                fetchCommentData(comment, rs);
                comments.add(comment);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PostDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return comments;
    }

    public ArrayList<Comment> listCommentsByPostId(int postId) {

        ArrayList<Comment> comments = new ArrayList<>();

        String sql = "SELECT * FROM comments c where post_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

             stmt.setInt(1, postId);

             try (ResultSet rs = stmt.executeQuery()) {
                 while (rs.next()) {
                     Comment comment = new Comment();
                     fetchCommentData(comment, rs);
                     comments.add(comment);
                 }
             }
        } catch (SQLException ex) {
            Logger.getLogger(PostDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return comments;
    }

    private void saveComment(Comment comment) throws SQLException {
        String sql = "INSERT INTO comments (employee_id, post_id, comment, datetime) "
                + "VALUES (?, ?, ?, now())";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            setCommentParams(statement, comment);
            statement.executeUpdate();
        }
    }

    private void setCommentParams(PreparedStatement statement, Comment comment) throws SQLException {
        statement.setInt(1, comment.getEmployeeId());
        statement.setInt(2, comment.getPostId());
        statement.setString(3, comment.getComment());
    }

    private void fetchCommentData(Comment comment, ResultSet rs) throws SQLException {
        comment.setCommentId(rs.getInt("c.comment_id"));
        comment.setEmployeeId(rs.getInt("c.employee_id"));
        comment.setPostId(rs.getInt("c.post_id"));
        comment.setComment(rs.getString("c.comment"));
        comment.setDatetime(rs.getDate("c.datetime"));
    }
}
