package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import sequence.sequence_member.global.utils.BaseTimeEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "portfolio")
public class PortfolioEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(length = 500)
    private String portfolioUrl;

    @ManyToOne
    @JoinColumn
    private MemberEntity member;


    public static List<PortfolioEntity> toPortfolioEntity(List<String> portfolioNames, MemberEntity memberEntityCopy) {
        List<PortfolioEntity> portfolioEntities = new ArrayList<>();

        for(String portfolioName : portfolioNames){
            PortfolioEntity portfolioEntity = new PortfolioEntity();
            portfolioEntity.setPortfolioUrl(portfolioName);
            portfolioEntity.setMember(memberEntityCopy);
            portfolioEntities.add(portfolioEntity);
        }
        return portfolioEntities;
    }


    public PortfolioEntity(String url, MemberEntity member) {
        this.portfolioUrl = url;
        this.member = member;
    }
}
