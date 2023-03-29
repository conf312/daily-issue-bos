package com.admin.domain.faq;

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
@Entity(name = "faq")
public class Faq extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String type;
    private String title;
    private String contents;
    private String useYn;
    private Long registerId;
    private Long modifyId;

    @Builder
    public Faq(Long id, String type, String title, String contents, String useYn, Long registerId, Long modifyId) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.contents = contents;
        this.useYn = useYn;
        this.registerId = registerId;
        this.modifyId = modifyId;
    }

    @Setter
    @Getter
    public static class Request {
        private Long[] idArr;
        private Long id;
        private String type;
        private String title;
        private String contents;
        private String useYn;
        private Long registerId;
        private Long modifyId;
        private String search;

        public Faq toEntity() {
            return Faq.builder()
                .type(type)
                .title(title)
                .contents(contents)
                .useYn(useYn)
                .registerId(registerId)
                .modifyId(modifyId)
                .build();
        }
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String type;
        private final String title;
        private final String contents;
        private final String useYn;
        private Long registerId;
        private Long modifyId;
        private final String registerTime;
        private final String modifyTime;
        private String registerName;
        private String modifyName;

        public Response(Object o) {
            Faq faq = (Faq)o;
            id = faq.getId();
            type = faq.getType();
            title = faq.getTitle();
            contents = faq.getContents();
            useYn = faq.getUseYn();
            registerId = faq.getRegisterId();
            modifyId = faq.getModifyId();
            registerTime = toStringDateTime(faq.getRegisterTime());
            modifyTime = toStringDateTime(faq.getModifyTime());
        }

        public Response(Long id, String type, String title, String contents, String useYn, LocalDateTime registerTime, LocalDateTime modifyTime, String registerName, String modifyName) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.contents = contents;
            this.useYn = useYn;
            this.registerTime = toStringDateTime(registerTime);
            this.modifyTime = toStringDateTime(modifyTime);
            this.registerName = registerName;
            this.modifyName = modifyName;
        }
    }
}
