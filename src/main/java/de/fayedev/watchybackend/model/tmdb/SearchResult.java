package de.fayedev.watchybackend.model.tmdb;

import com.uwetrottmann.tmdb2.entities.MovieResultsPage;
import com.uwetrottmann.tmdb2.entities.TvShowResultsPage;
import lombok.Data;

@Data
public class SearchResult {

    private TvShowResultsPage tvShowResultsPage;
    private MovieResultsPage movieResultsPage;
}
