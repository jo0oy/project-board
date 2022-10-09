package com.example.projectboard.domain.articlecomments;

import com.example.projectboard.domain.JpaAuditingFields;
import com.example.projectboard.domain.articles.Article;
import com.example.projectboard.domain.users.UserAccount;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "article_comments", indexes = {
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class ArticleComment extends JpaAuditingFields {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_comment_id")
    private Long id;

    @Column(nullable = false, length = 500)
    private String content; // 본문
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", updatable = false)
    private Article article; // 게시글 (id)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private UserAccount userAccount;

    @Builder
    private ArticleComment(Article article,
                           String content,
                           UserAccount userAccount) {
        this.article = article;
        this.content = content;
        this.userAccount = userAccount;
    }

    public void update(String content) {
        if(StringUtils.hasText(content)) this.content = content;
    }

    // equals & hashcode override
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment)) return false;
        ArticleComment that = (ArticleComment) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

