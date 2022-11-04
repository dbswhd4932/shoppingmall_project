package com.project.shop.member.repository;

import com.project.shop.factory.MemberFactory;
import com.project.shop.member.domain.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("회원 가입 DB 저장")
    void saveMemberTest() {
        //given
        Member member = MemberFactory.createMember();
        //when
        Member savedMember = memberRepository.save(member);
        //then
        assertThat(savedMember).isSameAs(member);

    }
}