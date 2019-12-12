package com.cos.insta.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@Entity
public class Follow {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;//시퀀스
	
	//중간 테이블 생성됨.
	//fromUser가 toUser를 following함
	//toUser를 fromUser가 follower함.
	@ManyToOne
	@JoinColumn(name = "fromUserId")
	@JsonIgnoreProperties({"images"})
	private User fromUser;
	
	@ManyToOne
	@JoinColumn(name = "toUserId")
	@JsonIgnoreProperties({"images"})
	private User toUser;
	
	//팔로우 팔로워 창에서 로그인 한 사용자의 팔로우상태체크
	@Transient//DB에 안들어가게 함
	private boolean followState;
	
	@CreationTimestamp // 자동으로 현재시간이 세팅
	private Timestamp createDate;
	@CreationTimestamp // 자동으로 현재시간이 세팅
	private Timestamp updateDate; 

}
