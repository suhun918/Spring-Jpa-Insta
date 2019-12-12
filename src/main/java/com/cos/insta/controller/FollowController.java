package com.cos.insta.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
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
		return "ok";
	}

	@GetMapping("/follow/follower/{id}")
	public String followFollower(@PathVariable int id) {
		// 팔로워 리스트
		return "follow/follow";
	}

	@GetMapping("/follow/follow/{id}")
	public String followFollow(@PathVariable int id) {
		// 팔로우 리스트
		return "follow/follow";
	}
}
