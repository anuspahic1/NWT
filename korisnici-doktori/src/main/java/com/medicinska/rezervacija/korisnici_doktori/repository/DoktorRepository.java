package com.medicinska.rezervacija.korisnici_doktori.repository;

import com.medicinska.rezervacija.korisnici_doktori.model.Doktor;
import com.medicinska.rezervacija.korisnici_doktori.repository.projections.DoctorStatistics;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoktorRepository extends JpaRepository<Doktor, Integer>, JpaSpecificationExecutor<Doktor> {

    Optional<Doktor> findByUserId(Long userId);
    Optional<Doktor> findByEmail(String email);

    @Query("SELECT d FROM Doktor d WHERE :naziv MEMBER OF d.specijalizacije")
    List<Doktor> findBySpecijalizacijaContains(@Param("naziv") String naziv);

    @Query("SELECT d FROM Doktor d WHERE d.ocjena >= :minOcjena AND d.iskustvo >= :minIskustvo ORDER BY d.ocjena DESC")
    List<Doktor> findTopRatedDoctors(@Param("minOcjena") double minOcjena, @Param("minIskustvo") int minIskustvo);

    @Query("SELECT d FROM Doktor d ORDER BY d.ocjena DESC")
    List<Doktor> findTopNDoktori(Pageable pageable);

    @Query("SELECT d FROM Doktor d WHERE SIZE(d.specijalizacije) >= :minSpecijalizacija")
    List<Doktor> findByNumberOfSpecializations(@Param("minSpecijalizacija") int minSpecijalizacija);

    List<Doktor> findByGrad(String grad);

    List<Doktor> findByImeAndPrezime(String ime, String prezime);

    List<Doktor> findByImeContainingIgnoreCaseAndPrezimeContainingIgnoreCase(String ime, String prezime);


    @Query("SELECT d FROM Doktor d WHERE lower(d.grad) LIKE lower(concat('%', :grad, '%')) AND :specijalizacija MEMBER OF d.specijalizacije")
    List<Doktor> findByGradAndSpecijalizacija(@Param("grad") String grad, @Param("specijalizacija") String specijalizacija);

    @Query("SELECT d FROM Doktor d WHERE " +
            "(:ime IS NULL OR d.ime LIKE %:ime%) AND " +
            "(:grad IS NULL OR d.grad LIKE %:grad%) AND " +
            "(:minOcjena IS NULL OR d.ocjena >= :minOcjena)")
    List<Doktor> advancedDoctorSearch(@Param("ime") String ime,
                                      @Param("grad") String grad,
                                      @Param("minOcjena") Double minOcjena);


    @Query("SELECT new com.medicinska.rezervacija.korisnici_doktori.repository.projections.DoctorStatistics(d.grad, COUNT(d.id), AVG(d.ocjena)) " +
            "FROM Doktor d GROUP BY d.grad")
    List<DoctorStatistics> getDoctorStatsByCity();


}
