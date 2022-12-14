package com.example.projectboard.domain.articlecomments;

import com.example.projectboard.domain.JpaAuditingFields;
import com.example.projectboard.domain.articles.Article;
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
    private String commentBody; // 본문
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", updatable = false)
    private Article article; // 게시글 (id)

    @Column(nullable = false, updatable = false)
    private Long userId; // 연관관계 매핑: UserAccount id

    @Builder
    private ArticleComment(Article article,
                           String commentBody,
                           Long userId) {
        this.article = article;
        this.commentBody = commentBody;
        this.userId = userId;
    }

    public void update(String content) {
        if(StringUtils.hasText(content)) this.commentBody = content;
    }

    // equals & hashcode override
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ArticleComment comment = (ArticleComment) o;
        return this.getId() != null && this.getId().equals(comment.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

