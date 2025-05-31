package scitech.scitechnewsparser.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import scitech.scitechnewsparser.models.NewsArticle;
import scitech.scitechnewsparser.models.NewsDto;
import scitech.scitechnewsparser.services.NewsParserService;
import scitech.scitechnewsparser.services.NewsService;

import java.io.IOException;
import java.net.ConnectException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping("/api/news")
public class ParseNewsController {

    @Autowired
    private NewsParserService parserService;

    @Autowired
    private NewsService newsService;
    @Value("${sel.url:http://infra_selenium:4444/wd/hub}")
    private String url;

    @GetMapping("/parse")
    public ResponseEntity<String> parseAndSaveNews() {

        List<NewsArticle> articles = null;
        try{
            articles = parserService.parseNewsList("https://наука.рф/news",2, url);

        } catch (IOException e){
            log.error(e.getMessage());
        }
        newsService.saveArticles(articles);
        return ResponseEntity.ok("Parsed and saved " + articles.size() + " articles");
    }
    @GetMapping("/parse/{numOfPaths}")
    public ResponseEntity<String> parseAndSaveNewsByCountOfPages(@PathVariable int numOfPaths) {

        List<NewsArticle> articles = null;
        try{
        articles = parserService.parseNewsList("https://наука.рф/news",numOfPaths, url);
        }
            catch (IOException e){
            log.error(e.getMessage());
        }
        newsService.saveArticles(articles);
        return ResponseEntity.ok("Parsed and saved " + articles.size() + " articles");
    }

    @GetMapping
    public ResponseEntity<List<NewsArticle>> getAllNews() {
        return ResponseEntity.ok(newsService.getAllArticles());
    }


    @Autowired
    private RestTemplate restTemplate; // Добавьте этот бин в ваш конфиг
    @Value("${news.service.url}")
    private String NEWS_SERVICE_URL; // Укажите правильный URL

    @PostMapping("/send-to-news-service")
    public ResponseEntity<Map<String, Object>> sendAllNewsToNewsService() {
        List<NewsArticle> articles = newsService.getAllArticles();
        List<NewsDto> newsDtos = articles.stream()
                .map(this::convertToNewsDto)
                .collect(Collectors.toList());

        try {
            ResponseEntity<List<NewsDto>> response = restTemplate.exchange(
                    NEWS_SERVICE_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(newsDtos),
                    new ParameterizedTypeReference<List<NewsDto>>() {}
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("News Service returned " + response.getStatusCode());
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Successfully sent " + newsDtos.size() + " news to News Service",
                    "data", newsDtos
            ));
        } catch (RestClientException e) {
            log.error("Failed to send news to News Service", e);
            throw new RuntimeException("News Service unavailable", e);
        }
    }
    private NewsDto convertToNewsDto(NewsArticle article) {
        NewsDto dto = new NewsDto();
        dto.setTitle(article.getTitle());
        dto.setUrl(article.getUrl());
        dto.setContent(article.getHtmlContent());
        dto.setDateOfCreation(article.getPublishDate());
        dto.setOwnerId(999L);
        dto.setTheme("Новости науки");
        dto.setStatus("Опубликовано");
        dto.setDescription(article.getHtmlContent().substring(0, Math.min(article.getHtmlContent().length(), 100)));
        dto.setLikes(0);
        dto.setShows(0);
        dto.setTags(List.of("наука", "новости"));

        return dto;
    }
}
