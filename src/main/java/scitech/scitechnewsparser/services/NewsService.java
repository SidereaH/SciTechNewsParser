package scitech.scitechnewsparser.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scitech.scitechnewsparser.models.NewsArticle;
import scitech.scitechnewsparser.repositories.NewsArticleRepository;

import java.util.List;

@Service
public class NewsService {

    @Autowired
    private NewsArticleRepository articleRepository;

    @Autowired
    private HtmlWyzConverter htmlConv;

    @Transactional
    public void saveArticles(List<NewsArticle> articles) {
        for (NewsArticle article : articles) {
            NewsArticle existing = articleRepository.findByUrl(article.getUrl());
            if (existing != null) {
                continue;
            }

//            String html = htmlGenerator.generateHtml(article);
//            article.setHtmlContent(html);
            articleRepository.save(article);


        }
    }


    public List<NewsArticle> getAllArticles() {
        return articleRepository.findAll();
    }
}