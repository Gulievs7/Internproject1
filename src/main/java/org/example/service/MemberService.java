// service/MemberService.java
package org.example.service;

import org.example.dto.member.MemberRequestDto;
import org.example.dto.member.MemberResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {
    MemberResponseDto create(MemberRequestDto dto);
    MemberResponseDto getById(Long id);
    Page<MemberResponseDto> getAll(Pageable pageable);
    MemberResponseDto update(Long id, MemberRequestDto dto);
    void delete(Long id);
}