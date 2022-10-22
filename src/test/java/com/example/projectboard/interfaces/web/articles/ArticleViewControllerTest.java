package com.example.projectboard.interfaces.web.articles;

import com.example.projectboard.application.PaginationService;
import com.example.projectboard.application.articles.ArticleQueryService;
import com.example.projectboard.config.TestSecurityConfig;
import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;
import com.example.projectboard.domain.articles.SearchType;
import com.example.projectboard.interfaces.dto.articles.ArticleDtoMapper;
import com.example.projectboard.interfaces.dto.articles.ArticleDtoMapperImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(TestSecurityConfig.class)
@ComponentScan(basePackageClasses = {ArticleDtoMapper.class, ArticleDtoMapperImpl.class})
@AutoConfigureMockMvc
@WebMvcTest(controllers = ArticleViewController.class)
public class ArticleViewControllerTest {

    @Autowired
    private MockMvc mvc;

    @SpyBean
    private ArticleDtoMapper articleDtoMapper;

    @MockBean
    private ArticleQueryService articleQueryService;

    @MockBean
    private PaginationService paginationService;

    @DisplayName("[성공][view][GET] 게시글 리스트 (게시판) 페이지")
    @Test
    void givenNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        // Given
        given(articleQueryService.articles(any(ArticleCommand.SearchCondition.class), any(Pageable.class)))
                .willReturn(Page.empty());

        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When & Then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("paginationBar"))
                .andExpect(model().attributeExists("searchTypes"));

        then(articleQueryService).should().articles(any(ArticleCommand.SearchCondition.class), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[성공][view][GET] 게시글 리스트 (게시판) 페이지 - 검색어와 함께 호출")
    @Test
    void givenSearchKeyword_whenSearchingArticlesView_thenReturnsArticlesView() throws Exception {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchValue = "title";

        given(articleQueryService.articles(any(ArticleCommand.SearchCondition.class), any(Pageable.class)))
                .willReturn(Page.empty());

        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));

        // When & Then
        mvc.perform(
                        get("/articles")
                                .queryParam("searchType", searchType.name())
                                .queryParam("searchValue", searchValue)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("searchTypes"));

        then(articleQueryService).should().articles(any(ArticleCommand.SearchCondition.class), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[성공][view][GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
    @Test
    void givenPagingAndSortingParams_whenSearchingArticlesView_thenReturnsArticlesView() throws Exception {
        // Given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);

        given(articleQueryService.articles(any(ArticleCommand.SearchCondition.class), eq(pageable)))
                .willReturn(Page.empty());

        given(paginationService.getPaginationBarNumbers(pageNumber, Page.empty().getTotalPages())).willReturn(barNumbers);

        // When & Then
        mvc.perform(
                        get("/articles")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationBar", barNumbers));

        then(articleQueryService).should().articles(any(ArticleCommand.SearchCondition.class), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[성공][view][GET] 게시글 해시태그 검색 페이지 - by hashtagId")
    @Test
    void givenHashtagId_whenHashtagSearchResultArticlesView_thenReturnsHashtagArticlesView() throws Exception {
        // Given
        var hashtagId = 1L;
        var pageNumber = 0;
        var pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc("article.createdAt")));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);

        given(articleQueryService.articlesByHashtagId(anyLong(), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageNumber, Page.empty().getTotalPages())).willReturn(barNumbers);

        // When & Then
        mvc.perform(get("/articles/hashtag-search/" + hashtagId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/hashtag-search"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationBar", barNumbers));

        then(articleQueryService).should().articlesByHashtagId(anyLong(), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @WithMockUser
    @DisplayName("[성공][view][GET] 새 게시글 작성 페이지 - 인증된 사용자")
    @Test
    void givenNothing_whenRequestingNewPostPage_thenReturnsNewArticlePage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/articles/new"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("registerForm"))
                .andExpect(view().name("articles/add-form"));
    }

    @DisplayName("[성공][view][GET] 새 게시글 작성 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    @Test
    void givenNothingNotAuthenticatedUser_whenRequestingNewPostPage_thenRedirectsToLoginPage() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/articles/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @WithMockUser
    @DisplayName("[성공][view][GET] 게시글 수정 페이지 - 인증된 사용자")
    @Test
    void givenArticleIdWithAuthenticatedUser_whenRequestingEditPage_thenReturnsUpdatedArticlePage() throws Exception {
        // Given
        var articleId = 1L;
        var articleInfo = getDefaultArticleInfo(articleId);
        given(articleQueryService.getArticle(anyLong())).willReturn(articleInfo);

        // When & Then
        mvc.perform(get("/articles/" + articleId + "/edit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/edit-form"))
                .andExpect(model().attributeExists("updateForm"));

        then(articleQueryService).should().getArticle(anyLong());
    }

    @DisplayName("[성공][view][GET] 게시글 수정 페이지 - 인증 없을 땐 로그인 페이지로 이동")
    @Test
    void givenArticleIdWithNotAuthenticatedUser_whenRequestingEditPage_thenRedirectsToLoginPage() throws Exception {
        // Given
        var articleId = 1L;

        // When & Then
        mvc.perform(get("/articles/" + articleId + "/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    private static ArticleInfo.MainInfo getDefaultArticleInfo(long articleId) {
        return ArticleInfo.MainInfo.builder()
                .articleId(articleId)
                .title("글 제목입니다!!!!")
                .content("글 내용입니다!!!!")
                .createdBy("userTest")
                .hashtagInfos(Set.of(
                        ArticleInfo.HashtagInfo.builder()
                                .actualHashtagName("#hashtag")
                                .hashtagId(1L)
                                .hashtagName("#hashtag")
                                .build(),
                        ArticleInfo.HashtagInfo.builder()
                                .actualHashtagName("#Pink")
                                .hashtagId(3L)
                                .hashtagName("#pink")
                                .build())
                ).build();
    }
}
