package org.example.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookRequestDto {
    @NotBlank(message = "ISBN boş ola bilməz")
    private String isbn;

    @NotBlank(message = "Başlıq boş ola bilməz")
    private String title;

    @NotNull(message = "Author ID mütləqdir")
    private Long authorId;

    private Long memberId;
}