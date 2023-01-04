package com.nosqlcourse;

import com.nosqlcourse.exception.DataNotFoundException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NoSqlCourseApplication {
	public static void main(String[] args) throws DataNotFoundException {
		SpringApplication.run(NoSqlCourseApplication.class, args);
	}
}