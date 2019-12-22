<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<sec:authorize access="isAuthenticated()">
	<sec:authentication property="principal" var="principal"/>
</sec:authorize>
    
<nav class="navigation">
	<a href="/"> <img src="/images/logo.png" />
	</a> <input type="text" placeholder="Search">
	<div class="navigation__links">
		<a href="/image/explore" class="navigation__link"> <i class="fa fa-compass"></i>
		</a> <a href="#" class="navigation__link like_popup"> <i class="fa fa-heart-o"></i>
		</a> <a href="/user/${principal.user.id}" class="navigation__link"> <i class="fa fa-user-o"></i>
		</a>
	</div>
</nav>
<!--좋아요 Modal 시작 -->
<div id="modal">
	<div id="like_popup_list">

		<div class="like_popup_item">
			<img src="/images/avatar.jpg" alt="코스사진">
			<p>
				<a href="#">누가 누구의 사진을 좋아합니다.</a>
			</p>
		</div>

		<div class="like_popup_item">
			<img src="/images/avatar.jpg" alt="코스사진">
			<p>
				<a href="#">누가 누구의 사진을 좋아합니다.</a>
			</p>
		</div>

		<div class="like_popup_close">
			<button type="button">닫기</button>
		</div>
	</div>

</div>
<!--좋아요 Modal 끝 -->