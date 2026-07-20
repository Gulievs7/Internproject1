// serviceimpl/AuthorServiceImpl.java
package org.example.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.example.dto.author.AuthorRequestDto;
import org.example.dto.author.AuthorResponseDto;
import org.example.entity.Author;
import org.example.entity.Book;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.AuthorRepository;
import org.example.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public AuthorResponseDto create(AuthorRequestDto dto) {
        Author author = new Author();
        author.setName(dto.getName());
        author.setBiography(dto.getBiography());
        return toDto(authorRepository.save(author));
    }

    @Override
    public AuthorResponseDto getById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author tapılmadı: " + id));
        return toDto(author);
    }

    @Override
    public Page<AuthorResponseDto> getAll(Pageable pageable) {
        return authorRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public AuthorResponseDto update(Long id, AuthorRequestDto dto) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Author tapılmadı: " + id));
        author.setName(dto.getName());
        author.setBiography(dto.getBiography());
        return toDto(authorRepository.save(author));
    }

    @Override
    public void delete(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Author tapılmadı: " + id);
        }
        authorRepository.deleteById(id);
    }

    private AuthorResponseDto toDto(Author author) {
        List<String> bookTitles = author.getBooks() == null ? List.of() :
                author.getBooks().stream().map(Book::getTitle).collect(Collectors.toList());
        return new AuthorResponseDto(author.getId(), author.getName(), author.getBiography(), bookTitles);
    }
}