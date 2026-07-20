// dto/author/AuthorResponseDto.java
package org.example.dto.author;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter @Setter @AllArgsConstructor
public class AuthorResponseDto {
    private Long id;
    private String name;
    private String biography;
    private List<String> bookTitles;
}
