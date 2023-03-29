package com.admin.service;

import com.admin.domain.inquiry.Inquiry;
import com.admin.domain.inquiry.InquiryRepository;
import com.admin.domain.inquiry.QInquiry;
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
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final QInquiry inquiry = QInquiry.inquiry;
    private final QMember member = QMember.member;

    public BooleanExpression eqType(String type) {
        if (!StringUtils.hasText(type)) return null;
        return inquiry.type.eq(type);
    }

    public BooleanExpression eqDeviceType(String deviceType) {
        if (!StringUtils.hasText(deviceType)) return null;
        return inquiry.deviceType.eq(deviceType);
    }

    public BooleanExpression eqAnswerStatus(String answerStatus) {
        if (!StringUtils.hasText(answerStatus)) return null;
        return inquiry.answerStatus.eq(answerStatus);
    }

    public BooleanExpression containsSearch(String search) {
        if (!StringUtils.hasText(search)) return null;
        return inquiry.title.containsIgnoreCase(search).or(inquiry.contents.containsIgnoreCase(search));
    }

    public HashMap<String, Object> findAll(Inquiry.Request request, Integer page, Integer pageSize) {
        HashMap<String, Object> resultMap = new HashMap<>();

        List<Inquiry.Response> list = jpaQueryFactory
            .from(inquiry)
            .where(
                eqType(request.getType()),
                eqDeviceType(request.getDeviceType()),
                eqAnswerStatus(request.getAnswerStatus()),
                containsSearch(request.getSearch())
            )
            .offset(page)
            .limit(pageSize)
            .orderBy(inquiry.registerTime.desc())
            .fetch()
            .stream()
            .map(Inquiry.Response::new)
            .collect(Collectors.toList());

        Long totalCnt = jpaQueryFactory
            .select(inquiry.count())
            .from(inquiry)
            .where(
                eqType(request.getType()),
                eqDeviceType(request.getDeviceType()),
                eqAnswerStatus(request.getAnswerStatus()),
                containsSearch(request.getSearch())
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

    public Inquiry.Response findById(Long id) {
        return jpaQueryFactory
            .select(Projections.constructor(Inquiry.Response.class,
                inquiry.id,
                inquiry.type,
                inquiry.email,
                inquiry.title,
                inquiry.contents,
                inquiry.deviceType,
                inquiry.registerTime,
                inquiry.modifyTime,
                inquiry.answer,
                inquiry.answerTime,
                inquiry.answerId,
                inquiry.answerStatus,
                ExpressionUtils.as(
                    JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where(
                        member.id.eq(inquiry.answerId)
                    ), "answerName"
                )
            ))
            .from(inquiry)
            .where(inquiry.id.eq(id))
            .fetchOne();
    }

    @Transactional
    public long updateAnswer(Inquiry.Request request) {
        return jpaQueryFactory
            .update(inquiry)
            .set(inquiry.answer, request.getAnswer())
            .set(inquiry.answerStatus, request.getAnswerStatus())
            .set(inquiry.answerId, AuthorizationUtil.getMember().getId())
            .set(inquiry.answerTime, LocalDateTime.now())
            .where(inquiry.id.eq(request.getId()))
            .execute();
    }
}
