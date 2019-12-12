package com.cos.insta.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.cos.insta.security.MyUserDetails;

@Controller
public class ImageController {
	
	@GetMapping({"/", "/image/feed"})
	public String imageFeed(
			//얘가 세션과 같은 역할을 하게 된다
			@AuthenticationPrincipal MyUserDetails userDetails) {
		return "image/feed";
	}
	
	@GetMapping({"/image/explore"})
	public String explore() {
		return "image/explore";
	}
}
