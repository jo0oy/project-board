package com.example.projectboard.interfaces.web.hashtags;

import com.example.projectboard.application.PaginationService;
import com.example.projectboard.application.hashtags.HashtagQueryService;
import com.example.projectboard.interfaces.dto.hashtags.HashtagDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HashtagViewController {

    private final HashtagQueryService hashtagQueryService;
    private final PaginationService paginationService;
    private final HashtagDtoMapper hashtagDtoMapper;

    @GetMapping("/hashtags")
    public String hashtagList(@PageableDefault(size = 20, sort = "hashtagName") Pageable pageable,
                              Model model) {

        var hashtags = hashtagQueryService.hashtags(pageable).map(hashtagDtoMapper::toDto);
        var barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), hashtags.getTotalPages());

        model.addAttribute("hashtags", hashtags);
        model.addAttribute("paginationBar", barNumbers);

        return "hashtags/hashtag-list";
    }
}
