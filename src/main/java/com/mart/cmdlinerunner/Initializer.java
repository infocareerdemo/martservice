package com.mart.cmdlinerunner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {

	@Override
	public void run(String... args) throws Exception {
		
	}

	   /*@Autowired
	    private JdbcTemplate jdbcTemplate;

	    public static Double GST_VALUE;

	    public Double fetchGstValue() {
	        String sql = "SELECT gst FROM gst LIMIT 1";
	        return jdbcTemplate.queryForObject(sql, Double.class);
	    }

	    @Override
	    public void run(String... args) throws Exception {
	        GST_VALUE = fetchGstValue();

	    }*/
}