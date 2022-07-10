package adam.bickford.reactivedemo.springwebfluxapp.user;

import adam.bickford.reactivedemo.springwebfluxapp.movie.Movie;
import adam.bickford.reactivedemo.springwebfluxapp.movie.Favorites;
import adam.bickford.reactivedemo.springwebfluxapp.movie.History;
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
