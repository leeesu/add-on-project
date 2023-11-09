package com.onpurple.domain.post.controller;

import com.onpurple.domain.post.category.PostCategory;
import com.onpurple.domain.post.dto.PostRequestDto;
import com.onpurple.domain.post.dto.PostResponseDto;
import com.onpurple.domain.post.service.PostService;
import com.onpurple.global.dto.ApiResponseDto;
import com.onpurple.global.dto.MessageResponseDto;
import com.onpurple.global.external.AwsS3UploadService;
import com.onpurple.global.security.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "게시글 API", description = "게시글 생성, 전체 게시글 조회, 상세 게시글 조회, 게시글 수정, 게시글 삭제")
public class PostController {

  private final PostService postService;
  private final AwsS3UploadService s3Service;


  // 게시글 작성
  @PostMapping
  @Operation(summary = "게시글 생성", description = "게시글 생성")
  @Parameter(name = "postRequestDto", description = "게시글 생성 정보", required = true)
  @Parameter(name = "userDetails", description = "게시글을 생성할 사용자의 정보", required = true)
  @Parameter(name = "multipartFiles", description = "게시글에 첨부할 이미지 파일(다중)", required = true)
  public ApiResponseDto<PostResponseDto> createPost(@RequestPart(value = "data", required = false) final PostRequestDto postRequestDto,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                    @NotNull @RequestPart(value = "imageUrl", required = false) final List<MultipartFile> multipartFiles) {

    List<String> imgPaths = s3Service.upload(multipartFiles);
    return postService.createPost(postRequestDto, userDetails.getUser(), imgPaths);
  }


  // 상세 게시글 가져오기
  @GetMapping("/{postId}")
  @Operation(summary = "상세 게시글 조회", description = "상세 게시글 조회")
  @Parameter(name = "postId", description = "조회할 게시글의 id", required = true)
  public ApiResponseDto<PostResponseDto> getPost(@PathVariable final Long postId) {
    return postService.getPost(postId);
  }


  // 게시글 수정
  @PatchMapping("/{postId}")
  @Operation(summary = "게시글 수정", description = "게시글 수정")
  @Parameter(name = "postId", description = "수정할 게시글의 id", required = true)
  @Parameter(name = "postRequestDto", description = "게시글 수정 정보", required = true)
  @Parameter(name = "userDetails", description = "게시글을 수정할 사용자의 정보", required = true)
  @Parameter(name = "multipartFiles", description = "게시글에 첨부할 이미지 파일(다중)", required = true)
  public ApiResponseDto<PostResponseDto> updatePost(@PathVariable final Long postId,
                                      @RequestPart(value = "data") final PostRequestDto requestDto,
                                      @RequestPart("imageUrl") final List<MultipartFile> multipartFiles,
                                      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    List<String> imgPaths = s3Service.upload(multipartFiles);
    return postService.updatePost(postId, requestDto, userDetails.getUser(), imgPaths);
  }

  //게시글 삭제
  @DeleteMapping("/{postId}")
  @Operation(summary = "게시글 삭제", description = "게시글 삭제")
  @Parameter(name = "postId", description = "삭제할 게시글의 id", required = true)
  @Parameter(name = "userDetails", description = "게시글을 삭제할 사용자의 정보", required = true)
  public ApiResponseDto<MessageResponseDto> deletePost(@PathVariable final Long postId,
                                                       @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return postService.deletePost(postId, userDetails.getUser());
  }

  // 카테고리별 전체 게시글 가져오기
  @GetMapping
  @Parameter(name = "category", description = "조회할 게시글의 카테고리", required = true)
  @Parameter(name = "page", description = "조회할 페이지", required = true)
  @Parameter(name = "size", description = "조회할 페이지의 사이즈", required = true)
  @Parameter(name = "userDetails", description = "게시글을 조회할 사용자의 정보", required = true)
  public ApiResponseDto<Slice<PostResponseDto>> getAllPost(
          @RequestParam(value = "category") final PostCategory category,
          @RequestParam int page, @RequestParam int size) {
    Pageable pageable = PageRequest.of(page, size);

    return postService.getAllPostCategory(category, pageable);
  }

  // 카테고리별 전체 게시글 검색
  @GetMapping("/search") //기본 카테고리 MEET 번개
  @Operation(summary = "카테고리별 전체 게시글 검색", description = "카테고리별 전체 게시글 검색")
  @Parameter(name = "category", description = "조회할 게시글의 카테고리", required = true)
  @Parameter(name = "page", description = "조회할 페이지", required = true)
  @Parameter(name = "size", description = "조회할 페이지의 사이즈", required = true)
  @Parameter(name = "keyword", description = "검색할 키워드", required = true)
  public ApiResponseDto<Slice<PostResponseDto>> getAllPostSearch(
          @RequestParam(value = "category") final PostCategory category,
          @RequestParam String keyword, @RequestParam int page,
          @RequestParam int size) {
    Pageable pageable = PageRequest.of(page, size);

    return postService.getAllPostCategorySearch(category, keyword, pageable);
  }

}

