package sequence.sequence_member.archive.controller;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.archive.dto.ArchiveDTO;
import sequence.sequence_member.archive.service.ArchiveService;

import java.util.List;

@RestController
@RequestMapping("/api/archives")
public class ArchiveController {

    @Autowired
    private ArchiveService archiveService;

    // Archive 저장 (Create)
    @PostMapping
    public ResponseEntity<ArchiveDTO> createArchive(@RequestBody ArchiveDTO archiveDTO) {
        ArchiveDTO saveArchive = archiveService.saveArchive(archiveDTO);
        return new ResponseEntity<>(saveArchive, HttpStatus.CREATED);
    }
}
