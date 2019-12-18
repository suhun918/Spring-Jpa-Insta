package com.cos.insta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.cos.insta.model.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	User findByUsername(String username);
	
	//kakao+id 셀렉트하는거
	User findByProviderAndProviderId(String provider, String providerId);
	
}
