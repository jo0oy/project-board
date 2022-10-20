package com.example.projectboard.domain.hashtags;

import com.example.projectboard.domain.JpaAuditingDateTimeFields;
import com.example.projectboard.domain.articles.articlehashtags.ArticleHashtag;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@ToString(exclude = "articles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hashtags")
@Entity
public class Hashtag extends JpaAuditingDateTimeFields {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    @Column(nullable = false)
    private String hashtagName;

    @OneToMany(mappedBy = "hashtag", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<ArticleHashtag> articles = new LinkedHashSet<>();

    private Hashtag(String hashtagName) {
        this.hashtagName = hashtagName;
    }

    public static Hashtag of(String hashtagName) {
        return new Hashtag(hashtagName);
    }

    public void addHashtagArticle(ArticleHashtag articleHashtag) {
        this.articles.add(articleHashtag);
        articleHashtag.setHashtag(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Hashtag hashtag = (Hashtag) o;
        return this.getId() != null && this.getId().equals(hashtag.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
