package com.admin.domain.timeboard;

import com.admin.domain.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Time;
import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
@Entity(name = "time_board")
public class TimeBoard extends BaseTime {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String title;
    private String contents;
    private String alwaysYn;
    private String startDate;
    private String endDate;
    private String useYn;
    private Long registerId;
    private Long modifyId;

    @Builder
    public TimeBoard(Long id, String title, String contents, String alwaysYn, String startDate, String endDate, String useYn, Long registerId, Long modifyId) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.alwaysYn = alwaysYn;
        this.startDate = startDate;
        this.endDate = endDate;
        this.useYn = useYn;
        this.registerId = registerId;
        this.modifyId = modifyId;
    }

    @Setter
    @Getter
    public static class Request {
        private Long[] idArr;
        private Long id;
        private String title;
        private String contents;
        private String alwaysYn;
        private String startDate;
        private String endDate;
        private String useYn;
        private Long registerId;
        private Long modifyId;
        private String search;
        private Long fileId;

        public TimeBoard toEntity() {
            return TimeBoard.builder()
                .title(title)
                .contents(contents)
                .alwaysYn(alwaysYn)
                .startDate(startDate)
                .endDate(endDate)
                .useYn(useYn)
                .registerId(registerId)
                .modifyId(modifyId)
                .build();
        }
    }

    @Getter
    public static class Response {
        private Long id;
        private String title;
        private String contents;
        private String alwaysYn;
        private String startDate;
        private String endDate;
        private String useYn;
        private String registerName;
        private String modifyName;
        private final String registerTime;
        private String modifyTime;

        private String saveFileName;
        private String webPath;
        private Long fileId;

        public Response(Object o) {
            TimeBoard timeBoard = (TimeBoard)o;
            id = timeBoard.getId();
            title = timeBoard.getTitle();
            contents = timeBoard.getContents();
            alwaysYn = timeBoard.getAlwaysYn();
            startDate = timeBoard.getStartDate();
            endDate = timeBoard.getEndDate();
            useYn = timeBoard.getUseYn();
            registerTime = toStringDateTime(timeBoard.getRegisterTime());
        }

        public Response(Long id, String title, String contents, String alwaysYn, String startDate, String endDate, String useYn, LocalDateTime registerTime, LocalDateTime modifyTime, String registerName, String modifyName, String saveFileName, String webPath, Long fileId) {
            this.id = id;
            this.title = title;
            this.contents = contents;
            this.alwaysYn = alwaysYn;
            this.startDate = startDate;
            this.endDate = endDate;
            this.useYn = useYn;
            this.registerTime = toStringDateTime(registerTime);
            this.modifyTime = toStringDateTime(modifyTime);
            this.registerName = registerName;
            this.modifyName = modifyName;
            this.saveFileName = saveFileName;
            this.webPath = webPath;
            this.fileId = fileId;
        }
    }
}
