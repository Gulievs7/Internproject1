// dto/book/BookResponseDto.java
package org.example.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class BookResponseDto {
    private Long id;
    private String isbn;
    private String title;
    private String authorName;
    private Long authorId;
    private String borrowedByName;
    private Long memberId;
}