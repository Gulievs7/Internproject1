package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.member.MemberRequestDto;
import org.example.dto.member.MemberResponseDto;
import org.example.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Member", description = "Kitabxana üzvlərinin idarə edilməsi üçün endpoint-lər")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Yeni üzv qeydiyyatdan keçir", description = "Verilən ad və email ilə yeni kitabxana üzvü yaradır.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Üzv uğurla yaradıldı"),
            @ApiResponse(responseCode = "400", description = "Validasiya xətası (məs. email formatı yanlışdır)")
    })
    @PostMapping
    public ResponseEntity<MemberResponseDto> create(@Valid @RequestBody MemberRequestDto dto) {
        return new ResponseEntity<>(memberService.create(dto), HttpStatus.CREATED);
    }

    @Operation(summary = "ID-yə görə üzv tap", description = "Verilən ID-yə uyğun üzvün məlumatlarını qaytarır.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Üzv tapıldı"),
            @ApiResponse(responseCode = "404", description = "Üzv tapılmadı")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponseDto> getById(
            @Parameter(description = "Üzvün ID-si") @PathVariable Long id) {
        return ResponseEntity.ok(memberService.getById(id));
    }

    @Operation(summary = "Bütün üzvləri siyahıla", description = "Səhifələnmiş (paginated) üzv siyahısını ort parametrini istifadə edin =name,asc).")
    @ApiResponse(responseCode = "200", description = "Siyahı uğurla qaytarıldı")
    @GetMapping
    public ResponseEntity<Page<MemberResponseDto>> getAll(Pageable pageable) {
        return ResponseEntity.ok(memberService.getAll(pageable));
    }

    @Operation(summary = "Üzv məlumatlarını yenilə", description = "Verilən ID-yə uyğun üzvün adını və emailini yeniləyir.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Uğurla yeniləndi"),
            @ApiResponse(responseCode = "404", description = "Üzv tapılmadı"),
            @ApiResponse(responseCode = "400", description = "Validasiya xətası")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MemberResponseDto> update(
            @Parameter(description = "Üzvün ID-si") @PathVariable Long id,
            @Valid @RequestBody MemberRequestDto dto) {
        return ResponseEntity.ok(memberService.update(id, dto));
    }

    @Operation(summary = "Üzvü sil", description = "Verilən ID-yə uyğun üzvü bazadan silir.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Uğurla silindi"),
            @ApiResponse(responseCode = "404", description = "Üzv tapılmadı")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Üzvün ID-si") @PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}