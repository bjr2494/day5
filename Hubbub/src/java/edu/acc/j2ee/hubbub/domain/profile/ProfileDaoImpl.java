package edu.acc.j2ee.hubbub.domain.profile;

import edu.acc.j2ee.hubbub.domain.user.User;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class ProfileDaoImpl implements ProfileDao {
    public static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z]{1,50}$");
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w\\-\\.]+@[\\w\\-\\.]+$");
            
    private final List<Profile> profiles = new ArrayList<>();
    
    @Override
    public Profile findByUsername(String username) {
        for (Profile p : profiles)
            if (p.getOwner().getUsername().equals(username))
                return p;
        return null;
    }
    
    @Override
    public Profile findByUser(User user) {
        for (Profile p : profiles)
            if (p.getOwner().equals(user))
                return p;
        return null;
    }

    @Override
    public void addProfile(Profile profile) {
        if (profile.getOwner() == null)
            throw new IllegalArgumentException("Profile owner cannot be null");
        profiles.add(profile);
    }

    @Override
    public boolean validate(Profile profile) {        
        if (profile.getFirstName().length() > 0 && !NAME_PATTERN.matcher(profile.getFirstName()).matches())
            return false;
        if (profile.getLastName().length() > 0 && !NAME_PATTERN.matcher(profile.getLastName()).matches())
            return false;
        if (profile.getEmailAddress().length() > 0 && !EMAIL_PATTERN.matcher(profile.getEmailAddress()).matches())
            return false;
        profile.setBiography(profile.getBiography()
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("'", "&apos;")
                .replaceAll("\"", "&quot;"));
        if (profile.getBiography().length() > 255)
            return false;
        return true;
    }

    @Override
    public void update(Profile owned, Profile bean) {
        owned.setFirstName(bean.getFirstName());
        owned.setLastName(bean.getLastName());
        owned.setEmailAddress(bean.getEmailAddress());
        owned.setBiography(bean.getBiography());
    }

}
