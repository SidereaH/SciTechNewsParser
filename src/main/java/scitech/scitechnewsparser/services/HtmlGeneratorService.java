package scitech.scitechnewsparser.services;

import org.springframework.stereotype.Service;
import scitech.scitechnewsparser.models.NewsArticle;
import scitech.scitechnewsparser.models.NewsContentBlock;

import java.time.LocalDate;

@Service
public class HtmlGeneratorService {

    public String generateHtml(NewsArticle article) {
        StringBuilder html = new StringBuilder();

        html.append("<article class=\"news-article\">");
        html.append("<h1 class=\"article-title\">").append(article.getTitle()).append("</h1>");
        html.append("<div class=\"article-meta\">");
        html.append("<span class=\"publish-date\">").append(formatDate(article.getPublishDate())).append("</span>");
        html.append("</div>");

        if (article.getImageUrl() != null) {
            html.append("<div class=\"article-main-image\">");
            html.append("<img src=\"").append(article.getImageUrl()).append("\" alt=\"\">");
            html.append("</div>");
        }

        html.append("<div class=\"article-content\">");

        for (NewsContentBlock block : article.getContentBlocks()) {
            html.append(generateBlockHtml(block));
        }

        html.append("</div>");
        html.append("</article>");

        return html.toString();
    }

    private String generateBlockHtml(NewsContentBlock block) {
        switch (block.getType()) {
            case TEXT:
                return "<p>" + block.getContent() + "</p>";
            case HEADER:
                return "<h3>" + block.getContent() + "</h3>";
            case IMAGE:
                return String.format("<div class=\"article-image image-%s\"><img src=\"%s\" alt=\"\"></div>",
                        block.getImageAlignment(), block.getImageUrl());
            case QUOTE:
                return "<blockquote>" + block.getContent() + "</blockquote>";
            default:
                return "";
        }
    }

    private String formatDate(LocalDate date) {

        return date.toString();
    }
}
