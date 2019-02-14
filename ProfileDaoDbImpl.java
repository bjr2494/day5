package edu.acc.j2ee.hubbub.domain.profile;

import edu.acc.j2ee.hubbub.DaoException;
import edu.acc.j2ee.hubbub.domain.user.User;
import edu.acc.j2ee.hubbub.domain.user.UserDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProfileDaoDbImpl extends ProfileDaoImpl {
    private Connection conn;
    private UserDao userDao;
    
    public ProfileDaoDbImpl(Connection conn, UserDao userDao) {
        this.conn = conn;
        this.userDao = userDao;
    }
    
    String profFields = "SELECT firstname FROM profiles WHERE firstname IS NOT NULL)" +
            " (SELECT lastname FROM profiles WHERE lastname IS NOT NULL)" +
            " (SELECT email FROM profiles WHERE email IS NOT NULL)" +
            " (SELECT biography FROM profiles WHERE biography IS NOT NULL)";
    
    String addProfFields = profFields.replaceAll("SELECT", "INSERT")
            .replaceAll("FROM", "INTO");
    
    public String[] usefulQuery(ResultSet rs) throws SQLException {
        String first = null;
        if (first != null)
            first = rs.getString("firstname");
        else first = "";
       
        String last = null;
        if (last != null)
            last = rs.getString("lastname");
        else last = "";
        
        String email = null;
        if (email != null)
            email = rs.getString("email");
        else email = "";
        
        String bio = null;
        if (bio != null)
            bio = rs.getString("biography");
        else bio = "";
        
        String[] profStrings = { first, last, email, bio };
        return profStrings;
    }
    
        @Override
    public Profile findByUsername(String username) {
        String sql = "SELECT owner,ID FROM profiles WHERE username=" + username +
                profFields;
        try (Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql)) { 
            User owner = userDao.findByUsername(rs.getString("owner"));
            int id = rs.getInt("id");
            String[] profStrings = this.usefulQuery(rs);
            return new Profile(owner, profStrings[0], profStrings[1],
                profStrings[2], profStrings[3], id);
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
   
        @Override
    public Profile findByUser(User user) {
        String sql = "SELECT ID FROM profiles WHERE user=" + user +
                profFields;
        try (Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql)) {
            int id = rs.getInt("id");
            String[] profStrings = this.usefulQuery(rs);
            return new Profile(user, profStrings[0], profStrings[1], 
                profStrings[2], profStrings[3], id);
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
    
        @Override
    public void addProfile(Profile profile) {
        String sql = "INSERT INTO PROFILES (owner, id) VALUES (?, ?)";
        String sql2 = addProfFields;
        try (PreparedStatement pst = conn.prepareStatement(sql);
            Statement stat = conn.createStatement()) {
            pst.setString(1, profile.getOwner().getUsername());
            pst.setInt(2, profile.getId());
            pst.executeUpdate();

            ResultSet rs = stat.executeQuery(sql2);
            String[] profStrings = this.usefulQuery(rs);
            profile.setFirstName(rs.getString(profStrings[0]));
            profile.setLastName(rs.getString(profStrings[1]));
            profile.setEmailAddress(rs.getString(profStrings[2]));
            profile.setBiography(rs.getString(profStrings[3]));
            stat.executeUpdate(sql2);
            rs.close();
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
    
        @Override
    public void update(Profile owned, Profile bean) {
        String sql = "UPDATE PROFILES SET firstname =" + bean.getFirstName() + 
                " SET lastname =" + bean.getLastName() + " SET email =" +
                bean.getEmailAddress() + " SET biography =" + bean.getBiography();
        try (Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery(sql)) {
            while (rs.next())
                owned.setFirstName(rs.getString(bean.getFirstName()));
                owned.setLastName(rs.getString(bean.getLastName()));
                owned.setEmailAddress(rs.getString(bean.getEmailAddress()));
                owned.setBiography(rs.getString(bean.getBiography()));
        }
        catch (SQLException sqle) {
            throw new DaoException(sqle);
        }
    }
}
