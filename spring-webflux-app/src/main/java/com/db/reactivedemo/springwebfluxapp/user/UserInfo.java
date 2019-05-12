package com.db.reactivedemo.springwebfluxapp.user;

import com.db.reactivedemo.springwebfluxapp.movie.Favorites;
import com.db.reactivedemo.springwebfluxapp.movie.History;
import com.db.reactivedemo.springwebfluxapp.movie.Movie;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class UserInfo {
    private User user;
    private Favorites favorites;
    private History history;
    private List<Movie> recommended;
}
