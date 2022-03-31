package com.hsu.mamomo.controller;

import static com.hsu.mamomo.controller.exception.ErrorCode.ABSENCE_OF_ESSENTIAL_FIELD;
import static com.hsu.mamomo.controller.exception.ErrorCode.INVALID_FIELD;
import static com.hsu.mamomo.controller.exception.ErrorCode.TOPIC_NOT_FOUND;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.controller.exception.ErrorResponse;
import com.hsu.mamomo.dto.LoginDto;
import com.hsu.mamomo.dto.TokenDto;
import com.hsu.mamomo.dto.UserDto;
import com.hsu.mamomo.dto.UserInfoDto;
import com.hsu.mamomo.jwt.JwtTokenProvider;
import com.hsu.mamomo.service.UserService;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     *
     * @param userDto
     * @return
     */
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody @Valid UserDto userDto) {
        return userService.signUp(userDto);
    }

    /**
     * 로그인 -> jwt 토큰 발급
     *
     * @param loginDto
     * @return
     */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> signUp(@RequestBody LoginDto loginDto) {
        return userService.authenticate(loginDto);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @GetMapping("/{email}")
    public UserInfoDto getUserInfo(@PathVariable String email,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION) String authorization) {
        log.info("{} 의 정보를 찾습니다 ..", email);
        log.info("getUserPk() = {}", jwtTokenProvider.getUserPk(authorization.substring(7)));
        return userService.getUserInfo();
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {
        return userService.deleteUser(email);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {
        ObjectError objectError = Objects.requireNonNull(
                exception.getBindingResult().getAllErrors().stream().findFirst()
                        .orElse(null));
        log.error("handleValidationException throw MethodArgumentNotValidException : {}",
                INVALID_FIELD);
        return ErrorResponse.toResponseEntity(INVALID_FIELD, objectError.getDefaultMessage());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadableException() {
        log.error("handleNotReadableException throw Exception : {}", ABSENCE_OF_ESSENTIAL_FIELD);
        return ErrorResponse.toResponseEntity(ABSENCE_OF_ESSENTIAL_FIELD);
    }

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

}
