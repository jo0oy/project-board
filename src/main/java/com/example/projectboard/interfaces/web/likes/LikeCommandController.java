package com.example.projectboard.interfaces.web.likes;

import com.example.projectboard.application.likes.LikeCommandService;
import com.example.projectboard.security.PrincipalUserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/likes")
@Controller
public class LikeCommandController {

    private final LikeCommandService likeCommandService;

    @PreAuthorize("isAuthenticated() and hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PostMapping("/{articleId}")
    public String pushLike(@PathVariable Long articleId,
                           @AuthenticationPrincipal PrincipalUserAccount principalUserAccount) {

        likeCommandService.pushLike(articleId, principalUserAccount.getUsername());

        return "redirect:/articles/" + articleId;
    }
}
