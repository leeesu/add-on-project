package com.onpurple.controller;

import com.onpurple.category.PostCategory;
import com.onpurple.dto.request.PostRequestDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.security.UserDetailsImpl;
import com.onpurple.service.PostService;
import com.onpurple.util.ValidationUtil;
import com.onpurple.util.s3.AwsS3UploadService;
import lombok.RequiredArgsConstructor;
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
  public ResponseDto<?> createPost(@RequestPart(value = "data",required = false) PostRequestDto requestDto,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails,
                                   @RequestPart(value = "imageUrl",required = false) List<MultipartFile> multipartFiles) {

    validationUtil.validateMultipartFiles(multipartFiles);
    List<String> imgPaths = s3Service.upload(multipartFiles);
    return postService.createPost(requestDto,userDetails.getUser(), imgPaths);
  }

  // 카테고리별 전체 게시글 가져오기
  @GetMapping //기본 카테고리 meet 번개
  public ResponseDto<?> getAllPosts(@RequestParam(defaultValue = "MEET", value="category") PostCategory category) {
    return postService.getAllPost(category);
  }

  
  // 상세 게시글 가져오기
  @GetMapping( "/{postId}")
  public ResponseDto<?> getPost(@PathVariable Long postId) {
    return postService.getPost(postId);
  }


  // 게시글 수정
  @PatchMapping ( "/{postId}")
  public ResponseDto<?> updatePost(@PathVariable Long postId,
                                   @RequestPart(value = "data") PostRequestDto requestDto,
                                   @RequestPart("imageUrl") List<MultipartFile> multipartFiles,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {

    validationUtil.validateMultipartFiles(multipartFiles);
    List<String> imgPaths = s3Service.upload(multipartFiles);
    return postService.updatePost(postId, requestDto, userDetails.getUser(), imgPaths);
  }

  //게시글 삭제
  @DeleteMapping( "/{postId}")
  public ResponseDto<?> deletePost(@PathVariable Long postId,
                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
    return postService.deletePost(postId, userDetails.getUser());
  }

  // 카테고리별 전체 게시글 검색
//  @GetMapping("/post/search") //기본 카테고리 meet 번개
//  public ResponseDto<?> getAllPostSearch(@RequestParam String keyword) {
//    return postService.getAllPostSearch(keyword);
//  }

//    // 카테고리별 전체 게시글 가져오기
//  @GetMapping("/post") //기본 카테고리 meet 번개
//  public ResponseDto<?> getAllPosts(@RequestParam(defaultValue = "meet", value="category")  String category,
//                                    @RequestParam int page, @RequestParam int size) {
//    return postService.getAllPost(category,page,size);
//  }
//
//  // 카테고리별 전체 게시글 검색
//  @GetMapping("/post/search") //기본 카테고리 meet 번개
//  public ResponseDto<?> getAllPosts(@RequestParam(defaultValue = "meet", value="category")  String category,
//                                    @RequestParam String keyword, @RequestParam int page, @RequestParam int size) {
//    return postService.getAllPostSearch(category, keyword,page,size);
//  }

  }

