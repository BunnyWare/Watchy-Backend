package de.fayedev.watchybackend.web;

import com.uwetrottmann.tmdb2.entities.*;
import de.fayedev.watchybackend.exception.ApplicationException;
import de.fayedev.watchybackend.model.tmdb.SearchResult;
import de.fayedev.watchybackend.model.tmdb.dto.SearchRequest;
import de.fayedev.watchybackend.model.user.User;
import de.fayedev.watchybackend.service.TmdbService;
import de.fayedev.watchybackend.service.UserService;
import de.fayedev.watchybackend.utils.SecurityAccessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/tmdb")
@Tag(name = "Tmdb API")
public class TmdbRestController {

    private final TmdbService tmdbService;
    private final UserService userService;

    public TmdbRestController(TmdbService tmdbService, UserService userService) {
        this.tmdbService = tmdbService;
        this.userService = userService;
    }

    @PostMapping("/search")
    @Operation(summary = "Search for movie or tv show.",
            security = {
                    @SecurityRequirement(name = "Bearer")})
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SearchResult.class))})
    public SearchResult search(@RequestBody @Valid SearchRequest searchRequest) throws ApplicationException {
        User user = userService.getUserByUserName(SecurityAccessor.getAuthenticatedUserName());
        return tmdbService.searchMovieOrTv(searchRequest.getSearchInput(), user.isShowAdult());
    }

    @GetMapping("/series/{id}")
    public TvShow getTvShow(@PathVariable("id") @NotNull Integer id) throws ApplicationException {
        return tmdbService.getTvInfo(id);
    }

    @GetMapping("/series/{seriesId}/seasons/{seasonId}")
    public TvSeason getTvShowSeason(@PathVariable("seriesId") @NotNull Integer seriesId, @PathVariable("seasonId") @NotNull Integer seasonId) throws ApplicationException {
        return tmdbService.getTvSeasonInfo(seriesId, seasonId);
    }

    @GetMapping("/movies/{id}")
    public Movie getMovie(@PathVariable("id") @NotNull Integer id) throws ApplicationException {
        return tmdbService.getMovieInfo(id);
    }

    @GetMapping("/genres")
    public GenreResults getGenres() throws ApplicationException {
        return tmdbService.getGenres();
    }
}
