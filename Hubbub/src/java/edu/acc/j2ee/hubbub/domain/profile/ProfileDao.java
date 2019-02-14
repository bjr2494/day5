package edu.acc.j2ee.hubbub.domain.profile;

import edu.acc.j2ee.hubbub.domain.user.User;

public interface ProfileDao {

    Profile findByUsername(String username);
    
    Profile findByUser(User user);
    
    void addProfile(Profile profile);
    
    boolean validate(Profile profile);
    
    void update(Profile owned, Profile bean);
}
