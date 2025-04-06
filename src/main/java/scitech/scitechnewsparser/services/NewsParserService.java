package scitech.scitechnewsparser.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
        //количеством нам оф стейтс нажимать на показать еще чтобы появилось как можно больше ссылок на карточки
        WebDriver driver = new ChromeDriver();
        driver.get(url);
        //принимаем кукисы
        WebElement okButton = driver.findElement(
                By.xpath("//button[.//span[text()[normalize-space()='ОК']]]")
        );
        okButton.click();


        // генерим статьи жоско
        for (int i = 0; i < numOfStates; i++) {
            WebElement button = driver.findElement(By.cssSelector(".u-news-page__btn-more"));
            try {
                button.click();
            }
            catch (Exception e) {
                break;
            }
            try {
                Thread.sleep(2000); // подождать, пока подгрузится
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        String pageSource = driver.getPageSource();
        Document doc2 = Jsoup.parse(pageSource);
// далее парсишь, как обычно

//        Document doc2 = null;
//        try {
//            doc2 = Jsoup.connect(url).get();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        //<section class="u-news-section">
        //     <div class="u-g-container">
        List<NewsArticle> articles = new ArrayList<>();

// Получаем все карточки новостей
        Elements newsCards = doc2.select("section.u-news-list a.u-news-card");

        for (Element card : newsCards) {
            try {
                String href = card.attr("href");  // относительная ссылка

                // Извлекаем данные из карточки
                Element cardInner = card.selectFirst(".u-news-card__inner");
                String title = cardInner.selectFirst(".u-news-card__title").text();
                String dateStr = cardInner.selectFirst(".u-news-card__date").text();
                String imageUrl = cardInner.selectFirst(".u-news-card__image").attr("src");

                // Создаем объект новости
                NewsArticle article = new NewsArticle();
                article.setTitle(title);
                article.setUrl(href);
                article.setPublishDate(parseDate(dateStr));
                article.setImageUrl(imageUrl);  // если в NewsArticle есть такое поле

                // Парсим полный текст статьи
                article.setHtmlContent(parseFullArticle("https://наука.рф"+href));

                articles.add(article);

            } catch (IOException e) {
                System.err.println("Ошибка при обработке карточки: " + card.attr("href"));
                e.printStackTrace();
            }
        }

        return articles;
    }
    private String getTitle(String articleUrl) throws IOException {
        Document doc = Jsoup.connect(articleUrl).get();
        return  doc.title();
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
        text = text.replace("\n", "");
        text = text.replace("img-wyz", "img");
        text = text.replace("<br>", "<br/>");
        text = text.replace("align=\"right\"", "");
        text = text.replaceAll(":\\w+=\"\\d+\"", "");


        System.out.println(text);
//        String cleanedText = text.replace("\\", "");
//         cleanedText = cleanedText.replace("<br>", "<br />");




//        return HtmlWyzConverter.convertImgWyzTags(text);

        return text;
//        return article;
    }

    private LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("d MMMM yyyy", new Locale("ru"));

        LocalDate date = LocalDate.parse(dateStr, formatter);

        return date;
    }


}