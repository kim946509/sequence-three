package sequence.sequence_member.archive.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sequence.sequence_member.archive.dto.ArchiveOutputDTO;
import sequence.sequence_member.archive.dto.ArchivePageResponseDTO;
import sequence.sequence_member.archive.dto.ArchiveRegisterInputDTO;
import sequence.sequence_member.archive.dto.ArchiveUpdateDTO;
import sequence.sequence_member.archive.service.ArchiveService;
import sequence.sequence_member.global.enums.enums.Category;
import sequence.sequence_member.global.enums.enums.SortType;
import sequence.sequence_member.global.response.ApiResponseData;
import sequence.sequence_member.global.response.Code;
import sequence.sequence_member.member.dto.CustomUserDetails;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/archive")
public class ArchiveController {

    private final ArchiveService archiveService;

    // 아카이브 등록
    @PostMapping
    public ResponseEntity<ApiResponseData<String>> createArchive(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ArchiveRegisterInputDTO archiveRegisterInputDTO) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        archiveService.createArchive(archiveRegisterInputDTO, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseData.success(null, "아카이브 등록 성공"));
    }

    // 아카이브 등록 후 결과 조회
    @GetMapping("/{archiveId}")
    public ResponseEntity<ApiResponseData<ArchiveOutputDTO>> getArchiveById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("archiveId") Long archiveId) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        return ResponseEntity.ok().body(ApiResponseData.of(
            Code.SUCCESS.getCode(), 
            "아카이브 상세 조회 성공", 
            archiveService.getArchiveById(archiveId, userDetails.getUsername())
        ));
    }

    // 아카이브 수정
    @PutMapping("/{archiveId}")
    public ResponseEntity<ApiResponseData<String>> updateArchive(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("archiveId") Long archiveId,
            @Valid @RequestBody ArchiveUpdateDTO archiveUpdateDTO) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        archiveService.updateArchive(archiveId, archiveUpdateDTO, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseData.success(null, "아카이브 수정 성공"));
    }

    // 아카이브 삭제
    @DeleteMapping("/{archiveId}")
    public ResponseEntity<ApiResponseData<String>> deleteArchiveById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("archiveId") Long archiveId) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        archiveService.deleteArchive(archiveId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponseData.success(null, "아카이브 삭제 성공"));
    }

    // 전체 리스트 조회
    @GetMapping("/projects")
    public ResponseEntity<ApiResponseData<ArchivePageResponseDTO>> getArchiveList(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "LATEST") SortType sortType) {
        
        return ResponseEntity.ok().body(ApiResponseData.of(
                Code.SUCCESS.getCode(),
                "아카이브 프로젝트 리스트 조회에 성공했습니다.",
                archiveService.getAllArchives(page, sortType, userDetails.getUsername())
        ));
    }

    // 검색 (카테고리 또는 키워드)
    @GetMapping("/projects/search")
    public ResponseEntity<ApiResponseData<ArchivePageResponseDTO>> searchArchives(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "LATEST") SortType sortType) {
        
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }

        String username = userDetails.getUsername();
        ArchivePageResponseDTO response;
        
        if (category != null) {
            response = archiveService.searchByCategory(category, page, sortType, username);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            response = archiveService.searchByTitle(keyword, page, sortType, username);
        } else {
            response = archiveService.getAllArchives(page, sortType, username);
        }
        
        return ResponseEntity.ok().body(ApiResponseData.of(
                Code.SUCCESS.getCode(),
                "검색 결과를 성공적으로 조회했습니다.",
                response
        ));
    }
} 