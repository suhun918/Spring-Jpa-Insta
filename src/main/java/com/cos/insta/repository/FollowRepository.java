package com.cos.insta.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.insta.model.Follow;

public interface FollowRepository extends JpaRepository<Follow, Integer> {

	// unFollow
	// = DELETE FROM follow WHERE fromUserId = ? and toUserId =?
	@Transactional
	int deleteByFromUserIdAndToUserId(int fromUserId, int toUserId);

	// follow 유무체크 
	// int는 작동된 행의 개수를 리턴하는 것 -> 1은 한개가작동 0은 하나도작동x -1은 오류
	//@Query(value = "SELECT count(*) FROM follow WHERE fromUserId=?1 AND toUserId =?2", nativeQuery = true) 
	int countByFromUserIdAndToUserId(int fromUserId, int toUserId);
	
	//팔로우 리스트
	//to user를 뽑아냄
	List<Follow> findByFromUserId(int fromUserId);
	
	//팔로워 리스트
	//from user를 뽑아냄
	List<Follow> findByToUserId(int toUserId);
}
