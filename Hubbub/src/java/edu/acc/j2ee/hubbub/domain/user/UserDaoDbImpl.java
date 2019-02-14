package edu.acc.j2ee.hubbub.domain.user;

import edu.acc.j2ee.hubbub.DaoException;
import edu.acc.j2ee.hubbub.HashTool;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDaoDbImpl extends UserDaoImpl {
    private Connection conn;
    
    public UserDaoDbImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void addUser(User u) {
        u.setPassword(HashTool.hash(u.getPassword()));        
        String sql = "INSERT INTO users (username,password) VALUES (?,?)";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, u.getUsername());
            pst.setString(2, u.getPassword());
            pst.executeUpdate();
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }

    @Override
    public User findByUsername(String username) {
        String sql = String.format(
                "SELECT password,joined FROM users WHERE username='%s'",
                username);
        try (Statement stat = conn.createStatement();
                ResultSet rs = stat.executeQuery(sql)) {
            if (!rs.next()) return null;
            String storedHash = rs.getString("password");
            Date joinDate = rs.getDate("joined");
            return new User(username, storedHash, joinDate);
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
    
}
