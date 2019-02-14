package edu.acc.j2ee.hubbub.domain.post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostDaoImpl implements PostDao {
    
    private static final List<Post> posts = new ArrayList<>();

    @Override
    public void addPost(Post post) {
        posts.add(post);
    }

    @Override
    public List<Post> findByRange(int page, int size) {
        return posts.stream()
                .sorted((a,b) -> b.getPostDate().compareTo(a.getPostDate()))
                .skip(page * size)
                .limit(size)
                .collect(Collectors.toList());
    }
    
    @Override
    public int allPostCount() {
        return posts.size();
    }
    
    @Override
    public int userPostCount(String username) {
        return (int)posts.stream()
                .filter(p -> p.getAuthor().getUsername().equals(username))
                .count();
    }

    @Override
    public List<Post> findUserPostsByRange(String who, int page, int size) {
        return posts.stream()
                .filter(p -> p.getAuthor().getUsername().equals(who))
                .sorted((a,b) -> b.getPostDate().compareTo(a.getPostDate()))
                .skip(page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

}
