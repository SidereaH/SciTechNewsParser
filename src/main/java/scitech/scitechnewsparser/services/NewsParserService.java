package scitech.scitechnewsparser.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import scitech.scitechnewsparser.models.NewsArticle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class NewsParserService {


    public List<NewsArticle> parseNewsList(String url, int numOfStates)  {

        Document doc2 = null;
        try {
            doc2 = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //<section class="u-news-section">
        //     <div class="u-g-container">
        List<NewsArticle> articles = new ArrayList<>();

        Elements links = doc2.select("section.u-news-section a.news-item");

        for (Element link : links) {
            try {
                // Получаем URL ссылки
                String href = link.attr("abs:href"); // abs:href даст абсолютный URL

                Element info = link.child(0).child(0);
                Element infodiv = info.select("div.news-item__info").first();
                String date = infodiv.child(0).text();
                NewsArticle article = new NewsArticle();
                article.setTitle(link.select(".u-news-card__title").text());
                article.setUrl(link.attr("href"));
                article.setPublishDate(parseDate(date));

                article.setHtmlContent( parseFullArticle(href));
//                article.setContentBlocks(fullArticle);

                articles.add(article);

            } catch (IOException e) {
                System.err.println("Ошибка при переходе по ссылке: " + link.attr("href"));
                e.printStackTrace();
            }
        }

        return articles;
    }

    private String parseFullArticle(String articleUrl) throws IOException {

        Document doc = Jsoup.connect(articleUrl).get();
        String title = doc.title();
        System.out.println("Заголовок страницы: " + title);


        Element content = doc.select("section.content-section").first();
        Element textContent = content.select("div.u-news-detail-page__text-content").first();
        String text = textContent.outerHtml();
        System.out.printf("svo");
//        String result = text.replace("\"\\", "");
//        result = result.replace("\"\"", "");
//        // Удаляем переносы строк (\n)
//        result = result.replace("\n", "");
        String cleanedText = text.replace("\\", "");
         cleanedText = cleanedText.replace("<br>", "<br />");




//        return HtmlWyzConverter.convertImgWyzTags(text);
        return cleanedText;
//        return article;
    }

    private LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("d MMMM yyyy", new Locale("ru"));

        LocalDate date = LocalDate.parse(dateStr, formatter);

        return date;
    }

}