package com.cos.insta.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.cos.insta.model.Image;
import com.cos.insta.model.Likes;
import com.cos.insta.model.Tag;
import com.cos.insta.model.User;
import com.cos.insta.repository.ImageRepository;
import com.cos.insta.repository.LikesRepository;
import com.cos.insta.repository.TagRepository;
import com.cos.insta.security.MyUserDetails;
import com.cos.insta.util.Utils;

@Controller
public class ImageController {
	
	@Value("${file.path}")
	private String fileRealPath;
	
	@Autowired
	private ImageRepository mImageRepo;
	
	@Autowired
	private TagRepository mTagRepo;
	
	@Autowired
	private LikesRepository mLikesRepo;
	
	
	@GetMapping("/image/explore")
	public String imageExplore
	(		
			Model model,
			@PageableDefault(size = 9, sort = "id", direction = Sort.Direction.DESC)
			Pageable pageable
	)
	{	
		//알고리즘(조회수가 높은것, 내주변에서 좋아요가 가장 많은 순으로 해보는 것 추천)
		Page<Image> pImages = mImageRepo.findAll(pageable);
		List<Image> images = pImages.getContent();
		//4. likeCount
		for (Image item : images) {
			int likeCount = mLikesRepo.countByImageId(item.getId());
			item.setLikeCount(likeCount);
		}
		model.addAttribute("images", images);
		return "image/explore";
	}
	
	
	// 수정 좋아요 카운트 증가
	@PostMapping("/image/like/{id}")
	public @ResponseBody String imageLike(
			@PathVariable int id,
			@AuthenticationPrincipal MyUserDetails userDetails
	) {
		
		Likes oldLike = mLikesRepo.findByUserIdAndImageId(
				userDetails.getUser().getId(), 
				id);
		
		Optional<Image> oImage = mImageRepo.findById(id);
		Image image = oImage.get();
		
		try {
			if(oldLike == null) { // 좋아요 안한 상태 (추가)
				Likes newLike = Likes.builder()
						.image(image)
						.user(userDetails.getUser())
						.build();
				
				mLikesRepo.save(newLike);
				// 좋아요 카운트 증가(리턴 값 수정)
				return "like";
			}else { // 좋아요 한 상태 (삭제)
				mLikesRepo.delete(oldLike);
				// 좋아요 카운트 증가(리턴 값 수정)
				return "unLike";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "fail";
	}
	
	// http://localhost:8080/image/feed/scroll?page=1..2..3..
	@GetMapping({"/image/feed/scroll"})
	public @ResponseBody List<Image> imageFeedScroll(
			//얘가 세션과 같은 역할을 하게 된다
			@AuthenticationPrincipal MyUserDetails userDetails,
			@PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC)
			Pageable pageable,
			Model model
			) {
		
		//1. 내가 팔로우한 친구들의 사진
		Page<Image> pageImages = 
				mImageRepo.findImage(userDetails.getUser().getId(), pageable);
		
		List<Image> images = pageImages.getContent();
		for (Image image : images) {
			Likes like = mLikesRepo.findByUserIdAndImageId(userDetails.getUser().getId(), image.getId());
			if(like != null) {
				image.setHeart(true);
			}
			int likeCount = mLikesRepo.countByImageId(image.getId());
			image.setLikeCount(likeCount);
		}
		
		return images;
	}
	
	@GetMapping({"/", "/image/feed"})
	public String imageFeed(
			//얘가 세션과 같은 역할을 하게 된다
			@AuthenticationPrincipal MyUserDetails userDetails,
			@PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC)
			Pageable pageable,
			Model model
			) {
		
		//1. 내가 팔로우한 친구들의 사진
		Page<Image> pageImages = 
				mImageRepo.findImage(userDetails.getUser().getId(), pageable);
		
		List<Image> images = pageImages.getContent();
//		포문을 돌리면서 사진 세장을 뽑고 그 세장을 내가 좋아요했는지 확인
		for (Image image : images) {
			Likes like = mLikesRepo.findByUserIdAndImageId(userDetails.getUser().getId(), image.getId());
			if(like != null) {
				image.setHeart(true);
			}
			//라이크카운트
			int likeCount = mLikesRepo.countByImageId(image.getId());
			image.setLikeCount(likeCount);
		}

		model.addAttribute("images", images);
		
		return "image/feed";
	}
	
	@GetMapping("/image/upload")
	public String imageUpload() {
		return "image/image_upload";
	}
	
	@PostMapping("/image/uploadProc")
	public String imageUploadProc(
			@AuthenticationPrincipal MyUserDetails userDetails,
			@RequestParam("file") MultipartFile file,
			@RequestParam("caption") String caption,
			@RequestParam("location") String location,
			@RequestParam("tags") String tags
			)
	{
		
		// 이미지 업로드 수행
		UUID uuid = UUID.randomUUID();
		String uuidFilename = uuid+"_"+file.getOriginalFilename();
		
		Path filePath = Paths.get(fileRealPath+uuidFilename);
		
		try {
			Files.write(filePath, file.getBytes());// 하드디스크 기록
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		User principal = userDetails.getUser();
		
		//image 모델에 @Builder를 달면 쓸 수있다.
		Image image = new Image();
		image.setCaption(caption);
		image.setLocation(location);
		image.setUser(principal);
		image.setPostImage(uuidFilename);
		
		//찾을 때는 <img src="/upload/파일명"/>
		
		mImageRepo.save(image);
		
		//Tag 객체 생성해서 따로 집어넣어야 영속성 전이가 된다.
		//이런 작업하기 싫으면 모델에서 cascade해줘도 되지만 일단 하자.
		List<String> tagList = Utils.tagParser(tags);
		for (String tag : tagList) {
			Tag t = new Tag();
			t.setName(tag);
			//얘는 영속화가 되어있거나 DB에 저장되어있어야한다
			//why? tag에는 image테이블을 건드릴 수있는 권한이 없기 때문에
			t.setImage(image);
			
			mTagRepo.save(t);
			//이렇게 해주면 image를 리턴 받을 때 tags를 들고 있는 image를 리턴받을 수 있다
			//다시 셀렉트 하는 것 보다 이렇게 하는게 효율이 훨씬좋다
			//image.getTags().add(t);
		}
		return "redirect:/";
	}
	

	
}
