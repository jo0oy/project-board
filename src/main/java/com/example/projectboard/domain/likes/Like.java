package com.example.projectboard.domain.likes;

import com.example.projectboard.domain.JpaAuditingDateTimeFields;
import com.example.projectboard.domain.articles.Article;
import com.example.projectboard.domain.users.UserAccount;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "likes")
@Entity
public class Like extends JpaAuditingDateTimeFields {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    private Like(UserAccount userAccount, Article article) {
        this.userAccount = userAccount;
        this.article = article;
    }

    public static Like of(UserAccount userAccount, Article article) {
        return new Like(userAccount, article);
    }
}
