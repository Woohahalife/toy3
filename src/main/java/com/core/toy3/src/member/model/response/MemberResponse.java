package com.core.toy3.src.member.model.response;

import com.core.toy3.src.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponse {
  private Long id;
  private String username;
  private String name;

  public static MemberResponse fromEntity(Member member){
    return MemberResponse.builder()
            .id(member.getId())
            .username(member.getUsername())
            .name(member.getName())
            .build();
  }
}
