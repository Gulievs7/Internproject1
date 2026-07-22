
package org.example.service;

import org.example.dto.book.BookRequestDto;
import org.example.dto.book.BookResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookResponseDto create(BookRequestDto dto);
    BookResponseDto getById(Long id);
    Page<BookResponseDto> getAll(Pageable pageable);
    BookResponseDto update(Long id, BookRequestDto dto);
    void delete(Long id);
}