package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.book.BookRequestDto;
import org.example.dto.book.BookResponseDto;
import org.example.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book", description = "Kitabların idarə edilməsi üçün endpoint-lər")
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Yeni kitab yarat", description = "Verilən müəllif (və istəyə görə üzv) ilə yeni kitab qeydiyyatı yaradır.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Kitab uğurla yaradıldı"),
            @ApiResponse(responseCode = "400", description = "Validasiya xətası (məs. ISBN boşdur)"),
            @ApiResponse(responseCode = "404", description = "Göstərilən Author və ya Member tapılmadı")
    })
    @PostMapping
    public ResponseEntity<BookResponseDto> create(@Valid @RequestBody BookRequestDto dto) {
        return new ResponseEntity<>(bookService.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "ID-yə görə kitab tap", description = "Verilən ID-yə uyğun kitabın məlumatlarını qaytarır.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Kitab tapıldı"),
            @ApiResponse(responseCode = "404", description = "Kitab tapılmadı")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getById(
            @Parameter(description = "Kitabın ID-si") @PathVariable Long id) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @Operation(summary = "Bütün kitabları siyahıla", description = "Səhifələnmiş (paginated) kitab siyahısını qaytar.")
    @ApiResponse(responseCode = "200", description = "Siyahı uğurla qaytarıldı")
    @GetMapping
    public ResponseEntity<Page<BookResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(bookService.getAll(pageable));
    }

    @Operation(summary = "Kitab məlumatlarını yenilə", description = "Verilən ID-yə uyğun kitabın başlığını,.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Uğurla yeniləndi"),
            @ApiResponse(responseCode = "404", description = "Kitab, Author və ya Member tapılmadı"),
            @ApiResponse(responseCode = "400", description = "Validasiya xətası")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDto> update(
            @Parameter(description = "Kitabın ID-si") @PathVariable Long id,
            @Valid @RequestBody BookRequestDto dto) {
        return ResponseEntity.ok(bookService.update(id, dto));
    }

    @Operation(summary = "Kitabı sil", description = "Verilən ID-yə uyğun kitabı bazadan silir.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Uğurla silindi"),
            @ApiResponse(responseCode = "404", description = "Kitab tapılmadı")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Kitabın ID-si") @PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}