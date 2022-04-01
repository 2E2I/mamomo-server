package com.hsu.mamomo.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 400 BAD_REQUEST : 잘못된 요청 */
    ABSENCE_OF_ESSENTIAL_FIELD(HttpStatus.BAD_REQUEST, "필수 인자가 존재하지 않습니다."),
    INVALID_FIELD(HttpStatus.BAD_REQUEST, "인자 형식이 맞지 않습니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저 정보를 찾을 수 없습니다"),
    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리를 찾을 수 없습니다."),
    HEART_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 좋아요 정보를 찾을 수 없어 좋아요 취소가 불가합니다."),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    ALREADY_HEARTED(HttpStatus.CONFLICT, "이미 좋아요 된 캠페인 입니다.");


    private final HttpStatus httpStatus;
    private final String detail;

}
