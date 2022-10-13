package com.example.projectboard.application.users;

import com.example.projectboard.domain.users.UserAccountCommand;
import com.example.projectboard.domain.users.UserAccountInfo;

public interface UserAccountCommandService {

    UserAccountInfo registerUser(UserAccountCommand.RegisterReq command);

    void updateUserInfo(Long userAccountId, String principalUsername, UserAccountCommand.UpdateReq command);

    void delete(Long userAccountId, String principalUsername);

    boolean verifyDuplicateUsername(String username);

    boolean verifyDuplicateEmail(String email);
}
