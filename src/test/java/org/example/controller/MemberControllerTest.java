package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.member.MemberRequestDto;
import org.example.dto.member.MemberResponseDto;
import org.example.exception.ResourceNotFoundException;
import org.example.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import org.example.exception.GlobalExceptionHandler;
import org.springframework.context.annotation.Import;
@WebMvcTest(MemberController.class)
@Import(GlobalExceptionHandler.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    void create_shouldReturn201_whenValidRequest() throws Exception {
        MemberRequestDto requestDto = new MemberRequestDto();
        requestDto.setName("Aygün Məmmədova");
        requestDto.setEmail("aygun@example.com");

        MemberResponseDto responseDto = new MemberResponseDto(1L, "Aygün Məmmədova", "aygun@example.com", List.of());

        when(memberService.create(any(MemberRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("aygun@example.com"));
    }

    @Test
    void create_shouldReturn400_whenEmailIsInvalid() throws Exception {
        MemberRequestDto requestDto = new MemberRequestDto();
        requestDto.setName("Aygün");
        requestDto.setEmail("not-an-email");

        mockMvc.perform(post("/api/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(memberService, never()).create(any());
    }

    @Test
    void getById_shouldReturn200_whenMemberExists() throws Exception {
        MemberResponseDto responseDto = new MemberResponseDto(1L, "Aygün", "aygun@example.com", List.of("Kitab"));

        when(memberService.getById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/api/members/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.borrowedBookTitles[0]").value("Kitab"));
    }

    @Test
    void getById_shouldReturn404_whenMemberNotFound() throws Exception {
        when(memberService.getById(99L)).thenThrow(new ResourceNotFoundException("Member tapılmadı: 99"));

        mockMvc.perform(get("/api/members/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_shouldReturn200_withPagedResult() throws Exception {
        MemberResponseDto dto = new MemberResponseDto(1L, "Aygün", "aygun@example.com", List.of());
        Page<MemberResponseDto> page = new PageImpl<>(List.of(dto));

        when(memberService.getAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/members?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Aygün"));
    }

    @Test
    void update_shouldReturn200_whenValid() throws Exception {
        MemberRequestDto requestDto = new MemberRequestDto();
        requestDto.setName("Yeni Ad");
        requestDto.setEmail("yeni@example.com");

        MemberResponseDto responseDto = new MemberResponseDto(1L, "Yeni Ad", "yeni@example.com", List.of());

        when(memberService.update(eq(1L), any(MemberRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/api/members/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Yeni Ad"));
    }

    @Test
    void delete_shouldReturn204_whenMemberExists() throws Exception {
        doNothing().when(memberService).delete(1L);

        mockMvc.perform(delete("/api/members/1"))
                .andExpect(status().isNoContent());

        verify(memberService, times(1)).delete(1L);
    }

    @Test
    void delete_shouldReturn404_whenMemberNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Member tapılmadı: 99"))
                .when(memberService).delete(99L);

        mockMvc.perform(delete("/api/members/99"))
                .andExpect(status().isNotFound());
    }
}