package com.admin.domain.inquiry;

import com.admin.domain.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity(name = "inquiry")
public class Inquiry extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String type;
    private String email;
    private String title;
    private String contents;
    private String deviceType;
    private Long registerId;
    private String answer;
    private LocalDateTime answerTime;
    private Long answerId;
    private String answerStatus;

    @Setter
    @Getter
    public static class Request {
        private Long id;
        private String type;
        private String deviceType;
        private String answer;
        private LocalDateTime answerTime;
        private Long answerId;
        private String answerStatus;
        private String search;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String type;
        private final String email;
        private final String title;
        private final String contents;
        private final String deviceType;
        private Long registerId;
        private final String registerTime;
        private final String modifyTime;
        private final String answer;
        private final String answerTime;
        private final Long answerId;
        private final String answerStatus;
        private String answerName;

        public Response(Object o) {
            Inquiry inquiry = (Inquiry)o;
            id = inquiry.getId();
            type = inquiry.getType();
            email = inquiry.getEmail();
            title = inquiry.getTitle();
            contents = inquiry.getContents();
            deviceType = inquiry.getDeviceType();
            registerId = inquiry.getRegisterId();
            registerTime = toStringDateTime(inquiry.getRegisterTime());
            modifyTime = toStringDateTime(inquiry.getModifyTime());
            answer = inquiry.getAnswer();
            answerTime = toStringDateTime(inquiry.getAnswerTime());
            answerId = inquiry.getAnswerId();
            answerStatus = inquiry.getAnswerStatus();
        }

        public Response(Long id, String type, String email, String title, String contents, String deviceType, LocalDateTime registerTime, LocalDateTime modifyTime, String answer, LocalDateTime answerTime, Long answerId, String answerStatus, String answerName) {
            this.id = id;
            this.type = type;
            this.email = email;
            this.title = title;
            this.contents = contents;
            this.deviceType = deviceType;
            this.registerTime = toStringDateTime(registerTime);
            this.modifyTime = toStringDateTime(modifyTime);
            this.answer = answer;
            this.answerTime = toStringDateTime(answerTime);
            this.answerId = answerId;
            this.answerStatus = answerStatus;
            this.answerName = answerName;
        }
    }
}
