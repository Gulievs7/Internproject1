package org.example.serviceimpl;

import org.example.dto.member.MemberRequestDto;
import org.example.dto.member.MemberResponseDto;
import org.example.entity.Book;
import org.example.entity.Member;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.MemberRepository;
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
class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    private Member member;
    private MemberRequestDto requestDto;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setName("Aygün Məmmədova");
        member.setEmail("aygun@example.com");

        requestDto = new MemberRequestDto();
        requestDto.setName("Aygün Məmmədova");
        requestDto.setEmail("aygun@example.com");
    }

    @Test
    void create_shouldReturnMemberResponseDto() {
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberResponseDto result = memberService.create(requestDto);

        assertNotNull(result);
        assertEquals("Aygün Məmmədova", result.getName());
        assertEquals("aygun@example.com", result.getEmail());
        assertTrue(result.getBorrowedBookTitles().isEmpty());
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void getById_shouldReturnMember_whenExists() {
        Book book = new Book();
        book.setTitle("Poçt qutusu");
        member.setBorrowedBooks(List.of(book));

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MemberResponseDto result = memberService.getById(1L);

        assertEquals("Aygün Məmmədova", result.getName());
        assertEquals(1, result.getBorrowedBookTitles().size());
        assertEquals("Poçt qutusu", result.getBorrowedBookTitles().get(0));
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberService.getById(99L));
    }

    @Test
    void update_shouldUpdateNameAndEmail() {
        MemberRequestDto updateDto = new MemberRequestDto();
        updateDto.setName("Yeni Ad");
        updateDto.setEmail("yeni@example.com");

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberResponseDto result = memberService.update(1L, updateDto);

        assertEquals("Yeni Ad", member.getName());
        assertEquals("yeni@example.com", member.getEmail());
        verify(memberRepository, times(1)).save(member);
    }

    @Test
    void update_shouldThrowException_whenMemberNotFound() {
        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberService.update(99L, requestDto));
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void delete_shouldCallRepository_whenMemberExists() {
        when(memberRepository.existsById(1L)).thenReturn(true);

        memberService.delete(1L);

        verify(memberRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_shouldThrowException_whenMemberNotExists() {
        when(memberRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> memberService.delete(99L));
        verify(memberRepository, never()).deleteById(anyLong());
    }
}