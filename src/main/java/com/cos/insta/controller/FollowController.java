package com.cos.insta.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.insta.model.Follow;
import com.cos.insta.model.User;
import com.cos.insta.repository.FollowRepository;
import com.cos.insta.repository.UserRepository;
import com.cos.insta.security.MyUserDetails;

@Controller
public class FollowController {

	@Autowired
	private UserRepository mUserRepo;
	@Autowired
	private FollowRepository mFollowRepo;

	@PostMapping("/follow/{id}")
	public @ResponseBody String follow(
			// 누가 : 로그인 한 유저
			@AuthenticationPrincipal MyUserDetails userDetails,
			// 누구에게 : {id}값에 들어간 유저를 팔로우함
			@PathVariable int id) {
		User fromUser = userDetails.getUser();
		Optional<User> oToUser = mUserRepo.findById(id);
		User toUser = oToUser.get();
		
		Follow follow = new Follow();
		follow.setFromUser(fromUser);
		follow.setToUser(toUser);

		mFollowRepo.save(follow);

		return "ok";
	}

	@DeleteMapping("/follow/{id}")
	public @ResponseBody String unFollow(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable int id) {
		User fromUser = userDetails.getUser();
		Optional<User> oToUser = mUserRepo.findById(id);
		User toUser = oToUser.get();

		mFollowRepo.deleteByFromUserIdAndToUserId(fromUser.getId(), toUser.getId());

		List<Follow> follows = mFollowRepo.findAll();
		return "ok"; // ResponseEntity로 수정하자고 썜한테 말하자
	}

	@GetMapping("/follow/follower/{id}")
	public String followFollower(
			@PathVariable int id,
			@AuthenticationPrincipal MyUserDetails userDetails,
			Model model) {
		// 팔로워 리스트
		List<Follow> followers =mFollowRepo.findByToUserId(id);
		
		List<Follow> principalFollows = mFollowRepo.findByFromUserId(userDetails.getUser().getId());
		
		//follow가 가진 id와 principalfollows가 가진 id 를 비교하여
		//같으면 true 아니면 false
		for (Follow f1 : followers) {//3번돈다
			for (Follow f2 : principalFollows) {
				if(f1.getFromUser().getId() == f2.getToUser().getId()) {
					f1.setFollowState(true);
				}
			}
		}

		model.addAttribute("followers", followers);
		return "follow/follower";
	}

	@GetMapping("/follow/follow/{id}")
	public String followFollow(
			@PathVariable int id,
			@AuthenticationPrincipal MyUserDetails userDetails,
			Model model) {
		// 팔로우 리스트
		// FromUserId인 이유 id값을 넣고 id값으로부터 팔로우를 받고있는 사람들을 출력해야하니까
		// 이건 목록에 뜨는 유저 사진과 이름
		// 팔로우리스트(ssar :3) 1, 2, 4
		List<Follow> follows = mFollowRepo.findByFromUserId(id);

		// 이건 로그인 한 사용자의 팔로우 정보로 버튼 구현위해서 사용
		// 팔로우리스트(cos :1)2, 3
		List<Follow> principalFollows = mFollowRepo.findByFromUserId(userDetails.getUser().getId());
		
		//follow가 가진 id와 principalfollows가 가진 id 를 비교하여
		//같으면 true 아니면 false
		for (Follow f1 : follows) {//3번돈다
			for (Follow f2 : principalFollows) {
				if(f1.getToUser().getId() == f2.getToUser().getId()) {
					f1.setFollowState(true);
				}
			}
		}
		//웹에서는 상관 없지만 다른 것에서도 사용하기 위해서는 모델을 하나만넘기는게 좋다.
		model.addAttribute("follows", follows);
		return "follow/follow";
	}
}
