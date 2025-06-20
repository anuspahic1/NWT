package com.medicinska.rezervacija.obavijesti_dokumentacija.controller;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DokumentacijaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.service.DokumentacijaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dokumentacija")
public class DokumentacijaController {

    @Autowired
    private DokumentacijaService dokumentacijaService;

    @PostMapping("/add")
    public DokumentacijaDTO addMedicalRecord(@RequestBody DokumentacijaDTO dokumentacijaDTO) {
        return dokumentacijaService.createDokumentacija(dokumentacijaDTO);
    }

    @GetMapping("/{id}")
    public DokumentacijaDTO getMedicalRecordById(@PathVariable Long id) {
        return dokumentacijaService.getDokumentacijaById(id);
    }

    @GetMapping("/pacijent/{pacijentId}")
    public List<DokumentacijaDTO> getDokumentacijaByPacijentId(@PathVariable Long pacijentId) {
        return dokumentacijaService.getDokumentacijaByPacijentId(pacijentId);
    }

    @GetMapping("/doktor/{doktorId}")
    public List<DokumentacijaDTO> getDokumentacijaByDoktorId(@PathVariable Long doktorId) {
        return dokumentacijaService.getDokumentacijaByDoktorId(doktorId);
    }

    @PostMapping("/upload")
    public ResponseEntity<DokumentacijaDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "pacijentID", required = false) Optional<Long> pacijentID, // Sada je opciono
            @RequestParam(value = "doktorID", required = false) Optional<Long> doktorID,   // Sada je opciono
            @RequestParam("tipDokumenta") String tipDokumenta,
            @RequestParam("pristup") String pristup) {
        try {
            DokumentacijaDTO uploadedDocument = dokumentacijaService.uploadDocument(file, pacijentID, doktorID, tipDokumenta, pristup);
            return new ResponseEntity<>(uploadedDocument, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Greška prilikom uploada dokumenta: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<ByteArrayResource> downloadDocument(@PathVariable Long id) {
        try {
            DokumentacijaDTO dokument = dokumentacijaService.downloadDocument(id);

            if (dokument == null || dokument.getSadrzajDokumenta() == null) {
                return ResponseEntity.notFound().build();
            }

            ByteArrayResource resource = new ByteArrayResource(dokument.getSadrzajDokumenta());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + dokument.getNazivDokumenta())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(resource);
        } catch (Exception e) {
            System.err.println("Greška prilikom preuzimanja dokumenta: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}