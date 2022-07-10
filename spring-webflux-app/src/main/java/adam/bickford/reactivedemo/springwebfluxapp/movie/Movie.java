package adam.bickford.reactivedemo.springwebfluxapp.movie;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private String title;
    private List<String> actors;
}
