package com.admin.service;

import com.admin.domain.attch.file.QAttachFile;
import com.admin.domain.member.QMember;
import com.admin.domain.message.Message;
import com.admin.domain.message.MessageRepository;
import com.admin.domain.message.QMessage;
import com.admin.util.AuthorizationUtil;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final QMessage message = QMessage.message;
    private final QMember member = QMember.member;
    private final QAttachFile attachFile = QAttachFile.attachFile;

    public Long save(Message.Request request) {
        request.setRegisterId(AuthorizationUtil.getMember().getId());
        return messageRepository.save(request.toEntity()).getId();
    }

    public HashMap<String, Object>findAll(Message.Request request, Integer page, Integer pageSize) {
        HashMap<String, Object> resultMap = new HashMap<>();

        List<Message.Response> list = jpaQueryFactory
            .select(Projections.constructor(Message.Response.class,
                message.id,
                message.toMemberId,
                message.contents,
                message.readYn,
                message.registerId,
                message.registerTime,
                ExpressionUtils.as(
                    JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where(member.id.eq(message.registerId)), "registerName"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(attachFile.saveFileName)
                    .from(attachFile)
                    .where(
                        attachFile.targetId.eq(message.registerId),
                        attachFile.targetTable.eq("member")
                    ), "saveFileName"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(attachFile.webPath)
                    .from(attachFile)
                    .where(
                        attachFile.targetId.eq(message.registerId),
                        attachFile.targetTable.eq("member")
                    ), "webPath"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(attachFile.id)
                    .from(attachFile)
                    .where(
                        attachFile.targetId.eq(message.registerId),
                        attachFile.targetTable.eq("member")
                    ), "fileId"
                )
            ))
            .from(message)
            .where(
                message.toMemberId.eq(AuthorizationUtil.getMember().getId()),
                message.useYn.eq("Y")
            )
            .offset(page)
            .limit(pageSize)
            .orderBy(message.registerTime.desc())
            .fetch();

        resultMap.put("list", list);

        return resultMap;
    }

    public HashMap<String, Object>findByTop5() {
        HashMap<String, Object> resultMap = new HashMap<>();

        List<Message.Response> list = jpaQueryFactory
            .select(Projections.constructor(Message.Response.class,
                message.id,
                message.toMemberId,
                message.contents,
                message.readYn,
                message.registerId,
                message.registerTime,
                ExpressionUtils.as(
                    JPAExpressions
                    .select(member.name)
                    .from(member)
                    .where(member.id.eq(message.registerId)), "registerName"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(attachFile.saveFileName)
                    .from(attachFile)
                    .where(
                        attachFile.targetId.eq(message.registerId),
                        attachFile.targetTable.eq("member")
                    ), "saveFileName"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(attachFile.webPath)
                    .from(attachFile)
                    .where(
                        attachFile.targetId.eq(message.registerId),
                        attachFile.targetTable.eq("member")
                    ), "webPath"
                ),
                ExpressionUtils.as(
                    JPAExpressions
                    .select(attachFile.id)
                    .from(attachFile)
                    .where(
                        attachFile.targetId.eq(message.registerId),
                        attachFile.targetTable.eq("member")
                    ), "fileId"
                )
            ))
            .from(message)
            .where(
                message.toMemberId.eq(AuthorizationUtil.getMember().getId()),
                message.readYn.eq("N")
            )
            .limit(5)
            .orderBy(message.registerTime.desc())
            .fetch();

        resultMap.put("list", list);

        return resultMap;
    }

    @Transactional
    public long updateReadYn(Message.Request request) {
        return jpaQueryFactory.update(message)
            .set(message.readYn, request.getReadYn())
            .set(message.modifyTime, LocalDateTime.now())
            .where(message.id.eq(request.getId()))
            .execute();
    }

    @Transactional
    public long updateUseYn(Message.Request request) {
        return jpaQueryFactory.update(message)
            .set(message.useYn, request.getUseYn())
            .set(message.modifyTime, LocalDateTime.now())
            .where(message.id.eq(request.getId()))
            .execute();
    }
}
