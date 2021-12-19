package cl.ucn.disc.dsm.slobos.newsapi;

import cl.ucn.disc.dsm.slobos.newsapi.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.file.LinkOption;

/**
 * The Repository of News.
 * @author Sebastian Lobos.
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long> {

}