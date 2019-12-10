package com.cos.insta.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Data
@Entity
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String location; // 사진 찍은 위치
	private String caption; // 사진 설명
	private String postImage; // 사진 파일 경로+이름

	
	// 한명의 유저가 여러개의 이미지를 가질 수 있고 하나의 이미지는 하나의 유저만 작성할 수 있다
	// 리스트가 아니므로 기본전략 Eager
	@ManyToOne 
	@JoinColumn(name="userId")// 기본은 user_Id라서 내가원하는 이름으로 변경
	//이미지를 불러올 때 유저 password는 노출되면 안됨, image를 한번더 불러오는 걸 막아줌
	@JsonIgnoreProperties({"password", "images"})
	private User user; // 누가 찍은 사진인지
	
	//(1) Tag <List>
	@OneToMany(mappedBy = "image")
	@JsonManagedReference
	private List<Tag> tags = new ArrayList<>();
	
	//(2) Like <List>
	//하나의 이미지는 여러개의 좋아요 들고 있을 수 있다
	@OneToMany(mappedBy = "image")//주인님을 넣어줘야함
	private List<Likes> likes = new ArrayList<>();
	
	@Transient //DB에 영향을 미치지 않는다.
	private int likeCount;//피드페이지에서 이미지를 불러올 때 보일 좋아요 개수
	
	//(3) 댓글 하고싶으면 직접 추가해
	@CreationTimestamp
	private Timestamp createDate;
	@CreationTimestamp
	private Timestamp updateDate;
	
}
