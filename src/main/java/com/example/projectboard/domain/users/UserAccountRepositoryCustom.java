package com.example.projectboard.domain.users;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserAccountRepositoryCustom {

    Page<UserAccount> findAllBySearchCondition(UserAccountSearchCondition condition, Pageable pageable);

    List<UserAccount> findAllBySearchCondition(UserAccountSearchCondition condition);
}
