package edu.acc.j2ee.hubbub.domain.user;

import edu.acc.j2ee.hubbub.HashTool;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class UserDaoImpl implements UserDao {
    
    private static final Pattern USER_PATT = Pattern.compile("^\\w{6,12}$");
    private static final Pattern PASS_PATT = Pattern.compile("^[^<>'\"]{8,16}$");
    private final List<User> users = new ArrayList<>();
    
    @Override
    public void addUser(User u) {
        u.setPassword(HashTool.hash(u.getPassword()));        
        users.add(u);
    }

    @Override
    public boolean validate(String username, String password) {
        if (username == null || password == null)
            return false;
        if (!USER_PATT.matcher(username).matches())
            return false;
        return PASS_PATT.matcher(password).matches();
    }

    @Override
    public User authenticate(String username, String password) {
        User user = this.findByUsername(username);
        if (user == null)
            return null;
        return HashTool.compare(password, user.getPassword()) ? user : null;
    }

    @Override
    public User findByUsername(String username) {
        for (User user : users)
            if (user.getUsername().equals(username))
                return user;
        return null;
    }
    
}
