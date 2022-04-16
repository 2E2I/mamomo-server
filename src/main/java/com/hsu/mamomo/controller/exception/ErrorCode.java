package com.hsu.mamomo.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    WRONG_OBJECT(HttpStatus.BAD_REQUEST, "객체 변환이 되지 않습니다. 옳은 형식을 보내주세요."),
    INVALID_FIELD(HttpStatus.BAD_REQUEST, "인자 형식이 맞지 않습니다."),
    INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 JWT 토큰입니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    HEART_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 좋아요 정보를 찾을 수 없습니다."),
    CAMPAIGN_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 캠페인 정보를 찾을 수 없습니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    ALREADY_HEARTED(HttpStatus.CONFLICT, "이미 좋아요 된 캠페인 입니다.");


    private final HttpStatus httpStatus;
    private final String detail;

}
