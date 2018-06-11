package mcb.demo.coinflip;

import mcb.demo.coinflip.model.Flip;
import mcb.demo.coinflip.model.Outcome;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;

@Component
public class CoinFlipRepository {
    private final DataSource dataSource;

    @Autowired
    public CoinFlipRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<Flip> saveAll(List<Flip> flips) {
        for (var flip : flips) {
            var insert = new SimpleJdbcInsert(this.dataSource);
            var parameters = new HashMap<String, Object>(2);
            parameters.put("request_id", flip.getRequestId());
            parameters.put("id", flip.getId());
            parameters.put("outcome", flip.getOutcome().name());
            parameters.put("currency", flip.getCurrency());
            parameters.put("denomination", flip.getDenomination());
            insert.withTableName("flips").execute(parameters);
        }
        return flips;
    }

    public List<Flip> getAll() {
        return new JdbcTemplate(this.dataSource)
                .query("SELECT request_id, id, outcome, currency, denomination FROM flips",
                        (rs, rowNum) -> new Flip(rs.getString("request_id"), rs.getString("id"), Outcome.valueOf(rs.getString("outcome")), rs.getString("currency"), rs.getInt("denomination")));
    }
}
