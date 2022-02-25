package com.jun.querydsl.repository;

import com.jun.querydsl.entity.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {

    @Autowired
    EntityManager em;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Test
    public void basicTest() throws Exception {
        //given
        Member member1 = new Member("member1", 10);
        memberJpaRepository.save(member1);

        Member findMember = memberJpaRepository.findById(member1.getId()).get();

        assertThat(findMember).isEqualTo(member1);

        List<Member> all = memberJpaRepository.findAll();

        assertThat(all).containsExactly(member1);

        List<Member> byUsername = memberJpaRepository.findByUsername("member1");

        assertThat(byUsername).containsExactly(member1);


        //when

        //then
    }

}