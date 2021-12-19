package cl.ucn.disc.dsm.slobos.newsapi.model;


import net.openhft.hashing.LongHashFunction;

import org.threeten.bp.ZonedDateTime;

import lombok.Getter;

import javax.persistence.*;

/**
 *  The News Class.
 *  @author Sebastian Lobos
 */
@Entity
public final class News {

    /**
     * Primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private Long key;

    /**
     *  ID unique.
     */
    // @Column(unique = true)
    @Getter
    private Long id;

    /**
     *  The title.
     */
    @Getter
    private String title;

    /**
     *  The source.
     */
    @Getter
    private String source;

    /**
     *  The author.
     */
    @Getter
    private String author;

    /**
     *  The URL.
     */
    @Getter
    private String url;

    /**
     *  The URL image.
     */
    @Getter
    private String urlImage;

    /**
     *  The Description.
     */
    @Getter
    private String description;

    /**
     *  The Content.
     */
    @Getter
    private String content;

    /**
     *  The date of publish.
     */
    @Getter
    private ZonedDateTime publishedAt;


    /**
     * JPA required
     */
    public News() {
        //Nothing here
    }

    /**
     *  The Constructor of News.
     * @param title can't be null
     * @param source can't be null
     * @param author can't be null
     * @param url can be null
     * @param urlImage can be null
     * @param description can't be null
     * @param content can't be null
     * @param publishedAt can't be null
     */
    public News(final String title,
                final String source,
                final String author,
                final String url,
                final String urlImage,
                final String description,
                final String content,
                final ZonedDateTime publishedAt) {

        // Title
        if (title == null || title.length() <2){
            throw new IllegalArgumentException("Title required");
        }
        this.title = title;

        // Source
        if (source == null || source.length() < 2) {
            throw new IllegalArgumentException("Source required");
        }
        this.source = source;

        // Author
        if (author == null || author.length() < 3){
            throw new IllegalArgumentException("Author required");
        }
        this.author = author;

        // ID: Hashing(title + | + source + | + author)
        this.id = LongHashFunction.xx().hashChars(title + "|" + source + "|" + author);

        this.url = url;
        this.urlImage = urlImage;


        if(description == null || description.length() < 3){
            throw new IllegalArgumentException("Description required");
        }
        this.description = description;

        if(content == null || content.length() < 2){
            throw new IllegalArgumentException("Content required");
        }
        this.content = content;

        if(publishedAt == null){
            throw new IllegalArgumentException("Published At required");
        }
        this.publishedAt = publishedAt;
    }
}