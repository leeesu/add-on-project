package com.onpurple.service;

import com.google.common.base.Optional;
import com.onpurple.category.PostCategory;
import com.onpurple.dto.request.PostRequestDto;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.CommentResponseDto;
import com.onpurple.dto.response.MessageResponseDto;
import com.onpurple.dto.response.PostResponseDto;
import com.onpurple.enums.SuccessCode;
import com.onpurple.model.Comment;
import com.onpurple.model.Post;
import com.onpurple.model.User;
import com.onpurple.repository.CommentRepository;
import com.onpurple.repository.PostRepository;
import com.onpurple.util.ImageUtil;
import com.onpurple.util.TestUtil;
import com.onpurple.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {


    @Mock
    PostRepository postRepository;

    @Mock
    ImageUtil imageUtil;

    @Mock
    CommentRepository commentRepository;
    @Mock
    ValidationUtil validationUtil;

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

        given(imageUtil.addImage(any(), any())).willReturn(imgPaths);


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
        when(validationUtil.validatePost(any())).thenReturn(post);
        // when
        ApiResponseDto<PostResponseDto> response = postService.getPost(post.getId());
        // then
        verify(validationUtil, times(1)).validatePost(any());
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
        when(validationUtil.validatePost(any())).thenReturn(post);
        when(imageUtil.updateImage(imgPaths, post)).thenReturn(imgPaths);

        ApiResponseDto<PostResponseDto> response = postService.updatePost(post.getId(), updateDto, user, imgPaths);

        // then
        verify(validationUtil).validatePost(post.getId());
        verify(imageUtil).updateImage(imgPaths, post);
        assertNotNull(response);
        verify(validationUtil, times(1)).validatePost(any());
        assertEquals(response.getData().getTitle(), updatePost.getTitle());
        assertEquals(response.getData().getContent(), updatePost.getContent());
    }

    @Test
    @DisplayName("게시글 삭제")
    void test_delete_post() {
        // given
        when(validationUtil.validatePost(any())).thenReturn(post);
        // when
        ApiResponseDto<MessageResponseDto> response = postService.deletePost(post.getId(), user);
        // then
        verify(validationUtil, times(1)).validatePost(any());
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
        when(validationUtil.validatePost(any())).thenReturn(post);
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
        verify(validationUtil, times(1)).validatePost(any());
        assertThat(post.getView()).isNotEqualTo(40);
        assertEquals(response.getData().getTitle(), post.getTitle());
        assertEquals(response.getData().getContent(), post.getContent());
    }










}