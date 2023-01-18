package com.example.projectboard.domain.articles;

import com.example.projectboard.domain.JpaAuditingFields;
import com.example.projectboard.domain.articles.articlehashtags.ArticleHashtag;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(exclude = "hashtags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "articles", indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends JpaAuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @Column(nullable = false, length = 150)
    private String title; // 제목

    @Column(nullable = false, length = 10000)
    private String content; // 본문

    @Column(columnDefinition = "integer default 0")
    private int viewCount;

    @Column(nullable = false, updatable = false)
    private Long userId; // 연관관계 매핑: UserAccount id

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<ArticleHashtag> hashtags = new LinkedHashSet<>(); // 게시물에 달린 해시태그 Set


    private Article(String title,
                    String content,
                    Long userId) {

        this.title = title;
        this.content = content;
        this.userId = userId;
    }


    // 생성 메서드 -> hashtags 제외
    public static Article of(String title,
                             String content,
                             Long userId) {
        return new Article(title, content, userId);
    }

    // 연관관계 메서드
    public void addHashtagArticle(ArticleHashtag articleHashtag) {
        this.hashtags.add(articleHashtag);
        articleHashtag.setArticle(this);
    }

    public void addHashtags(Set<ArticleHashtag> hashtags) {
        this.hashtags.addAll(hashtags);
    }

    // 비즈니스 로직 메서드
    public void update(String title,
                       String content) {

        if (StringUtils.hasText(title)) this.title = title;
        if (StringUtils.hasText(content)) this.content = content;
    }

    public void clearHashtags() {
        this.hashtags.clear();
    }

    // equals & hashcode override
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return this.getId() != null && this.getId().equals(article.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
