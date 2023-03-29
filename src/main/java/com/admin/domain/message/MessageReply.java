package com.admin.domain.message;

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
@Entity(name = "message_reply")
public class MessageReply extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private Long messageId;
    private String contents;
    private String useYn;
    private Long registerId;

    @Builder
    public MessageReply(Long id, Long messageId, String contents, String useYn, Long registerId) {
        this.id = id;
        this.messageId = messageId;
        this.contents = contents;
        this.useYn = useYn;
        this.registerId = registerId;
    }

    @Setter
    @Getter
    public static class Request {
        private Long id;
        private Long messageId;
        private String contents;
        private String useYn;
        private Long registerId;

        public MessageReply toEntity() {
            return MessageReply.builder()
                .messageId(messageId)
                .contents(contents)
                .useYn(useYn)
                .registerId(registerId)
                .build();
        }
    }

    @Getter
    public static class Response {
        private final Long id;
        private final Long messageId;
        private final String contents;
        private final String useYn;
        private final Long registerId;
        private final String registerTime;
        private final String modifyTime;

        public Response(Object o) {
            MessageReply message = (MessageReply)o;
            id = message.getId();
            messageId = message.getMessageId();
            contents = message.getContents();
            useYn = message.getUseYn();
            registerId = message.getRegisterId();
            registerTime = toStringDateTime(message.getRegisterTime());
            modifyTime = toStringDateTime(message.getModifyTime());
        }
    }
}
