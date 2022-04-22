package de.fayedev.watchybackend.service;

import com.uwetrottmann.tmdb2.Tmdb;
import com.uwetrottmann.tmdb2.entities.*;
import com.uwetrottmann.tmdb2.enumerations.AppendToResponseItem;
import de.fayedev.watchybackend.exception.ApplicationException;
import de.fayedev.watchybackend.exception.ApplicationExceptionCode;
import de.fayedev.watchybackend.model.tmdb.SearchResult;
import de.fayedev.watchybackend.utils.LogMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TmdbService {

    public static final String LANGUAGE = "en-US";
    private final Tmdb tmdb;

    public TmdbService(@Value("${tmdb.key}") String tmdbKey) throws ApplicationException {
        this.tmdb = new Tmdb(tmdbKey);
    }

    public SearchResult searchMovieOrTv(String searchInput, boolean includeAdult) throws ApplicationException {
        SearchResult searchResult = new SearchResult();
        searchResult.setMovieResultsPage(searchMovie(searchInput, includeAdult));
        searchResult.setTvShowResultsPage(searchTv(searchInput, includeAdult));

        return searchResult;
    }

    public MovieResultsPage searchMovie(String searchInput, boolean includeAdult) throws ApplicationException {
        try {
            return tmdb.searchService().movie(searchInput, 1, LANGUAGE, null, includeAdult, null, null).execute().body();
        } catch (IOException e) {
            throw new ApplicationException(HttpStatus.FAILED_DEPENDENCY, ApplicationExceptionCode.TMDB_FAILED, LogMessage.TMDB_FAILED);
        }
    }

    public TvShowResultsPage searchTv(String searchInput, boolean includeAdult) throws ApplicationException {
        try {
            return tmdb.searchService().tv(searchInput, 1, LANGUAGE, null, includeAdult).execute().body();
        } catch (IOException e) {
            throw new ApplicationException(HttpStatus.FAILED_DEPENDENCY, ApplicationExceptionCode.TMDB_FAILED, LogMessage.TMDB_FAILED);
        }
    }

    public Movie getMovieInfo(int movieId) {
        try {
            AppendToResponse appendToResponse = new AppendToResponse(AppendToResponseItem.SIMILAR, AppendToResponseItem.RECOMMENDATIONS,
                    AppendToResponseItem.KEYWORDS, AppendToResponseItem.CREDITS, AppendToResponseItem.VIDEOS);
            return tmdb.moviesService().summary(movieId, LANGUAGE, appendToResponse).execute().body();
        } catch (IOException e) {
            throw new ApplicationException(HttpStatus.FAILED_DEPENDENCY, ApplicationExceptionCode.TMDB_FAILED, LogMessage.TMDB_FAILED);
        }
    }

    public TvShow getTvInfo(int tvId) {
        try {
            return tmdb.tvService().tv(tvId, LANGUAGE).execute().body();
        } catch (IOException e) {
            throw new ApplicationException(HttpStatus.FAILED_DEPENDENCY, ApplicationExceptionCode.TMDB_FAILED, LogMessage.TMDB_FAILED);
        }
    }

    public TvSeason getTvSeasonInfo(int tvId, int seasonId) {
        try {
            return tmdb.tvSeasonsService().season(tvId, seasonId, LANGUAGE).execute().body();
        } catch (IOException e) {
            throw new ApplicationException(HttpStatus.FAILED_DEPENDENCY, ApplicationExceptionCode.TMDB_FAILED, LogMessage.TMDB_FAILED);
        }
    }

    public GenreResults getGenres() {
        try {
            return tmdb.genreService().movie(LANGUAGE).execute().body();
        } catch (IOException e) {
            throw new ApplicationException(HttpStatus.FAILED_DEPENDENCY, ApplicationExceptionCode.TMDB_FAILED, LogMessage.TMDB_FAILED);
        }
    }
}
