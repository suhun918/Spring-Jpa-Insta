package com.cos.insta.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.insta.model.Likes;
import com.cos.insta.model.User;
import com.cos.insta.repository.LikesRepository;
import com.cos.insta.security.MyUserDetails;

@RestController
public class LikesController {
	
	@Autowired
	private LikesRepository mLikesRepo;
	
	@GetMapping("/like/notification")
	public List<Likes> likeNotification
	(
			@AuthenticationPrincipal MyUserDetails userDetails
	)
	{
		User principal = userDetails.getUser();
		List<Likes> likesList = mLikesRepo.findLikeNotification(principal.getId());
		return likesList;
	}
}