package com.admin.domain.attch.file;

import com.admin.domain.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor
@Getter
@Entity(name = "attach_file")
public class AttachFile extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long targetId;
    private String targetTable;
    private String realFileName;
    private String saveFileName;
    private String fileExt;
    private int fileSize;
    private String filePath;
    private String webPath;
    private String useYn;

    @Builder
    public AttachFile(Long id, Long targetId, String targetTable, String realFileName, String saveFileName, String fileExt, int fileSize, String filePath, String webPath, String useYn) {
        this.id = id;
        this.targetId = targetId;
        this.targetTable = targetTable;
        this.realFileName = realFileName;
        this.saveFileName = saveFileName;
        this.fileExt = fileExt;
        this.fileSize = fileSize;
        this.filePath = filePath;
        this.webPath = webPath;
        this.useYn = useYn;
    }

    @Setter
    @Getter
    public static class Request {
        private Long id;
        private Long targetId;
        private String targetTable;
        private String realFileName;
        private String saveFileName;
        private String fileExt;
        private int fileSize;
        private String filePath;
        private String webPath;
        private String useYn;

        public AttachFile toEntity() {
            return AttachFile.builder()
                .targetId(targetId)
                .targetTable(targetTable)
                .realFileName(realFileName)
                .saveFileName(saveFileName)
                .fileExt(fileExt)
                .fileSize(fileSize)
                .filePath(filePath)
                .webPath(webPath)
                .useYn(useYn)
                .build();
        }
    }

    @Getter
    public static class Response {
        private final Long id;
        private final Long targetId;
        private final String targetTable;
        private final String realFileName;
        private final String saveFileName;
        private final String fileExt;
        private final int fileSize;
        private final String filePath;
        private final String webPath;
        private final String useYn;

        public Response(Object o) {
            AttachFile attachFile = (AttachFile)o;
            id = attachFile.getId();
            targetId = attachFile.getTargetId();
            targetTable = attachFile.getTargetTable();
            realFileName = attachFile.getRealFileName();
            saveFileName = attachFile.getSaveFileName();
            fileExt = attachFile.getFileExt();
            fileSize = attachFile.getFileSize();
            filePath = attachFile.getFilePath();
            webPath = attachFile.getWebPath();
            useYn = attachFile.getUseYn();
        }
    }
}
