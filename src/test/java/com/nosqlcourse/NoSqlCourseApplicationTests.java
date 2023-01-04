package com.nosqlcourse;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import com.nosqlcourse.dao.DAOFactory;
import com.nosqlcourse.dao.TypeDAO;
import com.nosqlcourse.model.Role;
import com.nosqlcourse.model.User;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Locale;

@SpringBootTest
class NoSqlCourseApplicationTests {



	private final DAOFactory dao = DAOFactory.getDAOInstance(TypeDAO.MONGO);
	static Faker faker = new Faker(new Locale("uk-UA"));
	static FakeValuesService fakeValuesService = new FakeValuesService(new Locale("uk-UA"), new RandomService());

	@Test
	void contextLoads() {
		long start = System.currentTimeMillis();
		Role role = new Role();
		role.setName("GUEST");
		for(int i = 0; i<10000; i++) {
			User user = new User();
			user.setRole(role);
			user.setEmail(fakeValuesService.bothify("????????###@gmail.com"));
			user.setPassword(faker.crypto().sha512());
			user.setName(faker.name().firstName());
			try{
				dao.getUserDao().insertUser(user);
			} catch (Exception e){}
		}
		long end = System.currentTimeMillis();
		System.out.println("Total time" + (end-start)/(double)1000000000);

	}

}
