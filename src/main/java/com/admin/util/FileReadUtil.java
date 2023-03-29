package com.admin.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReadUtil {
    private String path;
    public FileReadUtil(String path) {
        this.path = path;
    }

    public String getHtmlToString(String template) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            path = "/Users/venh/IdeaProjects/admin/src/main/resources/templates/mail/";
        }
        return new String(Files.readAllBytes(Paths.get(path + template)));
    }
}
