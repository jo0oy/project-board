package com.example.projectboard.application.likes;

import com.example.projectboard.common.exception.EntityNotFoundException;
import com.example.projectboard.common.exception.UsernameNotFoundException;
import com.example.projectboard.domain.articles.ArticleRepository;
import com.example.projectboard.domain.likes.Like;
import com.example.projectboard.domain.likes.LikeRepository;
import com.example.projectboard.domain.users.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class LikeCommandServiceImpl implements LikeCommandService {

    private final LikeRepository likeRepository;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    /**
     * '좋아요' push 메서드 ('좋아요' on/off)
     * @param articleId : push 하려는 게시글 id (Long)
     * @param principalUsername : push 하려는 유저(현재 로그인된 유저)의 username (String)
     */
    @Transactional
    @Override
    public void pushLike(Long articleId, String principalUsername) {
        log.info("{}:{}", getClass().getSimpleName(), "pushLike(Long, String)");

        // 게시글(Article) 엔티티 조회
        var article = articleRepository.findById(articleId)
                .orElseThrow(EntityNotFoundException::new);

        // 작성자(UserAccount) 엔티티 조회
        var userAccount = userAccountRepository.findByUsername(principalUsername).orElseThrow(() ->
                new UsernameNotFoundException("유저를 찾을 수 없습니다. username=" + principalUsername));

        // 좋아요가 눌러있는 상태 -> 좋아요 취소, 그렇지 않다면 Like 엔티티 생성 후 저장!
        var like = alreadyPushedLike(articleId, userAccount.getId());

        if (like.isPresent()) {
            log.info("already pushed LIKE then delete entity");
            likeRepository.delete(like.get());
        } else {
            log.info("saving new LIKE entity");
            likeRepository.save(Like.of(userAccount, article));
        }
    }

    // 이미 push 된 '좋아요'인지 여부 확인 메서드
    private Optional<Like> alreadyPushedLike(Long articleId, Long userId) {
        log.info("이미 눌린 '좋아요'인지 체크하는 로직 실행");
        return likeRepository.findByArticle_IdAndUserAccount_Id(articleId, userId);
    }
}
