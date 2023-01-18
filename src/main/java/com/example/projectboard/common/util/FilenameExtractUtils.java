package com.example.projectboard.common.util;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
public class FilenameExtractUtils {

    public static List<String> extract(String content) {
        String imgRegex = "<img[^>]*src=[\"']?([^>\"']+)[\"']?[^>]*>";

        var pattern = Pattern.compile(imgRegex);
        var matcher = pattern.matcher(content);

        var result = new ArrayList<String>();
        while (matcher.find()) {
            var path = matcher.group(1);
            var filename = path.trim().substring(path.lastIndexOf("/") + 1);
            log.info("extracted filename = {}", filename);

            result.add(filename);
        }

        return result;
    }
}
