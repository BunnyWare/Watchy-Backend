package de.fayedev.watchybackend.model.tmdb.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;

@Getter
public class SearchRequest {

    @NotBlank
    private String searchInput;
}
