package scitech.scitechnewsparser.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import scitech.scitechnewsparser.models.NewsArticle;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Long> {
    NewsArticle findByUrl(String title);

}
