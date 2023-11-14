package com.personalwebsite.api.post;


import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    private final PostRepository repository;

    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public Long createPost(PostDto req) {
        Post post = new Post(
                req.title(),
                req.rating(),
                req.image(),
                req.description(),
                req.body());
        repository.saveAndFlush(post);
        return post.getId();
    }

    public Optional<Post> readPost(Long id) {
        return repository.findById(id);
    }

    public List<Post> readAllPosts() {
        return repository.findAllByOrderByDateDesc();
    }

    @Transactional
    public void updatePost(Long id, PostDto req) {
        Post post = repository.findById(id)
                .orElseThrow(() ->
                        new IllegalStateException("Post does not exist.")
                );

        post.setTitle(req.title());
        post.setRating(req.rating());
        post.setImage(req.image());
        post.setBody((req.body()));

    }

    public void deletePost(Long id) {
        repository.deleteById(id);
    }
}
