package com.jun.querydsl.repository;

import com.jun.querydsl.entity.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Test
    public void basicTest() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        Member findMember = memberRepository.findById(member1.getId()).get();

        assertThat(findMember).isEqualTo(member1);

        List<Member> all = memberRepository.findAll();

        assertThat(all).containsExactly(member1);

        List<Member> byUsername = memberRepository.findByUsername("member1");

        assertThat(byUsername).containsExactly(member1);


        //when

        //then
    }


}