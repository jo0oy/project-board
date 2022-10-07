package com.example.projectboard.interfaces.web.users;

import com.example.projectboard.application.users.UserAccountCommandService;
import com.example.projectboard.interfaces.dto.users.UserAccountDto;
import com.example.projectboard.interfaces.dto.users.UserAccountDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserAccountCommandController {

    private final UserAccountCommandService userAccountCommandService;
    private final UserAccountDtoMapper userAccountDtoMapper;

    @PostMapping("/user-accounts")
    public String registerUser(UserAccountDto.RegisterReq req) {
        log.info("userAccountDto.RegisterReq={}", req);
        userAccountCommandService.registerUser(userAccountDtoMapper.toCommand(req));

        return "redirect:/";
    }
}
