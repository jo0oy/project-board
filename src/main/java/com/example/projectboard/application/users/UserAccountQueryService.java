package com.example.projectboard.application.users;

import com.example.projectboard.domain.users.UserAccountCommand;
import com.example.projectboard.domain.users.UserAccountInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserAccountQueryService {

    UserAccountInfo getUserAccountInfo(Long id, String principalUsername);

    UserAccountInfo getUserAccountInfo(String username, String principalUsername);

    Page<UserAccountInfo> userAccounts(UserAccountCommand.SearchCondition condition, Pageable pageable);

    List<UserAccountInfo> userAccountList(UserAccountCommand.SearchCondition condition);
}
