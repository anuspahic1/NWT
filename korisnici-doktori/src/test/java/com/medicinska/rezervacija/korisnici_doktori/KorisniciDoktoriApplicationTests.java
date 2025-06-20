package com.medicinska.rezervacija.korisnici_doktori;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class KorisniciDoktoriApplicationTests {

	@Test
	void contextLoads() {
		// Test će proći ako Spring kontekst uspješno učita
	}
}