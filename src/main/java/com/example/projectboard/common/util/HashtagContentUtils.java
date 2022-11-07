package com.example.projectboard.common.util;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HashtagContentUtils {

    public static List<String> convertToList(String hashtagContent) {
        List<String> hashtagNames = new ArrayList<>();

        if (StringUtils.hasText(hashtagContent)) {
            hashtagNames = Arrays.stream(hashtagContent.trim().split(",|, |/|/ | "))
                    .filter(StringUtils::hasText)
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

        return hashtagNames;
    }

    public static boolean verifyFormat(List<String> hashtagNames) {
        if (hashtagNames != null && hashtagNames.size() > 0) {
            for (String hashtagName : hashtagNames) {
                if (!hashtagName.startsWith("#")) {
                    return true;
                } else if (hashtagName.substring(1).contains("#")) {
                    return true;
                } else if (hashtagName.equals("#")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean verifyHashtagSize(List<String> hashtagNames) {
        return hashtagNames != null && hashtagNames.size() > 10;
    }
}
