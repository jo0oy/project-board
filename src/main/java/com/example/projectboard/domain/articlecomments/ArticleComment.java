package com.example.projectboard.domain.articlecomments;

import com.example.projectboard.domain.JpaAuditingFields;
import com.example.projectboard.domain.articles.Article;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "article_comments", indexes = {
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class ArticleComment extends JpaAuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_comment_id")
    private Long id;

    @Column(nullable = false, length = 500)
    private String commentBody; // 본문
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", updatable = false)
    private Article article; // 게시글 (id)

    @Column(nullable = false, updatable = false)
    private Long userId; // 연관관계 매핑: UserAccount id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", updatable = false)
    private ArticleComment parent;

    @ToString.Exclude
    @OrderBy("createdAt ASC") // 대댓글은 최신순으로 정렬한다.
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<ArticleComment> childs = new LinkedHashSet<>();

    private ArticleComment(String commentBody,
                           Article article,
                           Long userId,
                           ArticleComment parent) {
        this.commentBody = commentBody;
        this.article = article;
        this.userId = userId;
        this.parent = parent;
    }

    public static ArticleComment of(String commentBody,
                                    Article article,
                                    Long userId) {

        return new ArticleComment(commentBody, article, userId, null);
    }

    public static ArticleComment of(String commentBody,
                                    Article article,
                                    Long userId,
                                    ArticleComment parent) {

        return new ArticleComment(commentBody, article, userId, parent);
    }

    public void setParent(ArticleComment parent) {
        this.parent = parent;
    }

    // 부모-자식 양방향 매핑 메서드
    public void addChild(ArticleComment child) {
        child.setParent(this);
        this.getChilds().add(child);
    }

    public void update(String content) {
        if (StringUtils.hasText(content)) this.commentBody = content;
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
