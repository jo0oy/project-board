package com.example.projectboard.domain.articles;

import com.example.projectboard.domain.JpaAuditingFields;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "articles", indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends JpaAuditingFields {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @Column(nullable = false, length = 150)
    private String title; // 제목

    @Column(nullable = false, length = 10000)
    private String content; // 본문

    private String hashtag; // 해시태그

    @Column(nullable = false, updatable = false)
    private Long userId; // 연관관계 매핑: UserAccount id

    @Builder(builderClassName = "ArticleWithoutHashtag", builderMethodName = "ArticleWithoutHashtag")
    private Article(String title,
                   String content,
                   Long userId) {

        this.title = title;
        this.content = content;
        this.userId = userId;
    }

    @Builder(builderClassName = "ArticleWithHashtag", builderMethodName = "ArticleWithHashtag")
    private Article(String title,
                   String content,
                   String hashtag,
                   Long userId) {

        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
        this.userId = userId;
    }

    // 수정 메서드
    public void update(String title,
                       String content,
                       String hashtag) {

        if(StringUtils.hasText(title)) this.title = title;
        if(StringUtils.hasText(content)) this.content = content;
        this.hashtag = hashtag;
    }

    // equals & hashcode override
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return id != null && id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
