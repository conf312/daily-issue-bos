package com.admin.service;

import com.admin.domain.attch.file.AttachFile;
import com.admin.domain.member.QMember;
import com.admin.domain.notice.Notice;
import com.admin.domain.notice.NoticeRepository;
import com.admin.domain.notice.QNotice;
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
import org.springframework.web.multipart.MultipartRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class NoticeService {
    private final NoticeRepository noticeRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final AttachFileService attachFileService;
    private final QNotice notice = QNotice.notice;
    private final QMember member = QMember.member;

    public BooleanExpression eqType(String type) {
        if (!StringUtils.hasText(type)) return null;
        return notice.type.eq(type);
    }

    public BooleanExpression eqTopYn(String topYn) {
        if (!StringUtils.hasText(topYn)) return null;
        return notice.topYn.eq(topYn);
    }

    public BooleanExpression eqUseYn(String useYn) {
        if (!StringUtils.hasText(useYn)) return null;
        return notice.useYn.eq(useYn);
    }

    public BooleanExpression containsSearch(String search) {
        if (!StringUtils.hasText(search)) return null;
        return notice.title.containsIgnoreCase(search).or(notice.contents.containsIgnoreCase(search));
    }

    public Long save(Notice.Request request, MultipartRequest multipartRequest) throws Exception {

        request.setRegisterId(AuthorizationUtil.getMember().getId());
        Long result = noticeRepository.save(request.toEntity()).getId();

        if (result > 0) {
            attachFileService.upload(multipartRequest,"notice", result, null);
        }

        return result;
    }

    public HashMap<String, Object> findAll(Notice.Request request, Integer page, Integer pageSize) {
        HashMap<String, Object> resultMap = new HashMap<>();

        List<Notice.Response> list = jpaQueryFactory
            .from(notice)
            .where(
                containsSearch(request.getSearch()),
                eqType(request.getType()),
                eqTopYn(request.getTopYn()),
                eqUseYn(request.getUseYn())
            )
            .offset(page)
            .limit(pageSize)
            .orderBy(notice.registerTime.desc())
            .fetch()
            .stream()
            .map(Notice.Response::new)
            .collect(Collectors.toList());

        Long totalCnt = jpaQueryFactory
            .select(notice.count())
            .from(notice)
            .where(
                containsSearch(request.getSearch()),
                eqType(request.getType()),
                eqTopYn(request.getTopYn()),
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
    public long updateNotice(Notice.Request request, MultipartRequest multipartRequest) throws Exception {
        attachFileService.upload(multipartRequest,"notice", request.getId(), null);
        return jpaQueryFactory.update(notice)
            .set(notice.type, request.getType())
            .set(notice.topYn, request.getTopYn())
            .set(notice.title, request.getTitle())
            .set(notice.contents, request.getContents())
            .set(notice.useYn, request.getUseYn())
            .set(notice.modifyId, AuthorizationUtil.getMember().getId())
            .set(notice.modifyTime, LocalDateTime.now())
            .where(notice.id.eq(request.getId()))
            .execute();
    }

    public HashMap<String, Object> findById(Long id) {
        HashMap<String, Object> resultMap = new HashMap<>();

        List<AttachFile.Response> fileList = attachFileService.findByTargetIdAndUseYn("notice", id, "Y");
        if (fileList.size() > 0) {
            resultMap.put("fileList", fileList);
        } else {
            resultMap.put("fileList", null);
        }

        Notice.Response info = jpaQueryFactory
            .select(Projections.constructor(Notice.Response.class,
                notice.id,
                notice.type,
                notice.topYn,
                notice.title,
                notice.contents,
                notice.useYn,
                notice.readCnt,
                notice.registerTime,
                notice.modifyTime,
                ExpressionUtils.as(
                    JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where(member.id.eq(notice.registerId)), "registerName"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where(member.id.eq(notice.modifyId)), "modifyName"
                )
            ))
            .from(notice)
            .where(notice.id.eq(id))
            .fetchOne();

        resultMap.put("info", info);

        return resultMap;
    }

    @Transactional
    public long deleteNotice(Notice.Request request) {
        return jpaQueryFactory.delete(notice)
            .where(notice.id.in(request.getIdArr()))
            .execute();
    }
}
