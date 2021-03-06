package com.ssafy.tourtogether.api.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 보드 ID로 보드 불러오기 API ([GET] /board/searchByBoardID) 요청에 필요한 리퀘스트 바디 정의.
 */

@Getter
@Setter
@ApiModel("BoardSearchByBoardIdGetReq")
public class BoardSearchByBoardIdPostReq {
	@ApiModelProperty(name = "보드 아이디", example = "보드 아이디")
	int boardId;
}
