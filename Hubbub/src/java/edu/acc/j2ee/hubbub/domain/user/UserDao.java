package edu.acc.j2ee.hubbub.domain.user;

public interface UserDao {
    public void addUser(User u);
    public boolean validate(String username, String password);
    public User authenticate(String username, String password);
    public User findByUsername(String username);
}
