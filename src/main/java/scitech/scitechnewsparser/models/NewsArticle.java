package scitech.scitechnewsparser.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class NewsArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String url;
    private LocalDate publishDate;
    private String imageUrl;
    private String htmlContent;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private List<NewsContentBlock> contentBlocks;

}
