package com.cos.insta.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.cos.insta.model.User;
import com.cos.insta.repository.FollowRepository;
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
		model.addAttribute("user", user);
		//오늘은 follow 유무만 한다.
		//접근주체
		User principal = userDetails.getUser();
		
		int followCheck = mFollowRepo.countByFromUserIdAndToUserId(principal.getId(), id);
		model.addAttribute("followCheck", followCheck);
		
		return "user/profile";
	}
	
	@GetMapping("/user/edit/{id}")
	public String userEdit(@PathVariable int id) {
		
		//해당 ID로 select 하기
		// findByUserInfo() 사용 (만들어야 함)
		return "user/profile_edit";
	}
	
}
