package com.jun.querydsl;

import com.jun.querydsl.dto.MemberDto;
import com.jun.querydsl.entity.Member;
import com.jun.querydsl.entity.QMember;
import com.jun.querydsl.entity.QTeam;
import com.jun.querydsl.entity.Team;
import com.jun.querydsl.repository.MemberQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.List;

import static com.jun.querydsl.entity.QMember.*;

@SpringBootTest
@Transactional
@Commit
@Slf4j
public class QuerydslBasicTest {

    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @Autowired
    MemberQueryRepository memberQueryRepository;


    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        em.persist(teamA);
        em.persist(teamB);
        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);
        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);
        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @Test
    public void startQuerydsl() {
//member1을 찾아라.
        QMember m = new QMember("m");
        Member findMember = queryFactory
                .select(m)
                .from(m)
                .where(m.username.eq("member1"))//파라미터 바인딩 처리
                .fetchOne();


        Assertions.assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    @Test
    public void search() {

        Member member = queryFactory
                .selectFrom(QMember.member)
                .where(QMember.member.username.eq("member1")
                        .and(QMember.member.age.eq(10))
                )
                .fetchOne();

        Assertions.assertThat(member.getUsername()).isEqualTo("member1");

    }

    @Test
    public void resutFetch() {

        QueryResults<Member> memberQueryResults = queryFactory.selectFrom(member).fetchResults();

        memberQueryResults.getTotal();
    }

    /**
     * 나이 내림
     * 이름 올림
     * 이름이 null이면 마지막 출력
     */

    @Test
    public void sort() {

        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> members = queryFactory.selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        log.debug(members.get(0).toString());
    }

    @Test
    public void paging1() {
        List<Member> members = queryFactory.selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();

        Assertions.assertThat(members.size()).isEqualTo(2);


    }

    @Test
    public void group() throws Exception {
        //given
        List<Tuple> tuples = queryFactory.select(QTeam.team.name, member.age.avg())
                .from(member)
                .join(member.team, QTeam.team)
                .groupBy(QTeam.team.name)
                .fetch();
        //when


        //then
    }

    @Test
    public void join_on_filter() throws Exception {
        //given
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));
        em.persist(new Member("teamC"));

        List<Tuple> teamA = queryFactory.select(member, QTeam.team)
                .from(member)
                .leftJoin(QTeam.team)
                .on(member.username.eq(QTeam.team.name))
                .fetch();
        for (Tuple tuple : teamA) {
            System.out.println("tuple = " + tuple);
        }

    }

    @Test
    public void subQuery() throws Exception {
        //given

        QMember memSub = new QMember("memberSub");

        List<Tuple> fetch = queryFactory
                .select(member.username, JPAExpressions.select(memSub.age.avg()).from(memSub))
                .from(member)
                .fetch();
        for (Tuple fetch1 : fetch) {
            System.out.println("fetch1 = " + fetch1);
        }
        
    }

    @Test
    public void basicCase() throws Exception {
        //given
        List<String> fetch = queryFactory
                .select(member.age
                        .when(10).then("ten")
                        .when(20).then("twenty")
                        .otherwise("etc")
                )
                .from(member)
                .fetch();
        //when

        for (String s : fetch) {
            System.out.println("s = " + s);
        }
        //then
    }
    
    @Test
    public void findDtoBySetter() throws Exception {
        //given
        List<MemberDto> memberDtos = queryFactory.select(Projections.bean(MemberDto.class, member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : memberDtos) {
            System.out.println("memberDto = " + memberDto);
        }
        //when
        
        //then
    }

    @Test
    public void dynamicBoolean() throws Exception {
        //given
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);

        //when

        //then
    }

    private List<Member> searchMember1(String usernameParam, Integer ageParam) {

        List<Member> members = queryFactory
                .selectFrom(member)
                .where(usernameEq(usernameParam), ageEq(ageParam))
                .fetch();

        for (Member member1 : members) {
            System.out.println("member1 = " + member1);
        }

        return members;
    }

    private Predicate ageEq(Integer ageParam) {
        if(ageParam == null)
            return null;

        return member.age.eq(ageParam);
    }

    private Predicate usernameEq(String usernameParam) {
        if(usernameParam == null)
            return null;

        return member.username.eq(usernameParam);
    }
    
    @Test
    public void bulkUpdate() throws Exception {
        //given

        long number = queryFactory.update(member)
                .set(member.username, "exit")
                .where(member.age.lt(28))
                .execute();

        System.out.println("number = " + number);

        //bulk 연산 나가면 해줌
        em.flush();
        em.clear();

        List<Member> fetch = queryFactory.selectFrom(member).fetch();

        //db에서 값을 가져온다해도 영속성컨텍스트에 이미 있으면 가져온 값을 버리고 영속성컨텍스트에 있는 값을 사용
        //그래서 쿼리는 나가는데 바뀌지 않은 값인거임임

       for (Member fetch1 : fetch) {
            System.out.println("fetch1 = " + fetch1);
        }
        


        //when
        
        //then
    }

    @Test
    public void updateTest() throws Exception {
        //given
         memberQueryRepository.nativeQuery();


        //when

        //then
    }


}
