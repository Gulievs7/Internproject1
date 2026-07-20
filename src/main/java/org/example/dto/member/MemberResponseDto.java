// dto/member/MemberResponseDto.java
package org.example.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String name;
    private String email;
    private List<String> borrowedBookTitles;
}