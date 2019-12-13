package com.cos.insta.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.insta.model.Image;
import com.cos.insta.model.Likes;
import com.cos.insta.model.User;
import com.cos.insta.repository.ImageRepository;
import com.cos.insta.repository.UserRepository;

@Controller
public class TestController {

	@Autowired
	private UserRepository mUserRepository;

	@Autowired
	private ImageRepository mImageRepo;

	@GetMapping("/test/user/{id}")
	public @ResponseBody User testUser(@PathVariable int id) {
		Optional<User> oUser = mUserRepository.findById(id);
		User user = oUser.get();
		return user;
	}

	@GetMapping("/test/home")
	public String testHome() {
		return "home";
	}

	@GetMapping("/test/user")
	public @ResponseBody User getUser() {
		User user = new User();
		user.setId(1);
		user.setUsername("cos");
		user.setName("홍길동");
		user.setEmail("cos@nate.com");
		user.setProfileImage("my.jpg");

		Image img1 = new Image();
		img1.setId(1);
		img1.setCaption("음식 사진");
		img1.setLocation("food.jpg");
		img1.setLocation("부산 서면");
		img1.setUser(user);

		Image img2 = new Image();
		img2.setId(2);
		img2.setCaption("장난감 사진");
		img2.setLocation("game.jpg");
		img2.setLocation("서울 강남");
		img2.setUser(user);

		List<Image> images = new ArrayList<>();
		images.add(img1);
		images.add(img2);
		user.setImages(images);

		return user;
	}

	@GetMapping("/test/image")
	public @ResponseBody Image getImage() {
		User user = new User();
		user.setId(1);
		user.setUsername("cos");
		user.setName("홍길동");
		user.setEmail("cos@nate.com");
		user.setProfileImage("my.jpg");

		Image img1 = new Image();
		img1.setId(1);
		img1.setCaption("음식 사진");
		img1.setLocation("food.jpg");
		img1.setLocation("부산 서면");
		img1.setUser(user);

		return img1;
	}

	@GetMapping("/test/images")
	public @ResponseBody List<Image> getImages() {
		User user = new User();
		user.setId(1);
		user.setUsername("cos");
		user.setName("홍길동");
		user.setEmail("cos@nate.com");
		user.setProfileImage("my.jpg");

		Image img1 = new Image();
		img1.setId(1);
		img1.setCaption("음식 사진");
		img1.setLocation("food.jpg");
		img1.setLocation("부산 서면");
		img1.setUser(user);

		Image img2 = new Image();
		img2.setId(2);
		img2.setCaption("장난감 사진");
		img2.setLocation("game.jpg");
		img2.setLocation("서울 강남");
		img2.setUser(user);

		List<Image> images = new ArrayList<>();
		images.add(img1);
		images.add(img2);
		user.setImages(images);

		return images;
	}

	@GetMapping("/test/like")
	public @ResponseBody Likes getLike() {
		User user = new User();
		user.setId(1);
		user.setUsername("cos");
		user.setName("홍길동");
		user.setEmail("cos@nate.com");
		user.setProfileImage("my.jpg");

		Image img1 = new Image();
		img1.setId(1);
		img1.setCaption("음식 사진");
		img1.setLocation("food.jpg");
		img1.setLocation("부산 서면");
		img1.setUser(user);

		Likes like = new Likes();
		like.setId(1);
		like.setUser(user);
		like.setImage(img1);

		return like;
	}

	@GetMapping("/test/login")
	public String testLogin() {
		return "auth/login";
	}

	@GetMapping("/test/join")
	public String testJoin() {
		return "auth/join";
	}

	@GetMapping("/test/profile")
	public String testProfile() {
		return "user/profile";
	}

	@GetMapping("/test/profileEdit")
	public String testprofileEdit() {
		return "user/profile_edit";
	}

	@GetMapping("/test/feed")
	public String testfeed() {
		return "image/feed";
	}

	@GetMapping("/test/imageUpload")
	public String testimageUpload() {
		return "image/image_upload";
	}

	// sort = id, desc //sort = caption, asc //size = 1 //page = 3 (페이지는 시작 번호가 0)

	@GetMapping("/test/image/feed")
	public @ResponseBody Page<Image> testImageFeed(
			@PageableDefault(size = 2, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		int userId = 1;
		Page<Image> images = mImageRepo.findImage(userId, pageable);
		return images;
	}

}
