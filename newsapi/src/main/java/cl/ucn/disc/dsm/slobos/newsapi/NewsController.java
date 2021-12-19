package cl.ucn.disc.dsm.slobos.newsapi;

import cl.ucn.disc.dsm.slobos.newsapi.model.News;
import com.kwabenaberko.newsapilib.models.Article;
import com.kwabenaberko.newsapilib.models.response.ArticleResponse;
import com.kwabenaberko.newsapilib.network.APIClient;
import com.kwabenaberko.newsapilib.network.APIService;
import jdk.jfr.Frequency;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Controller of News
 * @author Sebastian Lobos.
 */
@Slf4j
@RestController
public class NewsController{

    /**
     * The Repost of News
     */
    private final NewsRepository newsRepository;

    /**
     * The Contructor of NewsController.
     * @param newsRepository to use.
     */
    public NewsController(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    /**
     *
     * @return all the News in the Backend.
     */
    @GetMapping("/v1/news")
    public List<News> all(@RequestParam(required = false, defaultValue = "false") Boolean reload) {

        // Is reload -> get news from NewsAPI.org
        if (reload) {
            this.reloadNewsFromNewsApi();
        }

        // Equals to SELECT * FROM News;
        final List<News> theNews = this.newsRepository.findAll();
        // TODO: Show the in console
        return theNews;
    }

    /**
     * Get the News from NewsAPI and save into the database.
     */
    private void reloadNewsFromNewsApi() {

        // WARNING: Just for test
        final String API_KEY = "ad0fd15a4680438a9b011f2cb6b2d461";
        final int pageSize = 100;

        // 1. Create the APIService from APIClient
        final APIService apiService = APIClient.getAPIService();

        // 2. Build the request params
        final Map<String, String> reqParams = new HashMap<>();

        // The ApiKey
        reqParams.put("apiKey", API_KEY);
        // Filter by category
        reqParams.put("category", "technology");
        // The number of resulte to return per page (request). 20 is the default, 100 is the maximum.
        reqParams.put("pageSize", String.valueOf(pageSize));

        // 3. Calls the Request
        try {
            Response<ArticleResponse> artcilesResponse =
                    apiService.getTopHeadlines(reqParams).execute();

            //Exito
            if(artcilesResponse.isSuccessful()) {
                //TODO: Check for getArticles != null
                List<Article> articles = artcilesResponse.body().getArticles();

                List<News> news = new ArrayList<>();
                // List<Article> to List<News>
                for (Article article : articles) {
                    news.add(toNews(article));
                }

                //Save into the local database
                this.newsRepository.saveAll(news);
            }

        } catch (IOException e) {
            log.error("Getting the Articles from NewsAPI", e);
        }

    }

    /**
     * Convert a Article to News.
     *
     * @param article to convert.
     * @return the News.
     */
    private static News toNews(final Article article) {

        // Protection: author
        if(article.getAuthor() == null || article.getAuthor().length() < 3) {
            article.setAuthor("No Author*");
        }

        // Protection: title
        if(article.getTitle() == null || article.getTitle().length() < 3) {
            article.setTitle("No Title*");
        }

        // Protection: description
        if(article.getDescription() == null || article.getDescription().length() < 4) {
            article.setDescription("No Description*");
        }

        ZonedDateTime publicshedAt = ZonedDateTime.parse(article.getPublishedAt()).withZoneSameInstant(ZoneId.of("-3"));


        // Constructor a News from Article
        return new News(
                article.getTitle(),
                article.getSource().getName(),
                article.getAuthor(),
                article.getUrl(),
                article.getUrlToImage(),
                article.getDescription(),
                article.getDescription(),
                publicshedAt
        );
    }

    @GetMapping("/v1/news/{id}")
    public News one(@PathVariable final long id) {
        // FIXME: Change the RuntimeException to 404.
        return this.newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News Not Found :("));
    }
}