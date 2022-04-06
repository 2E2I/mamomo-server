package com.hsu.mamomo.controller;

import com.hsu.mamomo.dto.HeartDto;
import com.hsu.mamomo.service.HeartService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/heart")
public class HeartController {

    private final HeartService heartService;

    @PostMapping
    public ResponseEntity<HeartDto> heart(@RequestBody @Valid HeartDto heartDto) {
        heartService.heart(heartDto);
        return new ResponseEntity<>(heartDto, HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<HeartDto> unHeart(@RequestBody @Valid HeartDto heartDto) {
        heartService.unHeart(heartDto);
        return new ResponseEntity<>(heartDto, HttpStatus.OK);
    }

}
