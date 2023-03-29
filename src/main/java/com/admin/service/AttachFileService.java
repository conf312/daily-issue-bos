package com.admin.service;

import com.admin.domain.attch.file.AttachFile;
import com.admin.domain.attch.file.AttachFileRepository;
import com.admin.domain.attch.file.QAttachFile;
import com.admin.util.FileUtil;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AttachFileService {
    private final AttachFileRepository attachFileRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private final QAttachFile attachFile = QAttachFile.attachFile;
    private final FileUtil fileUtil;

    public boolean upload(MultipartRequest multipartRequest, String targetTable, Long targetId, Long id) throws Exception {

        List<AttachFile> attachFileList = fileUtil.upload(multipartRequest, targetTable, targetId, id);

        if (attachFileList.size() < 1)
            return false;

        if (id != null)
            return updateFileInfo(attachFileList.get(0)) > 0;
        else
            return attachFileRepository.saveAll(attachFileList).size() > 0;
    }

    public AttachFile.Response findById(Long id) {
        return new AttachFile.Response(attachFileRepository.findById(id).get());
    }

    public List<AttachFile.Response> findByTargetIdAndUseYn(String targetTable, Long targetId, String useYn) {
        return jpaQueryFactory.from(attachFile)
            .where(
                attachFile.targetTable.eq(targetTable),
                attachFile.targetId.eq(targetId),
                attachFile.useYn.eq(useYn)
            )
            .orderBy(attachFile.registerTime.desc())
            .fetch()
            .stream()
            .map(AttachFile.Response::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public long updateUseYn(AttachFile.Request request) {
        return jpaQueryFactory.update(attachFile)
            .set(attachFile.useYn, request.getUseYn())
            .set(attachFile.modifyTime, LocalDateTime.now())
            .where(attachFile.id.eq(request.getId()))
            .execute();
    }

    @Transactional
    public long updateFileInfo(AttachFile request) {
        return jpaQueryFactory.update(attachFile)
            .set(attachFile.realFileName, request.getRealFileName())
            .set(attachFile.saveFileName, request.getSaveFileName())
            .set(attachFile.fileSize, request.getFileSize())
            .set(attachFile.fileExt, request.getFileExt())
            .set(attachFile.filePath, request.getFilePath())
            .set(attachFile.webPath, request.getWebPath())
            .set(attachFile.modifyTime, LocalDateTime.now())
            .where(attachFile.id.eq(request.getId()))
            .execute();
    }
}
