package edu.acc.j2ee.hubbub.domain.post;

import java.util.List;

public interface PostDao {
    public void addPost(Post post);
    public List<Post> findByRange(int page, int size);
    public List<Post> findUserPostsByRange(String who, int page, int size);
    public int allPostCount();
    public int userPostCount(String username);

}
