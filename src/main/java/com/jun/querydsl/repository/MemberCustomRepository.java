package com.jun.querydsl.repository;

import com.jun.querydsl.dto.MemberSearchCondition;
import com.jun.querydsl.dto.MemberTeamDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemberCustomRepository {

    Page<MemberTeamDto> search(MemberSearchCondition condition, Pageable pageable);
}
