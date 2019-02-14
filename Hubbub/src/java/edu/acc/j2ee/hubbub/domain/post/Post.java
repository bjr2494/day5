package edu.acc.j2ee.hubbub.domain.post;

import edu.acc.j2ee.hubbub.domain.user.User;
import java.util.Date;

public class Post implements java.io.Serializable {
    private String content;
    private User author;
    private Date postDate;
    private int  id;

    public Post() {
    }

    public Post(String content, User author) {
        this.content = content;
        this.author = author;
        this.postDate = new Date();
    }

    public Post(String content, User author, Date postDate) {
        this.content = content;
        this.author = author;
        this.postDate = postDate;
    }
    
    public Post(String content, User author, Date postDate, int id) {
        this(content, author, postDate);
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return String.format("Post[author:%s, postDate:%s, " +
            "content:%s, id:%d]", author, postDate, content, id);
    }
}
