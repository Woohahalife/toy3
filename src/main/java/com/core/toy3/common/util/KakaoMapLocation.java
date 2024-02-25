package com.core.toy3.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.DataInput;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class KakaoMapLocation {

  @Value("${kakao.api-key}")
  private String API_KEY;
  @Value("${kakao.api-url}")
  private String API_URL;

  @Getter
  @Setter
  public class JsonResponse {
    private List<String> documents;
  }

  public String getLocation(String address) {
    RestTemplate restTemplate = new RestTemplate();

    // 헤더 작성
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "KakaoAK " + API_KEY);

    // 쿼리 보내고 받아오기 (JSON Map으로 받음)
    HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
    ResponseEntity<String> result = restTemplate.exchange(
            API_URL+"?query="+address,
            HttpMethod.GET,
            httpEntity,
            String.class);

    ObjectMapper objectMapper = new ObjectMapper();
    try{
      Map jsonMap = objectMapper.readValue(result.getBody(), Map.class);

      // 첫번째 검색 결과 가져옴
      Map firstResult = (Map)((List) jsonMap.get("documents")).get(0);
      String addressType = (String) firstResult.get("address_type");

      String resultAddress = "";

      switch (addressType){
        case "ROAD_ADDR":
          String roadAddressName = (String) ((Map) firstResult.get("road_address")).get("address_name");
          String buildingName = (String) ((Map) firstResult.get("road_address")).get("building_name");

          resultAddress = roadAddressName + " " + buildingName;
          break;

        case "ROAD":
          roadAddressName = (String) ((Map) firstResult.get("road_address")).get("address_name");

          resultAddress = roadAddressName;
          break;

        // REGION, REGION_ADDR
        default:
          String regionAddressName = (String) ((Map) firstResult.get("address")).get("address_name");
          resultAddress = regionAddressName;
          break;
      }

      if (resultAddress.isEmpty()){
        return null;
      } else {
        return resultAddress;
      }
    } catch (Exception e){
      return null;
    }
  }
}
