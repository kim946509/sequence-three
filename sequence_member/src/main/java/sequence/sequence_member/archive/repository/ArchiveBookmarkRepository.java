package sequence.sequence_member.archive.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import sequence.sequence_member.archive.entity.Archive;
import sequence.sequence_member.archive.entity.ArchiveBookmark;


@Repository
public interface ArchiveBookmarkRepository extends JpaRepository<ArchiveBookmark, Long> {
    Optional<ArchiveBookmark> findByArchiveAndUsername(Archive archive, String username);
    void deleteByArchiveAndUsername(Archive archive, String username);
    boolean existsByArchiveAndUsername(Archive archive, String username);
    List<ArchiveBookmark> findAllByUsername(String username);  // 특정 사용자의 북마크 목록 조회
    long countByArchive(Archive archive);  // 특정 아카이브의 북마크 수를 반환하는 메서드
} 