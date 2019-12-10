package com.cos.insta.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Data // lombok
@Entity // JPA -> ORM
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;//시퀀스
	//String이면 기본 256byte 들어간다
	private String username;// 사용자 아이디
	private String password;// 암호화된 패스워드
	private String name; // 사용자 실제 이름
	private String website;// 홈페이지 주소
	private String bio; // 자기소개
	private String email;
	private String phone;
	private String gender;
	private String profileImage; //프로필사진 경로+이름
	
	// (1) findById() 때만 동작
	// (2) findByUserInfo()때는 제외 (쿼리문 만들어서)
	@OneToMany(mappedBy="user")//리스트라서 기본전력 Lazy
	//순환참조 막아줌
	@JsonIgnoreProperties({"user", "tags", "likes"})
	private List<Image> images = new ArrayList<>();
	//리스트 일 때는 new를 미리 해주는 것이 좋다.
	
	
	@CreationTimestamp // 자동으로 현재시간이 세팅
	private Timestamp createDate;
	@CreationTimestamp // 자동으로 현재시간이 세팅
	private Timestamp updateDate;
}
