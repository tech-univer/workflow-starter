package mcb.demo.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Component
public class UserRepository {

    private DataSource dataSource;

    @Autowired
    public UserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public User create(String username) {
        var insert = new SimpleJdbcInsert(this.dataSource)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        HashMap<String, Object> parameters = new HashMap<>(1);
        parameters.put("username", username);
        var id = insert.executeAndReturnKey(parameters).longValue();
        return new User(id, username);
    }

    public Optional<User> getByUsername(String username) {
        List<User> query = new JdbcTemplate(this.dataSource)
                .query("SELECT id, username FROM USERS WHERE username = ?",
                        new Object[]{username},
                        (rs, rowNum) -> new User(rs.getLong("id"), rs.getString("username")));
        return Optional.ofNullable(query.isEmpty() ? null : query.get(0));
    }
}
