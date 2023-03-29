package com.admin.util;

import com.admin.domain.attch.file.AttachFile;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import java.io.File;
import java.security.SecureRandom;
import java.util.*;

@Component
public class FileUtil {
    @Value("${global.file.webPath}")
    private String webPath;
    @Value("${global.file.savePath}")
    private String savePath;

    public List<AttachFile> upload(MultipartRequest multipartRequest, String targetTable, Long targetId, Long id) throws Exception {
        List<AttachFile> attachFileList = new ArrayList<AttachFile>();
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        Iterator<Map.Entry<String, MultipartFile>> itr = fileMap.entrySet().iterator();
        MultipartFile mFile;

        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));

        webPath = webPath + year + "/" + month + "/" + day;

        while (itr.hasNext()) {
            Map.Entry<String, MultipartFile> entry = itr.next();
            mFile = entry.getValue();
            int fileSize = (int) mFile.getSize();
            if (fileSize > 0) {
                String os = System.getProperty("os.name").toLowerCase();
                String filePath = savePath;

                if (os.contains("mac")) {
                    filePath = "/Users/em_nb99/IdeaProjects/uploadFiles/admin/";
                }

                filePath = filePath + year + File.separator + month + File.separator + day;

                String realFileName = mFile.getOriginalFilename();
                String fileExt = realFileName.substring(realFileName.lastIndexOf(".") + 1);
                String randomFileName = "FILE_" + RandomStringUtils.random(15, 0, 0, false, true, null, new SecureRandom());
                String saveFileName = randomFileName + "." + fileExt;
                String saveFile = filePath + File.separator + saveFileName;

                File fileFolder = new File(filePath);

                if (!fileFolder.exists()) {
                    if (!fileFolder.mkdirs()) {
                        throw new FileUploadException("==> FileUploadException upload");
                    }
                }

                mFile.transferTo(new File(saveFile));

                AttachFile attachFile = AttachFile.builder()
                    .id(id)
                    .targetId(targetId)
                    .targetTable(targetTable)
                    .realFileName(realFileName)
                    .saveFileName(saveFileName)
                    .fileExt(fileExt)
                    .fileSize(fileSize)
                    .filePath(filePath)
                    .webPath(webPath)
                    .useYn("Y")
                    .build();

                attachFileList.add(attachFile);
            }
        }

        return attachFileList;
    }
}
