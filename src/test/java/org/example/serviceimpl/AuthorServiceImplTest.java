package org.example.serviceimpl;

import org.example.dto.author.AuthorRequestDto;
import org.example.dto.author.AuthorResponseDto;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author;
    private AuthorRequestDto requestDto;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("Nizami Gəncəvi");
        author.setBiography("Azərbaycan şairi");

        requestDto = new AuthorRequestDto();
        requestDto.setName("Nizami Gəncəvi");
        requestDto.setBiography("Azərbaycan şairi");
    }

    @Test
    void create_shouldReturnAuthorResponseDto() {
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorResponseDto result = authorService.create(requestDto);

        assertNotNull(result);
        assertEquals("Nizami Gəncəvi", result.getName());
        assertTrue(result.getBookTitles().isEmpty());
        verify(authorRepository, times(1)).save(any(Author.class));
    }

    @Test
    void getById_shouldReturnAuthor_whenExists() {
        Book book = new Book();
        book.setTitle("Leyli və Məcnun");
        author.setBooks(List.of(book));

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        AuthorResponseDto result = authorService.getById(1L);

        assertEquals("Nizami Gəncəvi", result.getName());
        assertEquals(1, result.getBookTitles().size());
        assertEquals("Leyli və Məcnun", result.getBookTitles().get(0));
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.getById(99L));
    }

    @Test
    void update_shouldUpdateNameAndBiography() {
        AuthorRequestDto updateDto = new AuthorRequestDto();
        updateDto.setName("Yeni Ad");
        updateDto.setBiography("Yeni bioqrafiya");

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(any(Author.class))).thenReturn(author);

        AuthorResponseDto result = authorService.update(1L, updateDto);

        assertEquals("Yeni Ad", author.getName());
        verify(authorRepository, times(1)).save(author);
    }

    @Test
    void update_shouldThrowException_whenAuthorNotFound() {
        when(authorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authorService.update(99L, requestDto));
        verify(authorRepository, never()).save(any(Author.class));
    }

    @Test
    void delete_shouldCallRepository_whenAuthorExists() {
        when(authorRepository.existsById(1L)).thenReturn(true);

        authorService.delete(1L);

        verify(authorRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenAuthorNotExists() {
        when(authorRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> authorService.delete(99L));
        verify(authorRepository, never()).deleteById(anyLong());
    }
}