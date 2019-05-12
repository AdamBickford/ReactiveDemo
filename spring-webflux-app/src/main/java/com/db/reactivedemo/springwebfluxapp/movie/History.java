package com.db.reactivedemo.springwebfluxapp.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {
    private String userName;
    private List<Movie> movies;
}
