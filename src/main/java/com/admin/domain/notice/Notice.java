package com.admin.domain.notice;

import com.admin.domain.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@DynamicInsert
@NoArgsConstructor
@Getter
@Entity(name = "notice")
public class Notice extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String type;
    private String topYn;
    private String title;
    private String contents;
    private String useYn;
    private Long readCnt;
    private Long registerId;
    private Long modifyId;

    @Builder
    public Notice(Long id, String type, String topYn, String title, String contents, String useYn, Long readCnt, Long registerId, Long modifyId) {
        this.id = id;
        this.type = type;
        this.topYn = topYn;
        this.title = title;
        this.contents = contents;
        this.useYn = useYn;
        this.readCnt = readCnt;
        this.registerId = registerId;
        this.modifyId = modifyId;
    }

    @Setter
    @Getter
    public static class Request {
        private Long[] idArr;
        private Long id;
        private String type;
        private String topYn;
        private String title;
        private String contents;
        private String useYn;
        private Long readCnt;
        private Long registerId;
        private Long modifyId;
        private String search;

        public Notice toEntity() {
            return Notice.builder()
                .type(type)
                .topYn(topYn)
                .title(title)
                .contents(contents)
                .useYn(useYn)
                .readCnt(readCnt)
                .registerId(registerId)
                .modifyId(modifyId)
                .build();
        }
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String type;
        private final String topYn;
        private final String title;
        private final String contents;
        private final String useYn;
        private final Long readCnt;
        private Long registerId;
        private Long modifyId;
        private final String registerTime;
        private final String modifyTime;
        private String registerName;
        private String modifyName;

        public Response(Object o) {
            Notice notice = (Notice)o;
            id = notice.getId();
            type = notice.getType();
            topYn = notice.getTopYn();
            title = notice.getTitle();
            contents = notice.getContents();
            useYn = notice.getUseYn();
            readCnt = notice.getReadCnt();
            registerId = notice.getRegisterId();
            modifyId = notice.getModifyId();
            registerTime = toStringDateTime(notice.getRegisterTime());
            modifyTime = toStringDateTime(notice.getModifyTime());
        }

        public Response(Long id, String type, String topYn, String title, String contents, String useYn, Long readCnt, LocalDateTime registerTime, LocalDateTime modifyTime, String registerName, String modifyName) {
            this.id = id;
            this.type = type;
            this.topYn = topYn;
            this.title = title;
            this.contents = contents;
            this.useYn = useYn;
            this.readCnt = readCnt;
            this.registerTime = toStringDateTime(registerTime);
            this.modifyTime = toStringDateTime(modifyTime);
            this.registerName = registerName;
            this.modifyName = modifyName;
        }
    }
}
