package com.onpurple.controller;

import com.onpurple.category.PostCategory;
import com.onpurple.dto.request.PostRequestDto;
import com.onpurple.dto.response.ApiResponseDto;
import com.onpurple.dto.response.PostResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.PostService;
import com.onpurple.external.ValidationUtil;
import com.onpurple.external.s3.AwsS3UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  private final ValidationUtil validationUtil;


  // 게시글 작성
  @PostMapping
  public ApiResponseDto<PostResponseDto> createPost(@RequestPart(value = "data", required = false) PostRequestDto requestDto,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @RequestPart(value = "imageUrl", required = false) List<MultipartFile> multipartFiles) {

    validationUtil.validateMultipartFiles(multipartFiles);
    List<String> imgPaths = s3Service.upload(multipartFiles);
    return postService.createPost(requestDto, userDetails.getUser(), imgPaths);
  }


  // 상세 게시글 가져오기
  @GetMapping("/{postId}")
  public ApiResponseDto<?> getPost(@PathVariable Long postId) {
    return postService.getPost(postId);
  }


  // 게시글 수정
  @PatchMapping("/{postId}")
  public ApiResponseDto<?> updatePost(@PathVariable Long postId,
                                      @RequestPart(value = "data") PostRequestDto requestDto,
                                      @RequestPart("imageUrl") List<MultipartFile> multipartFiles,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    validationUtil.validateMultipartFiles(multipartFiles);
    List<String> imgPaths = s3Service.upload(multipartFiles);
    return postService.updatePost(postId, requestDto, userDetails.getUser(), imgPaths);
  }

  //게시글 삭제
  @DeleteMapping("/{postId}")
  public ApiResponseDto<?> deletePost(@PathVariable Long postId,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return postService.deletePost(postId, userDetails.getUser());
  }

  // 카테고리별 전체 게시글 가져오기
  @GetMapping //기본 카테고리 MEET 번개
  public ApiResponseDto<?> getAllPost(
          @RequestParam(defaultValue = "MEET", value = "category") PostCategory category,
          @RequestParam int page, @RequestParam int size) {
    Pageable pageable = PageRequest.of(page, size);

    return postService.getAllPostCategory(category, pageable);
  }

  // 카테고리별 전체 게시글 검색
  @GetMapping("/search") //기본 카테고리 MEET 번개
  public ApiResponseDto<?> getAllPosts(
          @RequestParam(defaultValue = "MEET", value = "category") PostCategory category,
          @RequestParam String keyword, @RequestParam int page,
          @RequestParam int size) {
    Pageable pageable = PageRequest.of(page, size);

    return postService.getAllPostCategorySearch(category, keyword, pageable);
  }

}

