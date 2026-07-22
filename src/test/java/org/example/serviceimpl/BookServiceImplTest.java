package org.example.serviceimpl;

import org.example.dto.book.BookRequestDto;
import org.example.dto.book.BookResponseDto;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.Member;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.example.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Author author;
    private Book book;
    private BookRequestDto requestDto;

    @BeforeEach
    void setUp() {
        author = new Author();
        author.setId(1L);
        author.setName("Cəlil Məmmədquluzadə");
        author.setBiography("Azərbaycan yazıçısı");

        book = new Book();
        book.setId(1L);
        book.setIsbn("978-3-16-148410-0");
        book.setTitle("Poçt qutusu");
        book.setAuthor(author);

        requestDto = new BookRequestDto();
        requestDto.setIsbn("978-3-16-148410-0");
        requestDto.setTitle("Poçt qutusu");
        requestDto.setAuthorId(1L);
    }

    @Test
    void create_shouldReturnBookResponseDto_whenAuthorExists() {
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponseDto result = bookService.create(requestDto);

        assertNotNull(result);
        assertEquals("Poçt qutusu", result.getTitle());
        assertEquals("Cəlil Məmmədquluzadə", result.getAuthorName());
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void create_shouldThrowException_whenAuthorNotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.create(requestDto));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void create_shouldSetBorrowedBy_whenMemberIdProvided() {
        Member member = new Member();
        member.setId(5L);
        member.setName("Aygün");

        requestDto.setMemberId(5L);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(memberRepository.findById(5L)).thenReturn(Optional.of(member));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            b.setId(1L);
            return b;
        });

        BookResponseDto result = bookService.create(requestDto);

        assertEquals("Aygün", result.getBorrowedByName());
    }

    @Test
    void getById_shouldReturnBook_whenExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponseDto result = bookService.getById(1L);

        assertEquals("978-3-16-148410-0", result.getIsbn());
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(bookRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookService.getById(99L));
    }

    @Test
    void update_shouldUpdateFields_whenBookAndAuthorExist() {
        BookRequestDto updateDto = new BookRequestDto();
        updateDto.setIsbn("978-0-00-000000-0");
        updateDto.setTitle("Yeni başlıq");
        updateDto.setAuthorId(1L);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponseDto result = bookService.update(1L, updateDto);

        assertEquals("Yeni başlıq", result.getTitle());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void delete_shouldCallRepository_whenBookExists() {
        when(bookRepository.existsById(1L)).thenReturn(true);

        bookService.delete(1L);

        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenBookNotExists() {
        when(bookRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> bookService.delete(99L));
        verify(bookRepository, never()).deleteById(anyLong());
    }
}