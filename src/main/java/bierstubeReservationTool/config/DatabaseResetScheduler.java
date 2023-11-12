package bierstubeReservationTool.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Component
public class DatabaseResetScheduler {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    // This will run every week at 12:00 AM on Sunday.

    @Scheduled(cron = "0 0 0 ? * SUN")
    public void resetDatabase() {
        try {
            // Load the SQL script
            Resource resource = resourceLoader.getResource("classpath:reset_db.sql");
            String sqlScript = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);

            // Execute the SQL script
            jdbcTemplate.execute(sqlScript);
        } catch (IOException e) {
            // Handle the exception, e.g., logging it
            e.printStackTrace();
        }
    }
}


