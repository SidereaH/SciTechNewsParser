package scitech.scitechnewsparser.models;

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
public class NewsDto {
        private Long id;
        private Long ownerId;
        private String theme;
        private String status;
        private String title;
        private String description;
        private String content;
        private String url;
        private int likes;
        private int shows;
        private List<String> tags;
        private LocalDate dateOfCreation;
}
