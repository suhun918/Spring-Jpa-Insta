package com.cos.insta.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.cos.insta.model.KakaoProfile;
import com.cos.insta.model.OAuth2Token;
import com.cos.insta.model.User;
import com.cos.insta.repository.UserRepository;
import com.cos.insta.security.MyUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
public class OAuth2Controller {
	
	@Autowired
	private UserRepository mUserRepo;
	
	@Autowired
	private MyUserDetailsService mMyUserDetailsService;
	
	private String clientId = "11d796ccb5cecdcab2f62e18d112626a";
	private String redirectUri = "http://localhost:8080/auth/kakao/callback";
	
	@GetMapping("/auth/kakao/login")
	public String kakaoLogin() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("https://kauth.kakao.com/oauth/authorize?");
		sb.append("client_id="+clientId+"&");
		sb.append("redirect_uri="+redirectUri+"&");
		sb.append("response_type=code");
		
		return "redirect:"+sb.toString();
	}
	
	@PostMapping("/auth/kakao/joinProc")
	public String kakaoJoinProc(User user,
			HttpSession session) {
		// name, email, provider, providerId
		
		//조인넘어오기전에 세션으로 받아온 것
		String providerId = (String)session.getAttribute("providerId");
		user.setProvider("kakao");
		user.setProviderId(providerId);
		
		mUserRepo.save(user);
		
		//로그인 처리
		// 해당 아이디로 로그인을 위해 강제 세션 부여
         UserDetails userDetail =
        		 mMyUserDetailsService.loadUserByUsername(user.getUsername());
          Authentication authentication = 
                new UsernamePasswordAuthenticationToken(userDetail, userDetail.getPassword(), userDetail.getAuthorities());
          
          SecurityContext securityContext = SecurityContextHolder.getContext();
          
          securityContext.setAuthentication(authentication);
          
          session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
		
		
		return "redirect:/";
	}
	
	//accessToken 역할 = 카카오에 정보를 받기 위한 key(10분~1시간)
	//refreshToken -> accessToken을 받을 수있음.(10일~30일 꼭 DB에 저장해라)
	//우리는 여기서 액세스/리프레스 토큰 버려버릴 것이다.
	@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(
			String code,
			HttpSession session) {
		
		//토큰받기
		//HttpUrlConnection, Retrofit2, okHttp, RestTemplate
		RestTemplate rt = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
		
		MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
		parameters.add("grant_type", "authorization_code");
		parameters.add("client_id", clientId);
		parameters.add("redirect_uri", redirectUri);
		parameters.add("code", code);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);
		
		ResponseEntity response = rt.exchange(
				"https://kauth.kakao.com/oauth/token",
				HttpMethod.POST,
				request,
				String.class);
		
		//jakson이 제공하는 gson이랑 같은역할하는 것
		ObjectMapper objectMapper = new ObjectMapper();
		
		OAuth2Token oToken =null;
		try {
			oToken = objectMapper.readValue(response.getBody().toString(), OAuth2Token.class );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(oToken.getAccess_token());
		
		//회원프로필조회 끝(인증 끝)
		
		RestTemplate rt2 = new RestTemplate();
		
		HttpHeaders headers2 = new HttpHeaders();
		//위에서 받은 엑세스토큰
		headers.add("Authorization", "Bearer "+oToken.getAccess_token());
		headers.add("Content-type", "application/x-www-form-urlencoded; charset=utf-8");
		
		HttpEntity request2 = new HttpEntity(headers);
		
		ResponseEntity response2 = rt2.exchange(
				"https://kapi.kakao.com/v2/user/me",
				HttpMethod.POST,
				request2,
				String.class);
		
		
		
		// 우리가 필요한 id값만 뺴오는 작업
		ObjectMapper objectMapper2 = new ObjectMapper();
		
		KakaoProfile kakaoProfile =null;
		try {
			kakaoProfile = objectMapper2.readValue(response2.getBody().toString(), KakaoProfile.class );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(kakaoProfile.getId());
		
		//가입자, 비가입자 확인 처리
		User user = mUserRepo.findByProviderAndProviderId("kakao", kakaoProfile.getId());
		
		if(user==null) {
			System.out.println("미가입자입니다.");
			//회원가입창으로 넘어가서 email,name -> 로그인 처리
			//회원가입창으로 들고갈 providerId값
			session.setAttribute("providerId", kakaoProfile.getId());
			return "auth/kakaoJoin";
		}else {
			System.out.println("가입된 유저입니다. 로그인 처리합니다.");
			//로그인 처리
			// 해당 아이디로 로그인을 위해 강제 세션 부여
	         UserDetails userDetail =
	        		 mMyUserDetailsService.loadUserByUsername(user.getUsername());
	          Authentication authentication = 
	                new UsernamePasswordAuthenticationToken(userDetail, userDetail.getPassword(), userDetail.getAuthorities());
	          
	          SecurityContext securityContext = SecurityContextHolder.getContext();
	          
	          securityContext.setAuthentication(authentication);
	          
	          session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
	          return "redirect:/";
		}
		
		
	}
}
