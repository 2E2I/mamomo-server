package com.hsu.mamomo.service;

import com.hsu.mamomo.domain.Campaign;
import com.hsu.mamomo.dto.CampaignDto;
import com.hsu.mamomo.dto.TextDto;
import com.hsu.mamomo.dto.TextMiningCampaignDto;
import com.hsu.mamomo.dto.TextMiningResultDto;
import com.hsu.mamomo.jwt.LoginAuthenticationUtil;
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

    private final ElasticTextMiningFactory textMiningFactory;
    private final LoginAuthenticationUtil loginAuthenticationUtil;

    private CampaignDto campaignDto;

    @Value("http://34.64.234.137:5000")
    private String FLASK_API_URL;

    /*
     * Flask 서버로 텍스트 마이닝 요청
     * requestBody - TextDto: {text: 텍스트 마이닝할 텍스트}
     * responseType - TextMiningResultDto: {keyword: 텍스트 키워드, value: 텍스트 마이닝 결과 값} 리스트
     * */
    public TextMiningCampaignDto requestTextMining(Pageable pageable, String authorization,
            TextDto textDto) {
        URI uri = UriComponentsBuilder
                .fromUriString(FLASK_API_URL)
                .path("/textMining")
                .build()
                .toUri();

        RequestEntity<TextDto> requestEntity = RequestEntity
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(textDto);

        RestTemplate restTemplate = new RestTemplate();
        TextMiningResultDto textMiningResultDto = restTemplate.exchange(requestEntity,
                TextMiningResultDto.class).getBody();

        campaignDto = getCampaignsByTextMining(textMiningResultDto, pageable);
        loginAuthenticationUtil.checkAuthAndAddHeartInfo(authorization, campaignDto);

        TextMiningCampaignDto textMiningCampaignDto = TextMiningCampaignDto.builder()
                .campaigns(campaignDto.getCampaigns())
                .textMining(textMiningResultDto.getResult())
                .build();

        return textMiningCampaignDto;
    }

    /*
     * ElasticSearch에서 텍스트 키워드 리스트(TextMiningResultDto)로 캠페인 검색
     * */
    public CampaignDto getCampaignsByTextMining(TextMiningResultDto textMiningResultDto,
            Pageable pageable) {
        return new CampaignDto(findCampaignsByTextMining(textMiningResultDto, pageable));
    }

    public Page<Campaign> findCampaignsByTextMining(TextMiningResultDto textMiningResultDto,
            Pageable pageable) {
        return textMiningFactory.getCampaignSearchList(
                textMiningFactory.createQuery(textMiningResultDto, pageable));
    }
}
