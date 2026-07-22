package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.author.AuthorRequestDto;
import org.example.dto.author.AuthorResponseDto;
import org.example.service.AuthorService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "Author", description = "Müəlliflərin idarə edilməsi üçün endpoint-lər")
public class AuthorController {

    private final AuthorService authorService;

    @Operation(summary = "Yeni müəllif yarat", description = "Verilən məlumatlarla yeni bir müəllif qeydiyyatı yaradır.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Müəllif uğurla yaradıldı"),
            @ApiResponse(responseCode = "400", description = "Validasiya xətası (məs. ad boşdur)")
    })
    @PostMapping
    public ResponseEntity<AuthorResponseDto> create(@Valid @RequestBody AuthorRequestDto dto) {
        return new ResponseEntity<>(authorService.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "ID-yə görə müəllif tap", description = "Verilən ID-yə uyğun müəllifin məlumatlarını qaytarır.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Müəllif tapıldı"),
            @ApiResponse(responseCode = "404", description = "Müəllif tapılmadı")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> getById(
            @Parameter(description = "Müəllifin ID-si") @PathVariable Long id) {
        return ResponseEntity.ok(authorService.getById(id));
    }

    @Operation(summary = "siyahıla", description = "Səhifələnmiş (paginated) siyahısını qaytarır).")
    @ApiResponse(responseCode = "200", description = "Siyahı uğurla qaytarıldı")
    @GetMapping
    public ResponseEntity<Page<AuthorResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(authorService.getAll(pageable));
    }

    @Operation(summary = " məlumatlarını yenilə", description = "Verilən ID-yə uyğun müəllifin adını və bioqrafiyasını yeniləyir.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Uğurla yeniləndi"),
            @ApiResponse(responseCode = "404", description = "Müəllif tapılmadı"),
            @ApiResponse(responseCode = "400", description = "Validasiya xətası")
    })
    @PutMapping("/{id}")
    public ResponseEntity<AuthorResponseDto> update(
            @Parameter(description = "Müəllifin ID-si") @PathVariable Long id,
            @Valid @RequestBody AuthorRequestDto dto) {
        return ResponseEntity.ok(authorService.update(id, dto));
    }

    @Operation(summary = "Müəllifi sil", description = "Verilən ID-yə uyğun müəllifi bazadan silir.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Uğurla silindi"),
            @ApiResponse(responseCode = "404", description = "Müəllif tapılmadı")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Müəllifin ID-si") @PathVariable Long id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}