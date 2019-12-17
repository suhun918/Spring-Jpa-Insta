package com.cos.insta.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Likes {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	//한명의 유저 여러개의 좋아요, 하나의 좋아요는 하나의 유저
	@ManyToOne //양쪽으로 관계를 그려서 항상 생각하고 써라
	@JoinColumn(name = "userId")
	//좋아요를 눌렀을 때 필요한 profileImage, userId, username을 제외하고 제외해준다.
	@JsonIgnoreProperties({"images", "password", "name", "website", "bio", "email", "phone", "gender", "updateDate", "createDate"})
	private User user;
	
	//한장의 이미지에 여러개의 좋아요박힘, 하나의 좋아요는 하나의 이미지에만 박힘
	@ManyToOne
	@JoinColumn(name = "imageId")
	@JsonIgnoreProperties({"tags", "user", "likes"})
	private Image image;
	
	
	@CreationTimestamp 
	private Timestamp createDate;
	@CreationTimestamp 
	private Timestamp updateDate;
}
