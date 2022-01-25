package com.ssafy.tourtogether.api.request;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

/**
 * 유저 닉네임 변경 API ([PATCH] /user/updateNickname/{userId}) 요청에 필요한 리퀘스트 바디 정의.
 */
@Getter
@Setter
@ApiModel("UserUpdateNicknamePatchRequest")
public class UserUpdateNicknamePatchReq {
	// TODO updateNickname req를 통해 들어오는 data에 따라 추가
//	@ApiModelProperty(name="유저 ID", example="ssafy_web")
//	String id;
//	@ApiModelProperty(name="유저 Password", example="your_password")
//	String password;
//	@ApiModelProperty(name="유저 name", example="your_name")
//	String name;
//	@ApiModelProperty(name="유저 department", example="your_department")
//	String department;
//	@ApiModelProperty(name="유저 position", example="your_position")
//	String position;
}
