package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.dto.TextDto;
import com.hsu.mamomo.dto.TextMiningResultDto;
import com.hsu.mamomo.service.factory.ElasticTextMiningFactory;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Service
public class TextMiningService {

    @Value("http://34.64.234.137:5000")
    private String flaskApiUrl;

    private final ElasticTextMiningFactory textMiningFactory;

    public CampaignDto requestTextMining(Pageable pageable, TextDto textDto) {
        URI uri = UriComponentsBuilder
                .fromUriString(flaskApiUrl)
                .path("/textMining")
                .build()
                .toUri();

        RequestEntity<TextDto> requestEntity = RequestEntity
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(textDto);

        RestTemplate restTemplate = new RestTemplate();
        TextMiningResultDto textMiningResultDto = restTemplate.exchange(requestEntity, TextMiningResultDto.class).getBody();
        return getCampaignsByTextMining(textMiningResultDto, pageable);
    }

    public CampaignDto getCampaignsByTextMining(TextMiningResultDto textMiningResultDto, Pageable pageable) {
        return new CampaignDto(findCampaignsByTextMining(textMiningResultDto, pageable));
    }

    public Page<Campaign> findCampaignsByTextMining(TextMiningResultDto textMiningResultDto, Pageable pageable) {
        return textMiningFactory.getCampaignSearchList(textMiningFactory.createQuery(textMiningResultDto,pageable));
    }
}
