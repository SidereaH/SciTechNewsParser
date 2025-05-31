package scitech.scitechnewsparser.services;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import scitech.scitechnewsparser.models.NewsArticle;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class NewsScheduler {

    @Autowired
    private NewsParserService parserService;

    @Autowired
    private NewsService newsService;
    @Value("${sel.url:http://infra_selenium:4444/wd/hub}")
    private String url;



    @Scheduled(cron = "0 0 * * * ?") //каждый часик
    public void scheduledParse() {
        List<NewsArticle> articles = null;
        try{
            articles = parserService.parseNewsList("https://наука.рф/news",10, url);

        }
        catch (IOException e){
            log.error("Error while shedule parsing"+ e.getMessage());
        }
        newsService.saveArticles(articles);
    }
}