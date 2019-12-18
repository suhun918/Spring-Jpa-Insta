package com.cos.insta.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cos.insta.model.Image;
import com.cos.insta.model.User;
import com.cos.insta.repository.FollowRepository;
import com.cos.insta.repository.LikesRepository;
import com.cos.insta.repository.UserRepository;
import com.cos.insta.security.MyUserDetails;

@Controller
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder PasswordEncoder;

	@Autowired
	private UserRepository mUserRepo;
	
	@Autowired
	private FollowRepository mFollowRepo;
	
	@Autowired
	private LikesRepository mLikesRepo;
	
	@Value("${file.path}")
	private String fileRealPath;
	
	
	@PostMapping("user/profileUpload")
	public String userProfileUpload
	(
			@RequestParam("profileImage") MultipartFile file,
			@AuthenticationPrincipal MyUserDetails userDetails
	)throws IOException
	{
		//업로드 후에 회원 자기 페이지로 돌아가는 용도
		User principal = userDetails.getUser();
		
		//파일 처리(파일을 write해서 쓰고 해당 경로만 디비에 저장)
		UUID uuid = UUID.randomUUID();
		String uuidFilename = uuid+"_"+file.getOriginalFilename();
		Path filePath = Paths.get(fileRealPath+uuidFilename);
		Files.write(filePath, file.getBytes());		
		
		Optional<User> oUser = mUserRepo.findById(principal.getId());
		
		User user = oUser.get();
		user.setProfileImage(uuidFilename);
		mUserRepo.save(user);
		return "redirect:/user/"+principal.getId();
	}
	
	
	
	@GetMapping("/auth/join")
	public String join() {
		return "auth/join";
	}
	
	@PostMapping("/auth/joinProc")
	public String joinProc(User user) {
		String rawPassword = user.getPassword();
		String encPassword = PasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		mUserRepo.save(user);
		return "redirect:/auth/login";
	}
	
	@GetMapping("/auth/login")
	public String login() {
		return "auth/login";
	}
	
	@GetMapping("/user/{id}")
	public String profile(
			@PathVariable int id,
			@AuthenticationPrincipal MyUserDetails userDetails,
			Model model)
	{
		/**
		 *  1. imageCount
		 *  2. followerCount
		 *  3. followingCount
		 *  4. User오브젝트 (Image(likeCount) 컬렉션)
		 *  5. follow 유무(1 팔로우 / 1이아니면 언팔로우)
		 */
		// 4번 jsp파일에서 쓰려고 임시로 만듬 (수정해야함)
		Optional<User> oUser = mUserRepo.findById(id);
		User user = oUser.get();
		
		//1번 imageCount
		int imageCount = user.getImages().size();
		model.addAttribute("imageCount", imageCount);
		
		//2번 followCount(select count(*) from follow where fromUserId = 1)
		int followCount = mFollowRepo.countByFromUserId(user.getId());
		model.addAttribute("followCount", followCount);
		//3번 followerCount(select count(*) from follower where toUserId =1)
		int followerCount = mFollowRepo.countByToUserId(user.getId());
		model.addAttribute("followerCount", followerCount);
		
		//4. likeCount
		for (Image item : user.getImages()) {
			int likeCount = mLikesRepo.countByImageId(item.getId());
			item.setLikeCount(likeCount);
		}
		
		model.addAttribute("user", user);
		//오늘은 follow 유무만 한다.
		//접근주체
		User principal = userDetails.getUser();
		
		int followCheck = mFollowRepo.countByFromUserIdAndToUserId(principal.getId(), id);
		model.addAttribute("followCheck", followCheck);
		
		return "user/profile";
	}
	
	@GetMapping("/user/edit")
	public String userEdit() {

		return "user/profile_edit";
	}
	
	@PutMapping("user/editProc")
	public String  userEditProc(User requestUser,
			@AuthenticationPrincipal MyUserDetails userDetails) {
		
		//영속화
		Optional<User> oUser = mUserRepo.findById(userDetails.getUser().getId());
		User user = oUser.get();
		
		//값 변경
		user.setName(requestUser.getName());
		user.setUsername(requestUser.getUsername());
		user.setWebsite(requestUser.getWebsite());
		user.setBio(requestUser.getBio());
		user.setEmail(requestUser.getEmail());
		user.setPhone(requestUser.getPhone());
		user.setGender(requestUser.getGender());
		
		// 다시 영속화 및 flush
		mUserRepo.save(user);

		return "redirect:/user/"+userDetails.getUser().getId();
	}
	
}
