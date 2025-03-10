package sequence.sequence_member.archive.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateRequestDTO {
    
    @NotBlank(message = "댓글 내용을 입력해주세요.")
    @Size(max = 500, message = "댓글은 500자 이하로 입력해주세요.")
    private String content;
    
    @NotBlank(message = "작성자를 입력해주세요.")
    @Size(max = 50, message = "작성자명은 50자 이하로 입력해주세요.")
    private String writer;
    
    private Long parentId;  // null이면 일반 댓글, 값이 있으면 대댓글
    
    // 공백 제거 메서드 추가
    public String getWriter() {
        return writer != null ? writer.trim() : null;
    }
    
    public String getContent() {
        return content != null ? content.trim() : null;
    }
} 