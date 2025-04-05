package scitech.scitechnewsparser.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewsContentBlock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BlockType type;

    @Column(columnDefinition = "TEXT")
    private String content;
    private String imageUrl;
    private String imageAlignment;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private NewsArticle article;

}


