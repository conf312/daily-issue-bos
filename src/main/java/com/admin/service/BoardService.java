package com.admin.service;

import com.admin.domain.board.Board;
import com.admin.domain.board.BoardRepository;
import com.admin.domain.board.QBoard;
import com.admin.domain.member.QMember;
import com.admin.util.AuthorizationUtil;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final AttachFileService attachFileService;
    private final JPAQueryFactory jpaQueryFactory;

    private QBoard board = QBoard.board;
    private QMember member = QMember.member;

    public BooleanExpression eqType(String type) {
        if (!StringUtils.hasText(type)) return null;
        return board.type.eq(type);
    }

    public BooleanExpression containsSearch(String serach) {
        if (!StringUtils.hasText(serach)) return null;
        return board.title.containsIgnoreCase(serach).or(board.contents.containsIgnoreCase(serach));
    }

    public Long saveBoard(Board.Request request, MultipartHttpServletRequest multipartRequest) throws Exception {
        request.setRegisterId(AuthorizationUtil.getMember().getId());
        request.setModifyId(AuthorizationUtil.getMember().getId());
        request.setReadCnt(0L);

        Long id = boardRepository.save(request.toEntity(request)).getId();

        if (id > 0)
            attachFileService.upload(multipartRequest,"board", id, null);

        return id;
    }

    public HashMap<String, Object> findAll(Board.Request request, Integer page, Integer pageSize) {
        HashMap<String, Object> resultMap = new HashMap<>();

       List<Board.Response> list = jpaQueryFactory
                .select(Projections.constructor(Board.Response.class,
                    board.id,
                    board.type,
                    board.title,
                    board.readCnt,
                    board.registerTime,
                    ExpressionUtils.as(
                        JPAExpressions
                        .select(member.name)
                        .from(member)
                        .where(member.id.eq(board.registerId)), "registerName")
                ))
                .from(board)
                .where(
                    containsSearch(request.getSearch()),
                    eqType(request.getType())
                )
                .offset(page)
                .limit(pageSize)
                .orderBy(board.registerTime.desc())
                .fetch();

        Long totalCnt = (long) jpaQueryFactory.select(board.count()).from(board)
                .where(
                    containsSearch(request.getSearch()),
                    eqType(request.getType())
                ).fetchOne();

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
    public HashMap<String, Object> findById(Long id) {
        HashMap<String, Object> resultMap = new HashMap<>();
        Board.Response info = jpaQueryFactory.select(Projections.constructor(Board.Response.class,
            board.id,
            board.type,
            board.startDate,
            board.endDate,
            board.allTimeYn,
            board.title,
            board.contents,
            board.videoUrl,
            board.readCnt,
            board.registerTime,
            board.modifyTime,
            ExpressionUtils.as(
                JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where(member.id.eq(board.registerId)), "registerName"),
            ExpressionUtils.as(
                JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where(member.id.eq(board.registerId)), "modifyName"))
        )
        .from(board)
        .where(board.id.eq(id))
        .fetchOne();

        jpaQueryFactory.update(board)
            .set(board.readCnt, info.getReadCnt() + 1)
            .where(board.id.eq(id))
            .execute();

        resultMap.put("info", info);
        resultMap.put("fileList", attachFileService.findByTargetIdAndUseYn("board", id, "Y"));

        return resultMap;
    }

    @Transactional
    public Long updateBoard(Board.Request request, MultipartHttpServletRequest multipartRequest) throws Exception {

        attachFileService.upload(multipartRequest,"board", request.getId(),null);

        return jpaQueryFactory.update(board)
            .set(board.type, request.getType())
            .set(board.startDate, request.getStartDate())
            .set(board.endDate, request.getEndDate())
            .set(board.allTimeYn, request.getAllTimeYn())
            .set(board.title, request.getTitle())
            .set(board.contents, request.getContents())
            .set(board.videoUrl, request.getVideoUrl())
            .set(board.modifyId, AuthorizationUtil.getMember().getId())
            .set(board.modifyTime, LocalDateTime.now())
            .where(board.id.eq(request.getId()))
            .execute();
    }

    @Transactional
    public Long deleteBoard(Board.Request request) {
        return jpaQueryFactory.delete(board)
            .where(board.id.in(request.getIdArr()))
            .execute();
    }
}
