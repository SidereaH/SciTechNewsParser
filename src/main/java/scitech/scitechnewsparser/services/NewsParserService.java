package scitech.scitechnewsparser.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import scitech.scitechnewsparser.models.BlockType;
import scitech.scitechnewsparser.models.NewsArticle;
import scitech.scitechnewsparser.models.NewsContentBlock;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsParserService {

    @Autowired
    private RestTemplate restTemplate;

    public List<NewsArticle> parseNewsList(String url) {
        String html = restTemplate.getForObject(url, String.class);
        Document doc = Jsoup.parse(html);

        List<NewsArticle> articles = new ArrayList<>();

        Elements newsItems = doc.select(".u-news-list .u-news-card");
        for (Element item : newsItems) {
            NewsArticle article = new NewsArticle();

            article.setTitle(item.select(".u-news-card__title").text());
            article.setUrl(item.attr("href"));
            article.setPublishDate(parseDate(item.select(".u-news-card__date").text()));

            Element img = item.select(".u-news-card__image").first();
            if (img != null) {
                article.setImageUrl(img.attr("src"));
            }

            NewsArticle fullArticle = parseFullArticle(article.getUrl());
            article.setContentBlocks(fullArticle.getContentBlocks());

            articles.add(article);
        }

        return articles;
    }

    private NewsArticle parseFullArticle(String articleUrl) {
        String html = restTemplate.getForObject(articleUrl, String.class);
        Document doc = Jsoup.parse(html);

        NewsArticle article = new NewsArticle();
        List<NewsContentBlock> blocks = new ArrayList<>();

        Element content = doc.select(".u-news-detail-page__text-content").first();
        if (content != null) {
            for (Element child : content.children()) {
                NewsContentBlock block = parseContentBlock(child);
                if (block != null) {
                    blocks.add(block);
                }
            }
        }

        article.setContentBlocks(blocks);
        return article;
    }

    private NewsContentBlock parseContentBlock(Element element) {
        NewsContentBlock block = new NewsContentBlock();

        if (element.tagName().equals("p")) {
            block.setType(BlockType.TEXT);
            block.setContent(element.html());
        }
        else if (element.tagName().equals("b") || element.tagName().equals("h3")) {
            block.setType(BlockType.HEADER);
            block.setContent(element.html());
        }
        else if (element.hasClass("img-wyz")) {
            block.setType(BlockType.IMAGE);
            Element img = element.select("img").first();
            if (img != null) {
                block.setImageUrl(img.attr("src"));
                block.setImageAlignment(element.hasClass("img-wyz--left") ? "left" :
                        element.hasClass("img-wyz--right") ? "right" : "none");
            }
        }
        return block;
    }

    private LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr);
    }
}