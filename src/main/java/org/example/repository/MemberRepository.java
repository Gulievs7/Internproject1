// repository/MemberRepository.java
package org.example.repository;

import org.example.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}