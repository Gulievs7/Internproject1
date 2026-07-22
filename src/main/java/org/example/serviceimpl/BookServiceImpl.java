package org.example.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.example.dto.book.BookRequestDto;
import org.example.dto.book.BookResponseDto;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.entity.Member;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.AuthorRepository;
import org.example.repository.BookRepository;
import org.example.repository.MemberRepository;
import org.example.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final MemberRepository memberRepository;

    @Override
    public BookResponseDto create(BookRequestDto dto) {
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author tapılmadı: " + dto.getAuthorId()));

        Book book = new Book();
        book.setIsbn(dto.getIsbn());
        book.setTitle(dto.getTitle());
        book.setAuthor(author);

        if (dto.getMemberId() != null) {
            Member member = memberRepository.findById(dto.getMemberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Member tapılmadı: " + dto.getMemberId()));
            book.setBorrowedBy(member);
        }

        return toDto(bookRepository.save(book));
    }

    @Override
    public BookResponseDto getById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitab tapılmadı: " + id));
        return toDto(book);
    }

    @Override
    public Page<BookResponseDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public BookResponseDto update(Long id, BookRequestDto dto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kitab tapılmadı: " + id));

        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Author tapılmadı: " + dto.getAuthorId()));

        book.setIsbn(dto.getIsbn());
        book.setTitle(dto.getTitle());
        book.setAuthor(author);

        if (dto.getMemberId() != null) {
            Member member = memberRepository.findById(dto.getMemberId())
                    .orElseThrow(() -> new ResourceNotFoundException("Member tapılmadı: " + dto.getMemberId()));
            book.setBorrowedBy(member);
        } else {
            book.setBorrowedBy(null);
        }

        return toDto(bookRepository.save(book));
    }

    @Override
    public void delete(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kitab tapılmadı: " + id);
        }
        bookRepository.deleteById(id);
    }

    private BookResponseDto toDto(Book book) {
        return new BookResponseDto(
                book.getId(),
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor().getName(),
                book.getAuthor().getId(),
                book.getBorrowedBy() != null ? book.getBorrowedBy().getName() : null,
                book.getBorrowedBy() != null ? book.getBorrowedBy().getId() : null
        );
    }
}