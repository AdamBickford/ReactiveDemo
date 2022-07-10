package adam.bickford.reactivedemo.springwebfluxapp.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepo userRepo;

    @Autowired
    public UserController(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/{userName}")
    public Mono<User> byUserName(ServerHttpRequest request, @PathVariable("userName") String userName) {
        return userRepo.byUserName(userName)
            .delayElement(Duration.ofSeconds(2))
            ;
    }
}
