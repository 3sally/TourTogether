package com.ssafy.tourtogether.api.response;

import com.ssafy.tourtogether.common.model.response.BaseResponseBody;
import com.ssafy.tourtogether.db.entity.User;

import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 유저 로그인 API ([POST] user/login) 요청에 대한 응답값 정의.
 */

@Getter
@Setter
@ApiModel("UserLoginPostResponse")
public class UserLoginPostRes extends BaseResponseBody{
	// TODO 토큰 형식 수정
//	@ApiModelProperty(name="JWT 인증 토큰", example="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN...")
//	String accessToken;
	
	String userNickName;
	String userProfileImage;	
	
	public static UserLoginPostRes of(Integer statusCode, String message, User user) {
		UserLoginPostRes res = new UserLoginPostRes();
		res.setStatusCode(statusCode);
		res.setMessage(message);
//		res.setAccessToken(accessToken);
		res.setUserNickName(user.getUserNickname());
		res.setUserProfileImage(user.getUserProfileImage());
		return res;
	}
}
