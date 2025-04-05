package scitech.scitechnewsparser.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scitech.scitechnewsparser.models.NewsArticle;

import java.util.List;

@Service
public class NewsScheduler {

    @Autowired
    private NewsParserService parserService;

    @Autowired
    private NewsService newsService;

    @Scheduled(cron = "0 0 * * * ?") //каждый часик
    public void scheduledParse() {
        List<NewsArticle> articles = parserService.parseNewsList("https://наука.рф/news");
        newsService.saveArticles(articles);
    }
}