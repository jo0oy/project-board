package com.example.projectboard.common.util;

import com.example.projectboard.common.exception.InvalidContentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

    // 입력받은 해시태그 내용 검증 메서드
    public static void validateHashtagContent(List<String> hashtagNames) {
        if (verifyFormat(hashtagNames)) {
            log.debug("올바르지 않은 해시태그 내용 값입니다. Format 문제 발생.");
            throw new InvalidContentException("해시태그는 '#'로 시작하는 공백이 없는 문자열이어야 합니다. " +
                    "다수의 해시태그를 입력할 경우, 구분자는 ','와 '/'만 가능합니다.");
        }

        if (verifyHashtagSize(hashtagNames)) {
            log.debug("올바르지 않은 해시태그 내용 값입니다. Size 문제 발생.");
            throw new InvalidContentException("해시태그는 최대 10개까지 입력 가능합니다.");
        }
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
