package com.onpurple.controller;

import com.onpurple.dto.request.PostRequestDto;
import com.onpurple.dto.response.ResponseDto;
import com.onpurple.service.PostService;
import com.onpurple.util.AwsS3UploadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class PostController {

  private final PostService postService;
  private final AwsS3UploadService s3Service;


  // 게시글 작성
  @PostMapping( "/post")
  public ResponseDto<?> createPost(@RequestPart(value = "data",required = false) PostRequestDto requestDto,
                                   HttpServletRequest request, @RequestPart(value = "imageUrl",required = false) List<MultipartFile> multipartFiles) {

    if (multipartFiles == null) {
      throw new NullPointerException("사진을 업로드해주세요");
    }
    List<String> imgPaths = s3Service.upload(multipartFiles);
    return postService.createPost(requestDto,request, imgPaths);
  }

  // 카테고리별 전체 게시글 가져오기
  @GetMapping("/post") //기본 카테고리 meet 번개
  public ResponseDto<?> getAllPosts(@RequestParam(defaultValue = "meet", value="category")  String category) {
    return postService.getAllPost(category);
  }

  
  // 상세 게시글 가져오기
  @GetMapping( "/post/{postId}")
  public ResponseDto<?> getPost(@PathVariable Long postId) {
    return postService.getPost(postId);
  }


  // 게시글 수정
  @PatchMapping ( "/post/{postId}")
  public ResponseDto<?> updatePost(@PathVariable Long postId,
                                   @RequestPart(value = "data") PostRequestDto requestDto,
                                   @RequestPart("imageUrl") List<MultipartFile> multipartFiles,
                                   HttpServletRequest request) {

    if (multipartFiles == null) {
      throw new NullPointerException("사진을 업로드해주세요");
    }
    List<String> imgPaths = s3Service.upload(multipartFiles);
    return postService.updatePost(postId, requestDto, request, imgPaths);
  }

  //게시글 삭제
  @DeleteMapping( "/post/{postId}")
  public ResponseDto<?> deletePost(@PathVariable Long postId,
                                   HttpServletRequest request) {
    return postService.deletePost(postId, request);
  }

  // 카테고리별 전체 게시글 검색
  @GetMapping("/post/search") //기본 카테고리 meet 번개
  public ResponseDto<?> getAllPostSearch(@RequestParam String keyword) {
    return postService.getAllPostSearch(keyword);
  }

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
