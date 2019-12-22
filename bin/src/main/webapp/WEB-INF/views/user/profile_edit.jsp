<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta http-equiv="X-UA-Compatible" content="ie=edge">
  <title>Edit Profile | Instagram</title>
  <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css"
    rel="stylesheet">
  <link rel="shortcut icon" href="/images/favicon.ico">
  <link rel="stylesheet" href="/css/styles.css">

</head>
<body>
<%@ include file="../include/nav.jsp" %>


  <main id="edit-profile">
    <div class="edit-profile__container u-default-box">
      
      <header class="edit-profile__header">
        <div class="fucker-container">
          <img src="/upload/${principal.user.profileImage}" onerror="this.onerror=null; this.src='/images/avatar.jpg'"/>
        </div>
        <!-- master comments -->
        <h1 class="edit-profile__username">${principal.user.username}</h1>
      </header>

      <form:form action="/user/editProc" method="put" class="edit-profile__form">
      	<input type="hidden" name="id" value="${principal.user.id}"/>
        <div class="edit-profile__row">
          <label class="edit-profile__label" for="name">Name</label>
          <input id="name" name="name" type="text" value="${principal.user.name }">
        </div>
        <div class="edit-profile__row">
          <label class="edit-profile__label" for="username">Username</label>
          <input id="username" name="username" type="text" value="${principal.user.username}">
        </div>
        <div class="edit-profile__row">
          <label class="edit-profile__label" for="website">Website</label>
          <input id="website" name="website" type="url" value="${principal.user.website}">
        </div>
        <div class="edit-profile__row">
          <label class="edit-profile__label" for="bio">Bio</label>
          <textarea id="bio" name="bio">${principal.user.bio}</textarea>
        </div>
        <div class="edit-profile__row">
          <label class="edit-profile__label" for="email">Email</label>
          <input id="email" name="email" type="email" value="${principal.user.email}">
        </div>
        <div class="edit-profile__row">
          <label class="edit-profile__label" for="phone-number">Phone Number</label>
          <input id="phone-number" name="phone" type="text" value="${principal.user.phone}">
        </div>
        <div class="edit-profile__row">
          <label class="edit-profile__label" for="gender">Gender</label>
		  <input id="gender" name="gender" type="text" value="${principal.user.gender}">
        </div>
        <div class="edit-profile__row">
          <span></span>
          <input style="background-color: #3897F0;" type="submit">
        </div>
      </form:form>

    </div>
  </main>
<%@ include file="../include/footer.jsp" %>
</body>
</html>
    