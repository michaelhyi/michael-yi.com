package com.personalwebsite.api.post;

import com.personalwebsite.api.exception.PostNotFoundException;
import com.personalwebsite.api.s3.S3Buckets;
import com.personalwebsite.api.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
            
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository repository;
    @Mock
    private S3Service s3Service;
    @Mock
    private S3Buckets buckets;
    private PostService underTest;

    @BeforeEach
    void setUp() {
        underTest = new PostService(repository, s3Service, buckets);
    }

    @Test
    void willThrowWhenCreatePostWithSameTitle() {
        Post post = new Post("id", "title", "content");
        given(repository.findById(post.getId())).willReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () ->
                underTest.createPost(new PostRequest("id", "<h1>title</h1><p>content</p>")));
        verify(repository).findById(post.getId());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void createPost() {
        Post post = new Post("id", "title", "content");
        given(repository.findById(post.getId())).willReturn(Optional.empty());

        underTest.createPost(
                new PostRequest("id", "<h1>title</h1>content")
        );

        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);

        verify(repository).findById(post.getId());
        verify(repository).saveAndFlush(postArgumentCaptor.capture());

        Post capturedPost = postArgumentCaptor.getValue();

        assertEquals(post.getTitle(), capturedPost.getTitle());
        assertEquals(post.getContent(), capturedPost.getContent());
    }

    @Test
    void willThrowWhenCreatePostImageOnNonexistentPost() {
        given(repository.findById("id")).willThrow(new PostNotFoundException());

        assertThrows(PostNotFoundException.class, () -> underTest.createPostImage("id", null));

        verify(repository).findById("id");
        verifyNoMoreInteractions(repository);
        verifyNoMoreInteractions(s3Service);
    }

    @Test
    void willDeleteS3ObjectWhenCreatePostImageIfImageAlreadyExists() {
        given(repository.findById("id")).willReturn(Optional.of(new Post("id", "title", "content")));
        given(s3Service.getObject(buckets.getBlog(), "id")).willReturn(new byte[0]);

        underTest.createPostImage("id", new MockMultipartFile("file", new byte[0]));

        assertDoesNotThrow(() -> PostNotFoundException.class);
        assertDoesNotThrow(() -> RuntimeException.class);

        verify(repository).findById("id");
        verify(s3Service).deleteObject(buckets.getBlog(), "id");
        verify(s3Service).putObject(buckets.getBlog(), "id", new byte[0]); 
    }

    //TODO: complete this test
    @Test
    void willThrowWhenCreatePostImageWithBadFile() {
        given(repository.findById("id")).willReturn(Optional.of(new Post("id", "title", "content")));

        underTest.createPostImage("id", new MockMultipartFile("file", new byte[0]));
        verify(repository).findById("id");
        verify(s3Service).putObject(eq(buckets.getBlog()), any(), eq(new byte[0]));
    }

    @Test
    void createPostImage() {
        given(repository.findById("id")).willReturn(Optional.of(new Post("id", "title", "content")));
        given(s3Service.getObject(buckets.getBlog(), "id")).willReturn(null);

        underTest.createPostImage("id", new MockMultipartFile("file", new byte[0]));

        verify(repository).findById("id");
        verify(s3Service).putObject(eq(buckets.getBlog()), any(), eq(new byte[0]));
    }

    @Test
    void willThrowReadPostWhenPostNotFound() {
        given(repository.findById("id")).willReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> underTest.readPost("id"));
        verify(repository).findById("id");
    }

    @Test
    void readPost() {
        given(repository.findById("id")).willReturn(Optional.of(new Post("id", "title", "content")));

        Post post = underTest.readPost("id");

        assertEquals("title", post.getTitle());
        assertEquals("content", post.getContent());
        verify(repository).findById("id");
    }

    @Test
    void willThrowReadPostImageWhenPostNotFound() {
        given(repository.findById("id")).willReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> underTest.readPostImage("id"));
        verify(repository).findById("id");
        verify(s3Service, never()).getObject(eq(buckets.getBlog()), any());
    }

    @Test
    void readPostImage() {
        given(repository.findById("id")).willReturn(Optional.of(new Post("id", "title", "content")));
        byte[] expectedFile = new byte[0];
        given(s3Service.getObject(buckets.getBlog(), "id")).willReturn(expectedFile);

        byte[] file = underTest.readPostImage("id");
        assertEquals(expectedFile, file);
        verify(repository).findById("id");
        verify(s3Service).getObject(buckets.getBlog(), "id");
    }

    @Test
    void readAllPosts() {
        underTest.readAllPosts();
        verify(repository).findAll(Sort.by(Sort.Direction.DESC, "date"));
    }

    @Test
    void willThrowUpdatePostWhenPostDoesNotExist() {
        given(repository.findById("id")).willReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> underTest.updatePost("id", new PostRequest("id", "<h1>title1</h1>content1")));

        verify(repository).findById("id");
        verify(repository, never()).save(any());
    }

    @Test
    void updatePost() {
        given(repository.findById("id")).willReturn(Optional.of(new Post("id", "title", "content")));

        underTest.updatePost("id", new PostRequest("id", "<h1>title1</h1>content1"));

        ArgumentCaptor<Post> postArgumentCaptor = ArgumentCaptor.forClass(Post.class);

        verify(repository).findById("id");
        verify(repository).save(postArgumentCaptor.capture());
    }

    @Test
    void willThrowDeletePostWhenPostNotFound() {
        given(repository.findById("id")).willReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> underTest.deletePost("id"));
        verify(s3Service, never()).deleteObject(eq(buckets.getBlog()), any());
        verify(repository, never()).deleteById("id");
    }

    @Test
    void deletePost() {
        given(repository.findById("id")).willReturn(Optional.of(new Post("id", "title", "content")));

        underTest.deletePost("id");

        verify(s3Service).deleteObject(buckets.getBlog(), "id");
        verify(repository).deleteById("id");
    }
}