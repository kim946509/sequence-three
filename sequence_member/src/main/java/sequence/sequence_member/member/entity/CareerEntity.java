package sequence.sequence_member.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sequence.sequence_member.global.utils.BaseTimeEntity;
import sequence.sequence_member.member.dto.MemberDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="career")
@NoArgsConstructor
public class CareerEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn
    private MemberEntity member;

    @Column(name = "company_name", length = 100,nullable = false)
    private String companyName;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date careerDuration;

    @Column(nullable = false)
    private String careerDescription;

    public CareerEntity(
            String companyName, Date careerDuration,
            String careerDescription, MemberEntity member
    ) {
        this.companyName = companyName;
        this.careerDuration = careerDuration;
        this.careerDescription = careerDescription;
        this.member = member;
    }

    public static List<CareerEntity> toCareerEntity(MemberDTO memberDTO, MemberEntity memberEntity){
        List<CareerEntity> careerEntities = new ArrayList<>();

        for(int i=0;i<memberDTO.getCareers().size();i++){
            CareerEntity careerEntity = new CareerEntity();

            careerEntity.setCompanyName(memberDTO.getCareers().get(i).getCompanyName());
            careerEntity.setCareerDescription(memberDTO.getCareers().get(i).getCareerDescription());
            careerEntity.setCareerDuration(memberDTO.getCareers().get(i).getCareerDuration());
            careerEntity.setMember(memberEntity);

            careerEntities.add(careerEntity);
        }
        return careerEntities;
    }
}
