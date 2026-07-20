// service/AuthorService.java
package org.example.service;

import org.example.dto.author.AuthorRequestDto;
import org.example.dto.author.AuthorResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuthorService {
    AuthorResponseDto create(AuthorRequestDto dto);
    AuthorResponseDto getById(Long id);
    Page<AuthorResponseDto> getAll(Pageable pageable);
    AuthorResponseDto update(Long id, AuthorRequestDto dto);
    void delete(Long id);
}