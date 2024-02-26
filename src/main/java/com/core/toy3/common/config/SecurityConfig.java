package com.core.toy3.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public BCryptPasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authz -> authz
                    .requestMatchers("/api/auth/test").authenticated()
                    .requestMatchers("/api/auth/member").authenticated()
      .requestMatchers("/api/auth/logout").authenticated() // 인증된 상태에서만 logout 접근 가능

    // ----(추후 url 수정 예정)----
      .requestMatchers("/api/likes/**").authenticated() // 좋아요 api
      .requestMatchers("/api/travel/**").authenticated() // 여행, 여정 api
      .requestMatchers("/api/trip/**").authenticated() // 여행, 여정 api
      .requestMatchers("/api/comment/**").authenticated() // 댓글 api
//      .requestMatchers(toH2Console()).permitAll() // h2 console 접근 가능
      .anyRequest().permitAll())

            .headers(headers -> headers
                    .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

      .formLogin(form -> form
              .loginPage("/api/auth/") // 인증 필요한 사이트 접근시 띄울 로그인 페이지(임시)
              .loginProcessingUrl("/api/auth/login") // 로그인
              .defaultSuccessUrl("/api/auth/logged", true) // 로그인 후 보내질 페이지
              .failureUrl("/api/auth/error")) // 로그인 실패 시 보내질 페이지

            .logout(logout -> logout
                    .logoutSuccessUrl("/api/auth/") // 로그아웃시 보내질 페이지
                    .logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
                    .clearAuthentication(true)
                    .deleteCookies("JSESSIONID")
                    .invalidateHttpSession(true)
            );
    return http.build();
  }
}
