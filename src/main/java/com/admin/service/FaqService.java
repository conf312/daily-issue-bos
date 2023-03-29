package com.admin.service;

import com.admin.domain.faq.Faq;
import com.admin.domain.faq.FaqRepository;
import com.admin.domain.faq.QFaq;
import com.admin.domain.member.QMember;
import com.admin.util.AuthorizationUtil;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class FaqService {
    private final FaqRepository faqRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final QFaq faq = QFaq.faq;
    private final QMember member = QMember.member;

    public BooleanExpression eqType(String type) {
        if (!StringUtils.hasText(type)) return null;
        return faq.type.eq(type);
    }

    public BooleanExpression eqUseYn(String useYn) {
        if (!StringUtils.hasText(useYn)) return null;
        return faq.useYn.eq(useYn);
    }

    public BooleanExpression containsSearch(String search) {
        if (!StringUtils.hasText(search)) return null;
        return faq.title.containsIgnoreCase(search).or(faq.contents.containsIgnoreCase(search));
    }

    public Long save(Faq.Request request) {
        request.setRegisterId(AuthorizationUtil.getMember().getId());
        return faqRepository.save(request.toEntity()).getId();
    }

    public HashMap<String, Object> findAll(Faq.Request request, Integer page, Integer pageSize) {
        HashMap<String, Object> resultMap = new HashMap<>();

        List<Faq.Response> list = jpaQueryFactory
            .from(faq)
            .where(
                containsSearch(request.getSearch()),
                eqType(request.getType()),
                eqUseYn(request.getUseYn())
            )
            .offset(page)
            .limit(pageSize)
            .orderBy(faq.registerTime.desc())
            .fetch()
            .stream()
            .map(Faq.Response::new)
            .collect(Collectors.toList());

        Long totalCnt = jpaQueryFactory
            .select(faq.count())
            .from(faq)
            .where(
                containsSearch(request.getSearch()),
                eqType(request.getType()),
                eqUseYn(request.getUseYn())
            )
            .fetchOne();

        int totalPage = (int) Math.ceil((float) totalCnt / pageSize);
        totalPage = totalPage == 0 ? 1 : totalPage;

        resultMap.put("list", list);
        resultMap.put("request", request);
        resultMap.put("page", page);
        resultMap.put("pageSize", pageSize);
        resultMap.put("totalCnt", totalCnt);
        resultMap.put("totalPage", totalPage);

        return resultMap;
    }

    @Transactional
    public long updateFaq(Faq.Request request) {
        return jpaQueryFactory.update(faq)
            .set(faq.type, request.getType())
            .set(faq.title, request.getTitle())
            .set(faq.contents, request.getContents())
            .set(faq.useYn, request.getUseYn())
            .set(faq.modifyId, AuthorizationUtil.getMember().getId())
            .set(faq.modifyTime, LocalDateTime.now())
            .where(faq.id.eq(request.getId()))
            .execute();
    }

    public Faq.Response findById(Long id) {
        return jpaQueryFactory
            .select(Projections.constructor(Faq.Response.class,
                faq.id,
                faq.type,
                faq.title,
                faq.contents,
                faq.useYn,
                faq.registerTime,
                faq.modifyTime,
                ExpressionUtils.as(
                    JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where(member.id.eq(faq.registerId)), "registerName"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where(member.id.eq(faq.modifyId)), "modifyName"
                )
            ))
            .from(faq)
            .where(faq.id.eq(id))
            .fetchOne();
    }

    @Transactional
    public long deleteFaq(Faq.Request request) {
        return jpaQueryFactory.delete(faq)
            .where(faq.id.in(request.getIdArr()))
            .execute();
    }
}
