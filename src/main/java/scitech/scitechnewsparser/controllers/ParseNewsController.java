package scitech.scitechnewsparser.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import scitech.scitechnewsparser.models.NewsArticle;
import scitech.scitechnewsparser.services.NewsParserService;
import scitech.scitechnewsparser.services.NewsService;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class ParseNewsController {

    @Autowired
    private NewsParserService parserService;

    @Autowired
    private NewsService newsService;

    @GetMapping("/parse")
    public ResponseEntity<String> parseAndSaveNews() {
        String baseUrl = "https://наука.рф";
        List<NewsArticle> articles = parserService.parseNewsList(baseUrl + "/news");
        newsService.saveArticles(articles);
        return ResponseEntity.ok("Parsed and saved " + articles.size() + " articles");
    }

    @GetMapping
    public ResponseEntity<List<NewsArticle>> getAllNews() {
        return ResponseEntity.ok(newsService.getAllArticles());
    }
}
