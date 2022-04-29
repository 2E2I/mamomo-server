package com.hsu.mamomo.controller;

import static com.hsu.mamomo.controller.exception.ErrorCode.UNAUTHORIZED;

import com.hsu.mamomo.controller.exception.CustomException;
import com.hsu.mamomo.dto.HeartDto;
import com.hsu.mamomo.service.HeartService;
import java.io.IOException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/heart")
public class HeartController {

    private final HeartService heartService;

    @PreAuthorize("hasAnyRole('USER')")
    @PostMapping
    public ResponseEntity<HeartDto> heart(@RequestBody @Valid HeartDto heartDto,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization)
            throws IOException {

        if (authorization == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        heartService.heart(heartDto, authorization.substring(7));

        return new ResponseEntity<>(heartDto, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('USER')")
    @DeleteMapping
    public ResponseEntity<HeartDto> unHeart(@RequestBody @Valid HeartDto heartDto,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization)
            throws IOException {

        if (authorization == null) {
            throw new CustomException(UNAUTHORIZED);
        }

        heartService.unHeart(heartDto, authorization.substring(7));

        return new ResponseEntity<>(heartDto, HttpStatus.OK);
    }

}
