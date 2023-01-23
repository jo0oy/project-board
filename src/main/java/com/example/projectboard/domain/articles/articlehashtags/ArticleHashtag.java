package com.example.projectboard.domain.articles.articlehashtags;

import com.example.projectboard.common.exception.InvalidParamException;
import com.example.projectboard.domain.JpaAuditingDateTimeFields;
import com.example.projectboard.domain.articles.Article;
import com.example.projectboard.domain.hashtags.Hashtag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Objects;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "article_hashtags")
@Entity
public class ArticleHashtag extends JpaAuditingDateTimeFields {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_hashtag_id")
    private Long id;

    private String actualHashtagName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    // 생성 메서드

    private ArticleHashtag(String actualHashtagName,
                           Article article,
                           Hashtag hashtag) {
        if(!StringUtils.hasText(actualHashtagName)) throw new InvalidParamException();
        if(article == null) throw new InvalidParamException();
        if(hashtag == null) throw new InvalidParamException();

        this.actualHashtagName = actualHashtagName;
        article.addHashtagArticle(this); // setArticle() 포함
        hashtag.addHashtagArticle(this); // setHashtag() 포함
    }

    public static ArticleHashtag of(String actualHashtagName,
                                    Article article,
                                    Hashtag hashtag) {
        return new ArticleHashtag(actualHashtagName, article, hashtag);
    }

    // 연관관계 메서드
    public void setArticle(Article article) {
        this.article = article;
    }

    public void setHashtag(Hashtag hashtag) {
        this.hashtag = hashtag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        ArticleHashtag articleHashtag = (ArticleHashtag) o;
        return this.getId() != null && this.getId().equals(articleHashtag.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
