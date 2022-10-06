package com.example.projectboard.domain.articles;

import com.example.projectboard.domain.JpaAuditingFields;
import com.example.projectboard.domain.users.UserAccount;
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

    @Column(nullable = false)
    private String title; // 제목

    @Column(nullable = false, length = 10000)
    private String content; // 본문

    private String hashtag; // 해시태그

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Builder(builderClassName = "ArticleWithoutHashtag", builderMethodName = "ArticleWithoutHashtag")
    public Article(String title,
                   String content,
                   UserAccount userAccount) {

        this.title = title;
        this.content = content;
        this.userAccount = userAccount;
    }

    @Builder(builderClassName = "ArticleWithHashtag", builderMethodName = "ArticleWithHashtag")
    public Article(String title,
                   String content,
                   String hashtag,
                   UserAccount userAccount) {

        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
        this.userAccount = userAccount;
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
