package com.codeQuest.Chatterly;

import com.codeQuest.Chatterly.Entities.Servers;
import com.codeQuest.Chatterly.Repositories.ServerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ChatterlyApplicationTests {

	@Autowired
	private ServerRepository serverRepository;

	@Test
	void testFindById() {
		Optional<Servers> server = serverRepository.findById(1L);
		assertTrue(server.isPresent());
	}

}
