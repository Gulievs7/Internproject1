package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.author.AuthorRequestDto;
import org.example.dto.author.AuthorResponseDto;
import org.example.exception.GlobalExceptionHandler;
import org.example.exception.ResourceNotFoundException;
import org.example.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
@Import(GlobalExceptionHandler.class)
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthorService authorService;

    @Test
    void create_shouldReturn201_whenValidRequest() throws Exception {
        AuthorRequestDto requestDto = new AuthorRequestDto();
        requestDto.setName("Nizami Gəncəvi");
        requestDto.setBiography("Azərbaycan şairi");

        AuthorResponseDto responseDto = new AuthorResponseDto(1L, "Nizami Gəncəvi", "Azərbaycan şairi", List.of());

        when(authorService.create(any(AuthorRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Nizami Gəncəvi"));
    }

    @Test
    void create_shouldReturn400_whenNameIsBlank() throws Exception {
        AuthorRequestDto requestDto = new AuthorRequestDto();
        requestDto.setName("");
        requestDto.setBiography("Bioqrafiya");

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(authorService, never()).create(any());
    }

    @Test
    void getById_shouldReturn200_whenAuthorExists() throws Exception {
        AuthorResponseDto responseDto = new AuthorResponseDto(1L, "Nizami Gəncəvi", "Bio", List.of("Leyli və Məcnun"));

        when(authorService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/authors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nizami Gəncəvi"))
                .andExpect(jsonPath("$.bookTitles[0]").value("Leyli və Məcnun"));
    }

    @Test
    void getById_shouldReturn404_whenAuthorNotFound() throws Exception {
        when(authorService.getById(99L)).thenThrow(new ResourceNotFoundException("Author tapılmadı: 99"));

        mockMvc.perform(get("/api/authors/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Author tapılmadı: 99"));
    }

    @Test
    void getAll_shouldReturn200_withPagedResult() throws Exception {
        AuthorResponseDto dto = new AuthorResponseDto(1L, "Nizami", "Bio", List.of());
        Page<AuthorResponseDto> page = new PageImpl<>(List.of(dto));

        when(authorService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/authors?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Nizami"));
    }

    @Test
    void update_shouldReturn200_whenValid() throws Exception {
        AuthorRequestDto requestDto = new AuthorRequestDto();
        requestDto.setName("Yeni Ad");
        requestDto.setBiography("Yeni bio");

        AuthorResponseDto responseDto = new AuthorResponseDto(1L, "Yeni Ad", "Yeni bio", List.of());

        when(authorService.update(eq(1L), any(AuthorRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/authors/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yeni Ad"));
    }

    @Test
    void delete_shouldReturn204_whenAuthorExists() throws Exception {
        doNothing().when(authorService).delete(1L);

        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());

        verify(authorService, times(1)).delete(1L);
    }

    @Test
    void delete_shouldReturn404_whenAuthorNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Author tapılmadı: 99"))
                .when(authorService).delete(99L);

        mockMvc.perform(delete("/api/authors/99"))
                .andExpect(status().isNotFound());
    }
}