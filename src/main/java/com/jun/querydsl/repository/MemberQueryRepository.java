package com.jun.querydsl.repository;


import com.jun.querydsl.dto.MemberDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberQueryRepository {

    private final EntityManager em;

    public void nativeQuery() {

//        String sql = "select username as username, age as age from member";
//
//        List resultList = em.createNativeQuery(sql).setTuple
//
//        for (Object o : resultList) {
//            System.out.println("o = " + o);
//        }


    }

}
