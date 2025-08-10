package com.wiseai.meeting_reservation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("WiseAI Meeting Reservation System")
            .description("회의실 목록 조회, 예약, 결제 API 문서")
            .version("v1"));
  }
}
