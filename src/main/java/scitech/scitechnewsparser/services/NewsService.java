package scitech.scitechnewsparser.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scitech.scitechnewsparser.models.NewsArticle;
import scitech.scitechnewsparser.models.NewsContentBlock;
import scitech.scitechnewsparser.repositories.NewsArticleRepository;
import scitech.scitechnewsparser.repositories.NewsContentBlockRepository;

import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsArticleRepository articleRepository;

    @Autowired
    private NewsContentBlockRepository blockRepository;
    @Autowired
    private HtmlGeneratorService htmlGenerator;

    @Transactional
    public void saveArticles(List<NewsArticle> articles) {
        for (NewsArticle article : articles) {
            NewsArticle existing = articleRepository.findByUrl(article.getUrl());
            if (existing != null) {
                continue;
            }

            String html = htmlGenerator.generateHtml(article);
            article.setHtmlContent(html);
            articleRepository.save(article);

            if (article.getContentBlocks() != null) {
                for (NewsContentBlock block : article.getContentBlocks()) {
                    block.setArticle(article);
                    blockRepository.save(block);
                }
            }
        }
    }

    public List<NewsArticle> getAllArticles() {
        return articleRepository.findAll();
    }
}