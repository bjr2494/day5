package edu.acc.j2ee.hubbub.domain.post;

import edu.acc.j2ee.hubbub.DaoException;
import edu.acc.j2ee.hubbub.domain.user.User;
import edu.acc.j2ee.hubbub.domain.user.UserDao;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PostDaoDbImpl extends PostDaoImpl {
    private Connection conn = null;
    private UserDao userDao;
    
    public PostDaoDbImpl(Connection conn, UserDao userDao) {
        this.conn = conn;
        this.userDao = userDao;
    }

    @Override
    public void addPost(Post post) {
        String sql = "INSERT INTO posts (author,content,posted) VALUES (?,?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, post.getAuthor().getUsername());
            pst.setString(2, post.getContent());
            pst.setDate(3, new Date(post.getPostDate().getTime()));
            pst.executeUpdate();
            ResultSet rs = pst.getGeneratedKeys();
            rs.next();
            post.setId(rs.getInt(1));
            rs.close();
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    @Override
    public List<Post> findByRange(int page, int size) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY posted DESC " +
                "OFFSET " + (page * size) + "ROWS FETCH NEXT " + size +
                " ROWS ONLY";
        try (Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(sql)) {
             while (rs.next()) {
                 User author = userDao.findByUsername(rs.getString("author"));
                 String content = rs.getString("content");
                 Date postDate = rs.getDate("posted");
                 int id = rs.getInt("id");
                 Post post = new Post(content, author, postDate, id);
                 posts.add(post);
             }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
        return posts;
     }

    @Override
    public int allPostCount() {
        String sql = "SELECT COUNT(id) AS n FROM posts";
        try (Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(sql)) {
            rs.next();
            return rs.getInt("n");
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    @Override
    public List<Post> findUserPostsByRange(String who, int page, int size) {
       List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE author = '" + who +
                "' ORDER BY posted DESC " +
                "OFFSET " + (page * size) + "ROWS FETCH NEXT " + size +
                " ROWS ONLY";
        try (Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(sql)) {
            User author = userDao.findByUsername(who);
            while (rs.next()) {
                String content = rs.getString("content");
                Date postDate = rs.getDate("posted");
                int id = rs.getInt("id");
                Post post = new Post(content, author, postDate, id);
                posts.add(post);
            }
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
        return posts;
    }

    @Override
    public int userPostCount(String username) {
        String sql = "SELECT COUNT(id) AS n FROM posts WHERE author = '"
                + username + "'";
        try (Statement stat = conn.createStatement();
             ResultSet rs = stat.executeQuery(sql)) {
            rs.next();
            return rs.getInt("n");
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
    
}
