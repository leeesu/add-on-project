package com.onpurple.domain.post.controller;

import com.onpurple.domain.post.category.PostCategory;
import com.onpurple.domain.post.dto.PostRequestDto;
import com.onpurple.domain.post.dto.PostResponseDto;
import com.onpurple.domain.post.service.PostService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.external.AwsS3UploadService;
import com.onpurple.global.security.UserDetailsImpl;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/post")
public class PostController {

  private final PostService postService;
  private final AwsS3UploadService s3Service;


  // 게시글 작성
  @PostMapping
  public ApiResponseDto<PostResponseDto> createPost(@RequestPart(value = "data", required = false) final PostRequestDto requestDto,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @NotNull @RequestPart(value = "imageUrl", required = false) final List<MultipartFile> multipartFiles) {

    List<String> imgPaths = s3Service.upload(multipartFiles);
    return postService.createPost(requestDto, userDetails.getUser(), imgPaths);
  }


  // 상세 게시글 가져오기
  @GetMapping("/{postId}")
  public ApiResponseDto<PostResponseDto> getPost(@PathVariable final Long postId) {
    return postService.getPost(postId);
  }


  // 게시글 수정
  @PatchMapping("/{postId}")
  public ApiResponseDto<PostResponseDto> updatePost(@PathVariable final Long postId,
                                      @RequestPart(value = "data") final PostRequestDto requestDto,
                                      @RequestPart("imageUrl") final List<MultipartFile> multipartFiles,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    List<String> imgPaths = s3Service.upload(multipartFiles);
    return postService.updatePost(postId, requestDto, userDetails.getUser(), imgPaths);
  }

  //게시글 삭제
  @DeleteMapping("/{postId}")
  public ApiResponseDto<MessageResponseDto> deletePost(@PathVariable final Long postId,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return postService.deletePost(postId, userDetails.getUser());
  }

  // 카테고리별 전체 게시글 가져오기
  @GetMapping //기본 카테고리 MEET 번개
  public ApiResponseDto<Slice<PostResponseDto>> getAllPost(
          @RequestParam(value = "category") final PostCategory category,
          @RequestParam int page, @RequestParam int size) {
    Pageable pageable = PageRequest.of(page, size);

    return postService.getAllPostCategory(category, pageable);
  }

  // 카테고리별 전체 게시글 검색
  @GetMapping("/search") //기본 카테고리 MEET 번개
  public ApiResponseDto<Slice<PostResponseDto>> getAllPostSearch(
          @RequestParam(value = "category") final PostCategory category,
          @RequestParam String keyword, @RequestParam int page,
          @RequestParam int size) {
    Pageable pageable = PageRequest.of(page, size);

    return postService.getAllPostCategorySearch(category, keyword, pageable);
  }

}

