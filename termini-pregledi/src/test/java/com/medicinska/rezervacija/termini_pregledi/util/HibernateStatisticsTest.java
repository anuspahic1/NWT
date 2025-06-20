/*package com.medicinska.rezervacija.termini_pregledi.util;

import com.medicinska.rezervacija.termini_pregledi.service.*;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HibernateStatisticsTest {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private DoktorService doktorService;

    @Autowired
    private PacijentService pacijentService;

    @Autowired
    private PregledService pregledService;

    @Autowired
    private SpecijalizacijaService specijalizacijaService;

    @Autowired
    private TerminService terminService;


    @Test
    @Transactional
    void testNPlusOneProblemForDoktori() {
        // Omogućavanje Hibernate statistike
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();  // Resetujemo brojač upita prije testa

        // Poziv metode koja bi mogla izazvati N+1 problem
        doktorService.getAllDoktori();

        // Dohvaćanje broja izvršenih upita
        long queryCount = statistics.getQueryExecutionCount();

        System.out.println("Broj izvršenih SQL upita za doktore: " + queryCount);

        // Ako imamo N+1 problem, broj upita će biti značajno veći od očekivanog
        assertTrue(queryCount < 5, "Previše SQL upita! Potencijalni N+1 problem za doktore."); // Promijenjen prag
    }

    @Test
    @Transactional
    void testNPlusOneProblemForPacijenti() {
        // Omogućavanje Hibernate statistike
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();  // Resetujemo brojač upita prije testa

        // Poziv metode koja bi mogla izazvati N+1 problem
        pacijentService.getAllPacijenti();

        // Dohvaćanje broja izvršenih upita
        long queryCount = statistics.getQueryExecutionCount();

        System.out.println("Broj izvršenih SQL upita za pacijente: " + queryCount);

        // Ako imamo N+1 problem, broj upita će biti značajno veći od očekivanog
        assertTrue(queryCount < 5, "Previše SQL upita! Potencijalni N+1 problem za pacijente."); // Promijenjen prag
    }

    @Test
    @Transactional
    void testNPlusOneProblemForPregledi() {
        // Omogućavanje Hibernate statistike
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();  // Resetujemo brojač upita prije testa

        // Poziv metode koja bi mogla izazvati N+1 problem
        pregledService.getAllPregledi();

        // Dohvaćanje broja izvršenih upita
        long queryCount = statistics.getQueryExecutionCount();

        System.out.println("Broj izvršenih SQL upita za preglede: " + queryCount);

        // Ako imamo N+1 problem, broj upita će biti značajno veći od očekivanog
        assertTrue(queryCount < 5, "Previše SQL upita! Potencijalni N+1 problem za preglede."); // Promijenjen prag
    }

    @Test
    @Transactional
    void testNPlusOneProblemForSpecijalizacije() {
        // Omogućavanje Hibernate statistike
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();  // Resetujemo brojač upita prije testa

        // Poziv metode koja bi mogla izazvati N+1 problem
        specijalizacijaService.getAllSpecijalizacije();

        // Dohvaćanje broja izvršenih upita
        long queryCount = statistics.getQueryExecutionCount();

        System.out.println("Broj izvršenih SQL upita za specijalizacije: " + queryCount);

        // Ako imamo N+1 problem, broj upita će biti značajno veći od očekivanog
        assertTrue(queryCount < 5, "Previše SQL upita! Potencijalni N+1 problem za specijalizacije."); // Promijenjen prag
    }

    @Test
    @Transactional
    void testNPlusOneProblemForTermini() {
        // Omogućavanje Hibernate statistike
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();  // Resetujemo brojač upita prije testa

        // Poziv metode koja bi mogla izazvati N+1 problem
        Pageable pageable = PageRequest.of(0, 10);
        terminService.getAllTerminiPaged(pageable);

        // Dohvaćanje broja izvršenih upita
        long queryCount = statistics.getQueryExecutionCount();

        System.out.println("Broj izvršenih SQL upita za termine: " + queryCount);

        // Ako imamo N+1 problem, broj upita će biti značajno veći od očekivanog
        assertTrue(queryCount < 5, "Previše SQL upita! Potencijalni N+1 problem za termine."); // Promijenjen prag
    }
}

*/