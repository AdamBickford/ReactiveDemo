package adam.bickford.reactivedemo.springwebfluxapp.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    String userName;
    String firstName;
    String lastName;
}
