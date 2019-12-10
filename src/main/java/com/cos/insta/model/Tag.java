package com.cos.insta.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

@Data
@Entity
//어떤 이미지에 달려있는지만 알면 된다.
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;// 시퀀스
	private String name; // 태그이름

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "imageId")
	@JsonBackReference
	private Image image;

	@CreationTimestamp
	private Timestamp createDate;

}
