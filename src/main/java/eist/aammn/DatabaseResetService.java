package eist.aammn;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DatabaseResetService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "0 0 2 * * MON") // Every Monday at 2 AM
    public void resetDatabase() {
        jdbcTemplate.execute("TRUNCATE your_table_name RESTART IDENTITY CASCADE;");
    }
}

