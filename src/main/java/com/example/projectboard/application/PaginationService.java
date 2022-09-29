package com.example.projectboard.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class PaginationService {

    private static final int BAR_LENGTH = 5;

    /**
     * @param currentPageNumber : int : 현재 페이지 번호 (0부터 시작)
     * @param totalPages : int : 총 페이지 수
     * @return List<Integer> ( 현재 페이지 번호가 센터에 오는 길이가 5인 페이지 바 )
     */
    public List<Integer> getPaginationBarNumbers(int currentPageNumber, int totalPages) {
        log.info("{}: {}", getClass().getSimpleName(), "getPaginationBarNumbers(int, int)");

        // 음수가 나올 경우 (현재 페이지가 0 or 1인 경우) 0을 시작 번호로 한다.
        int startNumber = Math.max(currentPageNumber - (BAR_LENGTH / 2), 0);

        // startNumber + BAR_LENGTH 가 총 페이지 수를 초과할 경우, totalPages 를 끝번호로 한다.
        int endNumber = Math.min(startNumber + BAR_LENGTH, totalPages);

        return IntStream.range(startNumber, endNumber).boxed().collect(Collectors.toList());
    }

    public int currentBarLength() {
        return BAR_LENGTH;
    }
}
