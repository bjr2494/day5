package edu.acc.j2ee.hubbub;

import edu.acc.j2ee.hubbub.domain.user.User;
import edu.acc.j2ee.hubbub.domain.user.UserDao;
import edu.acc.j2ee.hubbub.domain.post.PostDao;
import edu.acc.j2ee.hubbub.domain.post.Post;
import edu.acc.j2ee.hubbub.domain.profile.Profile;
import edu.acc.j2ee.hubbub.domain.profile.ProfileDao;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    private UserDao userDao;
    private PostDao postDao;
    private ProfileDao profileDao;

    @Override
    public void init() {
        userDao = this.getUserDao();
        postDao = this.getPostDao();
        profileDao = this.getProfileDao();
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action"), destination;
        if (action == null || action.length() == 0)
            action = this.getServletConfig().getInitParameter("default.action");
        switch (action) {
            default:
            case "wall":
            case "timeline": destination = timeline(request); break;
            case "login": destination = login(request); break;
            case "logout": destination = logout(request); break;
            case "join": destination = join(request); break;
            case "post": destination = post(request); break;
            case "profile": destination = profile(request); break;
        }
        
        String redirect = this.getServletConfig().getInitParameter("redirect.tag");
        if (destination.startsWith(redirect)) {
            response.sendRedirect("main?action=" + destination.substring(
                destination.indexOf(redirect) + redirect.length()));
            return;
        }
        String viewDir = this.getServletConfig().getInitParameter("view.dir");
        String viewType = this.getServletConfig().getInitParameter("view.type");
        request.getRequestDispatcher(viewDir + destination + viewType)
                .forward(request, response);
    }
    
    private String login(HttpServletRequest request) {
        
        if (request.getSession().getAttribute("user") != null)
            return "redirect:timeline";
        if (request.getMethod().equalsIgnoreCase("GET"))
            return "login";
        
        String userText = request.getParameter("username");
        String passText = request.getParameter("password");
        
        String destination = "login";
        if (!userDao.validate(userText, passText))
            request.setAttribute("flash", "Invalid Credentials");
        else {
            User user = userDao.authenticate(userText, passText);
            if (user == null)
                request.setAttribute("flash", "Access Denied");
            else {
                request.getSession().setAttribute("user", user);
                destination = "redirect:timeline";
            }
        }
        return destination;
    }
    
    private String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:timeline";
    }
    
    private String timeline(HttpServletRequest request) {
        
        int pageSize = Integer.parseInt(this.getServletContext().getInitParameter("page.size"));
        Pager pager = new Pager(pageSize);
        try { pager.setPage(Integer.parseInt(request.getParameter("page"))); }
        catch (NullPointerException | NumberFormatException e) {}
        
        List<Post> posts;
        if (request.getParameter("action") == null || request.getParameter("action").equalsIgnoreCase("timeline")) {
            posts = postDao.findByRange(pager.getPage(), pager.getPageSize());
            pager.setSize(postDao.allPostCount());
        }
        else {
            String whoseWall = request.getParameter("for");
            if (whoseWall == null || whoseWall.length() == 0) {
                @SuppressWarnings("unchecked")
                User user = (User)request.getSession().getAttribute("user");
                whoseWall = user.getUsername();
            }
            // TODO: we need to validate whoseWall here because the DB doesn't!!!!
            posts = postDao.findUserPostsByRange(whoseWall, pager.getPage(), pager.getPageSize());
            pager.setSize(postDao.userPostCount(whoseWall));
        }
        request.setAttribute("posts", posts);
        request.setAttribute("pager", pager);
        return "timeline";
    }
    
    private String join(HttpServletRequest request) {
        // only available when logged out:
        if (request.getSession().getAttribute("user") != null)
            return "redirect:timeline";
        
        // GET mapping
        if (request.getMethod().equalsIgnoreCase("GET"))
            return "join";

        // POST mapping
        String username = request.getParameter("username");
        String password1 = request.getParameter("password1");
        String password2 = request.getParameter("password2");
        if (!password1.equals(password2)) {
            request.setAttribute("flash", "Passwords don't match");
            return "join";
        }
 
        if (!userDao.validate(username, password2)) {
            request.setAttribute("flash", "Username or password invalid");
            return "join";
        }

        if (userDao.findByUsername(username) != null) {
            request.setAttribute("flash", "That username is taken");
            return "join";
        }
        
        // Users must have a profile initialized with the user as owner
        User user = new User(username, password1);
        Profile profile = new Profile(user);
        userDao.addUser(user);
        profileDao.addProfile(profile);
        
        request.getSession().setAttribute("user", user);
        return "redirect:timeline";
    }
    
    private String post(HttpServletRequest request) {
      
        @SuppressWarnings("unchecked")
        User author = (User)request.getSession().getAttribute("user");
        if (author == null)
            return "redirect:timeline";
        if (request.getMethod().equalsIgnoreCase("GET"))
            return "post";
        String content = request.getParameter("content");
        if (content == null || content.length() < 1) {
            request.setAttribute("flash", "Your post was empty");
            return "post";
        }
        if (content.length() > 255) {
            request.setAttribute("flash", "Too much post in yer post");
            request.setAttribute("content", content);
            return "post";
        }
        Post post = new Post(content, author);
        postDao.addPost(post);
        request.getSession().setAttribute("lastPost", post);
        return "redirect:post";
    }
    
    private String profile(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        User user = (User)request.getSession().getAttribute("user");
        
        // Only available to logged-in users
        if (user == null)
            return "redirect:timeline";

        // GET mapping
        if (request.getMethod().equalsIgnoreCase("GET")) {
            String target = request.getParameter("for");
            Profile profile = profileDao.findByUsername(target);
            if (profile == null)
                request.setAttribute("flash", "There is no profile for " + target);
            request.setAttribute("profile", profile);
            return "profile";
        }
        
        // POST mapping
        Profile temp = new Profile();
        temp.setFirstName(request.getParameter("firstName"));
        temp.setLastName(request.getParameter("lastName"));
        temp.setEmailAddress(request.getParameter("emailAddress"));
        temp.setBiography(request.getParameter("biography"));
        Profile profile = profileDao.findByUser(user);
        if (!profileDao.validate(temp)) {
            request.setAttribute("flash", "Invalid fields in profile");
        }
        else {
            profileDao.update(profile, temp);
            request.setAttribute("success", "Profile updated");
        }
        request.setAttribute("profile", profile);
        return "profile";
    }

    private UserDao getUserDao() {
        @SuppressWarnings("unchecked")
        UserDao dao= (UserDao)this.getServletContext().getAttribute("userDao");
        return dao;
    }
    
    private PostDao getPostDao() {
        @SuppressWarnings("unchecked")
        PostDao dao = (PostDao)this.getServletContext().getAttribute("postDao");
        return dao;
    }
    
    private ProfileDao getProfileDao() {
        @SuppressWarnings("unchecked")
        ProfileDao dao = (ProfileDao)this.getServletContext().getAttribute("profileDao");
        return dao;
    }
    
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
