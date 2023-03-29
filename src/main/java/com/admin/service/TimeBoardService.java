package com.admin.service;

import com.admin.domain.attch.file.QAttachFile;
import com.admin.domain.member.QMember;
import com.admin.domain.timeboard.QTimeBoard;
import com.admin.domain.timeboard.TimeBoard;
import com.admin.domain.timeboard.TimeBoardRepository;
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
public class TimeBoardService {
    private final TimeBoardRepository timeBoardRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final QTimeBoard timeBoard = QTimeBoard.timeBoard;
    private final QMember member = QMember.member;
    private final QAttachFile attachFile = QAttachFile.attachFile;
    private final AttachFileService attachFileService;

    public BooleanExpression eqAlwaysYn(String alwaysYn) {
        if (!StringUtils.hasText(alwaysYn)) return null;
        return timeBoard.alwaysYn.eq(alwaysYn);
    }

    public BooleanExpression eqUseYn(String useYn) {
        if (!StringUtils.hasText(useYn)) return null;
        return timeBoard.useYn.eq(useYn);
    }

    public BooleanExpression containsSearch(String search) {
        if (!StringUtils.hasText(search)) return null;
        return timeBoard.title.containsIgnoreCase(search).or(timeBoard.contents.containsIgnoreCase(search));
    }

    public Long save(TimeBoard.Request request, MultipartRequest multipartRequest) throws Exception {

        request.setRegisterId(AuthorizationUtil.getMember().getId());
        Long result = timeBoardRepository.save(request.toEntity()).getId();

        if (result > 0) {
            attachFileService.upload(multipartRequest, "time-board", result, null);
        }

        return result;
    }

    public HashMap<String, Object>findAll(TimeBoard.Request request, Integer page, Integer pageSize) {
        HashMap<String, Object> resultMap = new HashMap<>();

        List<TimeBoard.Response> list = jpaQueryFactory
            .from(timeBoard)
            .where(
                eqAlwaysYn(request.getAlwaysYn()),
                eqUseYn(request.getUseYn()),
                containsSearch(request.getSearch())
            )
            .offset(page)
            .limit(pageSize)
            .orderBy(timeBoard.registerTime.desc())
            .fetch()
            .stream()
            .map(TimeBoard.Response::new)
            .collect(Collectors.toList());

        Long totalCnt = jpaQueryFactory
            .select(timeBoard.count())
            .from(timeBoard)
            .where(
                eqAlwaysYn(request.getAlwaysYn()),
                eqUseYn(request.getUseYn()),
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

    public TimeBoard.Response findById(Long id) {
        return jpaQueryFactory
            .select(Projections.constructor(TimeBoard.Response.class,
                timeBoard.id,
                timeBoard.title,
                timeBoard.contents,
                timeBoard.alwaysYn,
                timeBoard.startDate,
                timeBoard.endDate,
                timeBoard.useYn,
                timeBoard.registerTime,
                timeBoard.modifyTime,
                ExpressionUtils.as(
                    JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where(member.id.eq(timeBoard.registerId)), "registerName"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where(member.id.eq(timeBoard.modifyId)), "modifyName"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(attachFile.saveFileName)
                    .from(attachFile)
                    .where(
                        attachFile.targetId.eq(timeBoard.id),
                        attachFile.targetTable.eq("time-board")
                    ), "saveFileName"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(attachFile.webPath)
                    .from(attachFile)
                    .where(
                        attachFile.targetId.eq(timeBoard.id),
                        attachFile.targetTable.eq("time-board")
                    ), "webPath"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(attachFile.id)
                    .from(attachFile)
                    .where(
                        attachFile.targetId.eq(timeBoard.id),
                        attachFile.targetTable.eq("time-board")
                    ), "fileId"
                )
            ))
            .from(timeBoard)
            .where(
                timeBoard.id.eq(id)
            )
            .fetchOne();
    }

    @Transactional
    public long updateTimeBoard(TimeBoard.Request request, MultipartRequest multipartRequest) throws Exception {
        attachFileService.upload(multipartRequest, "time-board", request.getId(), request.getFileId());
        return jpaQueryFactory.update(timeBoard)
            .set(timeBoard.title, request.getTitle())
            .set(timeBoard.contents, request.getContents())
            .set(timeBoard.alwaysYn, request.getAlwaysYn())
            .set(timeBoard.startDate, request.getStartDate())
            .set(timeBoard.endDate, request.getEndDate())
            .set(timeBoard.useYn, request.getUseYn())
            .set(timeBoard.modifyId, AuthorizationUtil.getMember().getId())
            .set(timeBoard.modifyTime, LocalDateTime.now())
            .where(timeBoard.id.eq(request.getId()))
            .execute();
    }

    @Transactional
    public long deleteTimeBoard(TimeBoard.Request request) {
        return jpaQueryFactory.delete(timeBoard)
            .where(timeBoard.id.in(request.getIdArr()))
            .execute();
    }
}
