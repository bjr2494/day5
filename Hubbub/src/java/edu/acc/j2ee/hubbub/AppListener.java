package edu.acc.j2ee.hubbub;

import edu.acc.j2ee.hubbub.domain.user.UserDaoImpl;
import edu.acc.j2ee.hubbub.domain.user.User;
import edu.acc.j2ee.hubbub.domain.user.UserDao;
import edu.acc.j2ee.hubbub.domain.post.PostDaoImpl;
import edu.acc.j2ee.hubbub.domain.post.PostDao;
import edu.acc.j2ee.hubbub.domain.post.Post;
import edu.acc.j2ee.hubbub.domain.profile.Profile;
import edu.acc.j2ee.hubbub.domain.profile.ProfileDao;
import edu.acc.j2ee.hubbub.domain.profile.ProfileDaoImpl;
import java.util.Date;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class AppListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final ServletContext sc = sce.getServletContext();
        
        String u1user = sce.getServletContext().getInitParameter("user.1.name");
        String u1pass = sce.getServletContext().getInitParameter("user.1.pass");
        String u2user = sce.getServletContext().getInitParameter("user.2.name");
        String u2pass = sce.getServletContext().getInitParameter("user.2.pass");
        
        UserDao userDao = new UserDaoImpl();        
        User u1 = new User(u1user, u1pass, new Date(118, 5, 6));
        User u2 = new User(u2user, u2pass, new Date(119, 0, 28));
        userDao.addUser(u1);
        userDao.addUser(u2);
        sc.setAttribute("userDao", userDao);
        
        PostDao postDao = new PostDaoImpl();
        Post p1 = new Post("My first Hubbub Post!", u1);
        Post p2 = new Post("Joined 'cause johndoe told me to!", u2);
        Post p3 = new Post("My second post.", u1);
        Post p4 = new Post("My third post.", u1);
        Post p5 = new Post("My fourth post.", u1);
        Post p6 = new Post("My fifth post.", u1);
        postDao.addPost(p1);
        postDao.addPost(p2);
        postDao.addPost(p3);
        postDao.addPost(p4);
        postDao.addPost(p5);
        postDao.addPost(p6);
        sc.setAttribute("postDao", postDao);
        
        ProfileDao profileDao = new ProfileDaoImpl();
        Profile prof1 = new Profile(u1);
        Profile prof2 = new Profile(u2);
        profileDao.addProfile(prof1);
        profileDao.addProfile(prof2);
        sc.setAttribute("profileDao", profileDao);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
