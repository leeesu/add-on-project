package com.onpurple.service;

import com.onpurple.domain.comment.repository.CommentRepository;
import com.onpurple.domain.post.category.PostCategory;
import com.onpurple.domain.post.dto.PostRequestDto;
import com.onpurple.domain.post.dto.PostResponseDto;
import com.onpurple.domain.post.model.Post;
import com.onpurple.domain.post.repository.PostRepository;
import com.onpurple.domain.post.service.PostService;
import com.onpurple.domain.user.model.User;
import com.onpurple.external.TestUtil;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.enums.SuccessCode;
import com.onpurple.global.helper.EntityValidatorManager;
import com.onpurple.global.helper.ImageUploaderManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {


    @Mock
    PostRepository postRepository;

    @Mock
    ImageUploaderManager imageUploaderManager;

    @Mock
    CommentRepository commentRepository;
    @Mock
    EntityValidatorManager entityValidatorManager;

    @InjectMocks
    PostService postService;



    @Mock
    Logger logger;

    final User user = mock(User.class);
    final PostRequestDto requestDto = TestUtil.createPost("제목1", "내용1");

    final Post post = Post.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .category(requestDto.getCategory())
            .user(user)
            .view(0)
            .build();
    @Test
    @DisplayName("게시글 생성")
    void createPost() {
        // given


        List<String> imgPaths = List.of("img1", "img2");

        given(imageUploaderManager.addImage(any(), any())).willReturn(imgPaths);


        given(postRepository.save(any())).willReturn(post);
        // when
        ApiResponseDto<PostResponseDto> savePost = postService.createPost(requestDto, user, imgPaths);
        // then
        assertThat(savePost.getData().getTitle()).isEqualTo(requestDto.getTitle());

    }

    @Test
    @DisplayName("게시글 카테고리 조회")
    void getAllPostCategory() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<PostResponseDto> mockResponseList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mockResponseList.add(mock(PostResponseDto.class));
        }
        Slice<PostResponseDto> mockSlice = new SliceImpl<>(mockResponseList, pageRequest, false);

        when(postRepository.findAllByCategory(any(), any())).thenReturn(mockSlice);
        // when
        ApiResponseDto<Slice<PostResponseDto>> response = postService.getAllPostCategory(PostCategory.MEET, pageRequest);

        // then
        assertEquals(response.getData().getContent().size(), 10);
        verify(postRepository, times(1)).findAllByCategory(any(), any());
    }

    @Test
    @DisplayName("게시글 카테고리 검색 조회")
    void getAllPostCategorySearch() {
        // given
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<PostResponseDto> mockResponseList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mockResponseList.add(mock(PostResponseDto.class));
        }

        Slice<PostResponseDto> mockSlice = new SliceImpl<>(mockResponseList, pageRequest, false);

        when(postRepository.findAllByCategorySearchScroll(any(), any(), any())).thenReturn(mockSlice);

        // when
        ApiResponseDto<Slice<PostResponseDto>> response =
                postService.getAllPostCategorySearch(PostCategory.MEET, "keyword", pageRequest);
        // then
        assertEquals(response.getData().getContent().size(), 10);
        verify(postRepository, times(1)).findAllByCategorySearchScroll(any(), any(), any());
    }

    @Test
    @DisplayName("게시글 단일 조회")
    void test_get_post() {
        // given
        when(entityValidatorManager.validatePost(any())).thenReturn(post);
        // when
        ApiResponseDto<PostResponseDto> response = postService.getPost(post.getId());
        // then
        verify(entityValidatorManager, times(1)).validatePost(any());
        assertEquals(response.getData().getTitle(), post.getTitle());
        assertEquals(response.getData().getContent(), post.getContent());
    }

    @Test
    @DisplayName("게시글 수정")
    void test_update_post() {
        // given
        PostRequestDto updateDto = PostRequestDto.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .category(PostCategory.MEET)
                .build();
        Post updatePost = Post.builder()
                .title(updateDto.getTitle())
                .content(updateDto.getContent())
                .category(updateDto.getCategory())
                .build();
        List<String> imgPaths = List.of("img1", "img2");
        //
        when(entityValidatorManager.validatePost(any())).thenReturn(post);
        when(imageUploaderManager.updateImage(imgPaths, post)).thenReturn(imgPaths);

        ApiResponseDto<PostResponseDto> response = postService.updatePost(post.getId(), updateDto, user, imgPaths);

        // then
        verify(entityValidatorManager).validatePost(post.getId());
        verify(imageUploaderManager).updateImage(imgPaths, post);
        assertNotNull(response);
        verify(entityValidatorManager, times(1)).validatePost(any());
        assertEquals(response.getData().getTitle(), updatePost.getTitle());
        assertEquals(response.getData().getContent(), updatePost.getContent());
    }

    @Test
    @DisplayName("게시글 삭제")
    void test_delete_post() {
        // given
        when(entityValidatorManager.validatePost(any())).thenReturn(post);
        // when
        ApiResponseDto<MessageResponseDto> response = postService.deletePost(post.getId(), user);
        // then
        verify(entityValidatorManager, times(1)).validatePost(any());
        assertEquals(SuccessCode.SUCCESS_POST_DELETE.getMessage(), response.getMessage());
    }

    @Test
    @DisplayName("조회수 증가")
    void test_view_count() {

        postService.viewCount(post);
        assertEquals(post.getView(), 1);
    }


    @Test
    @DisplayName("게시글 단일 조회_조회수 동시성 테스트")
    void test_get_post_view() throws InterruptedException{
        User user = mock(User.class);
        // given
        when(entityValidatorManager.validatePost(any())).thenReturn(post);
        int threadCount = 40;
        ExecutorService executorService = Executors.newFixedThreadPool(40);
        CountDownLatch latch = new CountDownLatch(40);


        for(int i=0; i < threadCount; i++){
            executorService.execute(() -> {
                postService.viewCount(post);
                latch.countDown();
            });
        }
        latch.await();
        // when
        ApiResponseDto<PostResponseDto> response = postService.getPost(post.getId());
        // then
        System.out.println("현재 조회 수 : "+ post.getView());                        ;
        verify(entityValidatorManager, times(1)).validatePost(any());
        assertThat(post.getView()).isNotEqualTo(40);
        assertEquals(response.getData().getTitle(), post.getTitle());
        assertEquals(response.getData().getContent(), post.getContent());
    }










}