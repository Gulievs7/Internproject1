package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.book.BookRequestDto;
import org.example.dto.book.BookResponseDto;
import org.example.exception.ResourceNotFoundException;
import org.example.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.example.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    void create_shouldReturn201_whenValidRequest() throws Exception {
        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setIsbn("978-3-16-148410-0");
        requestDto.setTitle("Poçt qutusu");
        requestDto.setAuthorId(1L);

        BookResponseDto responseDto = new BookResponseDto(
                1L, "978-3-16-148410-0", "Poçt qutusu", "Cəlil Məmmədquluzadə", 1L, null, null);

        when(bookService.create(any(BookRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Poçt qutusu"))
                .andExpect(jsonPath("$.authorName").value("Cəlil Məmmədquluzadə"));
    }

    @Test
    void create_shouldReturn400_whenIsbnIsBlank() throws Exception {
        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setIsbn("");
        requestDto.setTitle("Başlıq");
        requestDto.setAuthorId(1L);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(bookService, never()).create(any());
    }

    @Test
    void create_shouldReturn404_whenAuthorNotFound() throws Exception {
        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setIsbn("978-0-00-000000-0");
        requestDto.setTitle("Kitab");
        requestDto.setAuthorId(99L);

        when(bookService.create(any(BookRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Author tapılmadı: 99"));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_shouldReturn200_whenBookExists() throws Exception {
        BookResponseDto responseDto = new BookResponseDto(
                1L, "978-3-16-148410-0", "Poçt qutusu", "Cəlil Məmmədquluzadə", 1L, null, null);

        when(bookService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("978-3-16-148410-0"));
    }

    @Test
    void getById_shouldReturn404_whenBookNotFound() throws Exception {
        when(bookService.getById(99L)).thenThrow(new ResourceNotFoundException("Kitab tapılmadı: 99"));

        mockMvc.perform(get("/api/books/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_shouldReturn200_withPagedResult() throws Exception {
        BookResponseDto dto = new BookResponseDto(1L, "isbn", "title", "author", 1L, null, null);
        Page<BookResponseDto> page = new PageImpl<>(List.of(dto));

        when(bookService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/books?page=0&size=10&sort=title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("title"));
    }

    @Test
    void update_shouldReturn200_whenValid() throws Exception {
        BookRequestDto requestDto = new BookRequestDto();
        requestDto.setIsbn("978-0-00-000000-0");
        requestDto.setTitle("Yeni başlıq");
        requestDto.setAuthorId(1L);

        BookResponseDto responseDto = new BookResponseDto(
                1L, "978-0-00-000000-0", "Yeni başlıq", "Author", 1L, null, null);

        when(bookService.update(eq(1L), any(BookRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Yeni başlıq"));
    }

    @Test
    void delete_shouldReturn204_whenBookExists() throws Exception {
        doNothing().when(bookService).delete(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).delete(1L);
    }

    @Test
    void delete_shouldReturn404_whenBookNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Kitab tapılmadı: 99"))
                .when(bookService).delete(99L);

        mockMvc.perform(delete("/api/books/99"))
                .andExpect(status().isNotFound());
    }
}