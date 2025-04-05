package scitech.scitechnewsparser.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import scitech.scitechnewsparser.models.NewsContentBlock;

public interface NewsContentBlockRepository extends JpaRepository<NewsContentBlock, Long> {

}
