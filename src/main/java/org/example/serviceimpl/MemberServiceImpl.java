// serviceimpl/MemberServiceImpl.java
package org.example.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.example.dto.member.MemberRequestDto;
import org.example.dto.member.MemberResponseDto;
import org.example.entity.Book;
import org.example.entity.Member;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.MemberRepository;
import org.example.service.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    @Override
    public MemberResponseDto create(MemberRequestDto dto) {
        Member member = new Member();
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        return toDto(memberRepository.save(member));
    }

    @Override
    public MemberResponseDto getById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member tapılmadı: " + id));
        return toDto(member);
    }

    @Override
    public Page<MemberResponseDto> getAll(Pageable pageable) {
        return memberRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public MemberResponseDto update(Long id, MemberRequestDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member tapılmadı: " + id));
        member.setName(dto.getName());
        member.setEmail(dto.getEmail());
        return toDto(memberRepository.save(member));
    }

    @Override
    public void delete(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member tapılmadı: " + id);
        }
        memberRepository.deleteById(id);
    }

    private MemberResponseDto toDto(Member member) {
        List<String> borrowedTitles = member.getBorrowedBooks() == null ? List.of() :
                member.getBorrowedBooks().stream().map(Book::getTitle).collect(Collectors.toList());
        return new MemberResponseDto(member.getId(), member.getName(), member.getEmail(), borrowedTitles);
    }
}