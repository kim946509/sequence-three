package sequence.sequence_member.archive.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sequence.sequence_member.archive.dto.*;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.entity.ArchiveComment;
import sequence.sequence_member.archive.repository.ArchiveCommentRepository;
import sequence.sequence_member.archive.repository.ArchiveRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArchiveCommentService {

    private final ArchiveCommentRepository commentRepository;
    private final ArchiveRepository archiveRepository;

    // 아카이브 존재 여부 확인
    public boolean checkArchiveExists(Long archiveId) {
        return archiveRepository.existsById(archiveId);
    }

    // 댓글 작성
    @Transactional
    public Long createComment(Long archiveId, CommentCreateRequestDTO dto) {
        // 아카이브 조회
        Optional<Archive> archiveOpt = archiveRepository.findById(archiveId);
        if (archiveOpt.isEmpty()) {
            return null;
        }
        
        Archive archive = archiveOpt.get();

        // 대댓글인 경우 부모 댓글 검증
        ArchiveComment parent = null;
        if (dto.getParentId() != null) {
            Optional<ArchiveComment> parentOpt = commentRepository.findById(dto.getParentId());
            if (parentOpt.isEmpty()) {
                return null;
            }
            
            parent = parentOpt.get();
            
            // 대댓글의 대댓글 작성 방지
            if (parent.isReply()) {
                return null;
            }
        }

        ArchiveComment comment = ArchiveComment.builder()
                .archive(archive)
                .writer(dto.getWriter())
                .parent(parent)
                .content(dto.getContent())
                .isDeleted(false)
                .build();

        return commentRepository.save(comment).getId();
    }

    // 댓글 수정
    @Transactional
    public boolean updateComment(Long archiveId, Long commentId, String writer, CommentUpdateRequestDTO dto) {
        Optional<Archive> archiveOpt = archiveRepository.findById(archiveId);
        if (archiveOpt.isEmpty()) {
            return false;
        }
        
        Archive archive = archiveOpt.get();

        Optional<ArchiveComment> commentOpt = commentRepository.findByIdAndArchiveAndWriter(commentId, archive, writer);
        if (commentOpt.isEmpty()) {
            return false;
        }
        
        ArchiveComment comment = commentOpt.get();

        if (comment.isDeleted()) {
            return false;
        }

        comment.updateContent(dto.getContent());
        return true;
    }

    // 댓글 삭제
    @Transactional
    public boolean deleteComment(Long archiveId, Long commentId, String writer) {
        Optional<Archive> archiveOpt = archiveRepository.findById(archiveId);
        if (archiveOpt.isEmpty()) {
            return false;
        }
        
        Archive archive = archiveOpt.get();

        Optional<ArchiveComment> commentOpt = commentRepository.findByIdAndArchiveAndWriter(commentId, archive, writer);
        if (commentOpt.isEmpty()) {
            return false;
        }
        
        ArchiveComment comment = commentOpt.get();

        // 이미 삭제된 댓글인지 확인
        if (comment.isDeleted()) {
            return false;
        }

        comment.delete();
        return true;
    }

    // 댓글 목록 조회 (페이징)
    public CommentPageResponseDTO getComments(Long archiveId, int page) {
        // 아카이브 존재 확인
        if (!archiveRepository.existsById(archiveId)) {
            return CommentPageResponseDTO.builder()
                    .comments(List.of())
                    .totalPages(0)
                    .totalElements(0L)
                    .build();
        }

        Pageable pageable = PageRequest.of(page, 10);  // 한 페이지당 10개 댓글
        Page<ArchiveComment> commentPage = commentRepository.findParentCommentsByArchiveId(archiveId, pageable);

        List<CommentResponseDTO> commentDTOs = commentPage.getContent().stream()
            .map(comment -> {
                CommentResponseDTO dto = CommentResponseDTO.from(comment);
                // 대댓글이 있는 경우 대댓글 목록 추가
                List<CommentResponseDTO> replies = commentRepository.findRepliesByParentId(comment.getId())
                    .stream()
                    .map(CommentResponseDTO::from)
                    .collect(Collectors.toList());
                return dto.toBuilder()
                    .replies(replies)
                    .build();
            })
            .collect(Collectors.toList());

        return CommentPageResponseDTO.builder()
                .comments(commentDTOs)
                .totalPages(commentPage.getTotalPages())
                .totalElements(commentPage.getTotalElements())
                .build();
    }
} 