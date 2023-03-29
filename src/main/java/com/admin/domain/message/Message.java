package com.admin.domain.message;

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
@Entity(name = "message")
public class Message extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long toMemberId;
    private String contents;
    private String readYn;
    private String useYn;
    private Long registerId;

    @Builder
    public Message(Long id, Long toMemberId, String contents, String readYn, String useYn, Long registerId) {
        this.id = id;
        this.toMemberId = toMemberId;
        this.contents = contents;
        this.readYn = readYn;
        this.useYn = useYn;
        this.registerId = registerId;
    }

    @Setter
    @Getter
    public static class Request {
        private Long id;
        private Long toMemberId;
        private String contents;
        private String readYn;
        private String useYn;
        private Long registerId;

        public Message toEntity() {
            return Message.builder()
                .toMemberId(toMemberId)
                .contents(contents)
                .readYn(readYn)
                .useYn(useYn)
                .registerId(registerId)
                .build();
        }
    }

    @Getter
    public static class Response {
        private final Long id;
        private final Long toMemberId;
        private final String contents;
        private final String readYn;
        private String useYn;
        private final Long registerId;
        private final String registerTime;
        private String modifyTime;
        private String registerName;
        private String saveFileName;
        private String webPath;
        private Long fileId;

        public Response(Object o) {
            Message message = (Message)o;
            id = message.getId();
            toMemberId = message.getToMemberId();
            contents = message.getContents();
            readYn = message.getReadYn();
            useYn = message.getUseYn();
            registerId = message.getRegisterId();
            registerTime = toStringDateTime(message.getRegisterTime());
            modifyTime = toStringDateTime(message.getModifyTime());
        }

        public Response(Long id, Long toMemberId, String contents, String readYn, Long registerId, LocalDateTime registerTime, String registerName, String saveFileName, String webPath, Long fileId) {
            this.id = id;
            this.toMemberId = toMemberId;
            this.contents = contents;
            this.readYn = readYn;
            this.registerId = registerId;
            this.registerTime = toStringDateTime(registerTime);
            this.registerName = registerName;
            this.saveFileName = saveFileName;
            this.webPath = webPath;
            this.fileId = fileId;
        }
    }
}
