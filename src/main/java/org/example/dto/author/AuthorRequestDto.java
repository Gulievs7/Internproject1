
package org.example.dto.author;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AuthorRequestDto {
    @NotBlank(message = "Ad boş ola bilməz")
    private String name;

    @NotBlank(message = "Bioqrafiya boş ola bilməz")
    private String biography;
}