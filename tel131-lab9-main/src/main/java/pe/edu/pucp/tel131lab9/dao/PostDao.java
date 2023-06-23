package pe.edu.pucp.tel131lab9.dao;

import pe.edu.pucp.tel131lab9.bean.Employee;
import pe.edu.pucp.tel131lab9.bean.Post;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostDao extends DaoBase{

    public ArrayList<Post> listPosts() {

        ArrayList<Post> posts = new ArrayList<>();

        String sql = "SELECT p.post_id, p.title, concat (e.first_name,' ', e.last_name) as \"autor\", p.datetime as \"fecha\", p.content, \n" +
                "p.employee_id, count(c.post_id) as \"cantidad de comentarios\" FROM comments c right join post p on c.post_id = p.post_id \n" +
                "left join employees e on e.employee_id = p.employee_id group by p.post_id;";

        try (Connection conn = this.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Post post = new Post();
                fetchPostData(post, rs);
                posts.add(post);
            }
        } catch (SQLException ex) {
            Logger.getLogger(PostDao.class.getName()).log(Level.SEVERE, null, ex);
        }
        return posts;
    }

    public Post getPost(int id) {

        Post post = null;

        String sql = "SELECT * FROM post p left join employees e on p.employee_id = e.employee_id "+
                "where p.post_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setInt(1, id);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    post = new Post();
                    fetchPostData(post, rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return post;
    }

    public Post savePost(Post post) {

        return post;
    }

    private void fetchPostData(Post post, ResultSet rs) throws SQLException {
        post.setPostId(rs.getInt(1));
        post.setTitle(rs.getString(2));

        post.setContent(rs.getString(3));
        post.setEmployeeId(rs.getInt(4));
        post.setDatetime(rs.getTimestamp("p.datetime"));


        Employee employee = new Employee();
        employee.setEmployeeId(rs.getInt("e.employee_id"));
        employee.setFirstName(rs.getString("e.first_name"));
        employee.setLastName(rs.getString("e.last_name"));
        post.setEmployee(employee);
    }


    public ArrayList<Post> buscarPost(String texto) {

        ArrayList<Post> listaPost = new ArrayList<>();

        String sql = "select * from post p left join employees e ON p.employee_id = e.employee_id \n" +
                "where lower(p.title) like ?  or lower(p.content) like ?  or lower(e.first_name) like ? or lower(e.last_name) like ?";

        try (Connection conn = this.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%"+texto+"%");
            pstmt.setString(2, "%"+texto+"%");
            pstmt.setString(3, "%"+texto+"%");
            pstmt.setString(4, "%"+texto+"%");

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    Post post = new Post();
                    fetchPostData(post, rs);

                    listaPost.add(post);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return listaPost;
    }

}
