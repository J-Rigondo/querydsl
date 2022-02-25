package com.jun.querydsl.repository;

import com.jun.querydsl.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface MemberRepository extends JpaRepository<Member, Long>, MemberCustomRepository{

    List<Member> findByUsername(String username);

}
