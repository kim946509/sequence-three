package sequence.sequence_member.mypage.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import sequence.sequence_member.member.entity.AwardEntity;
import sequence.sequence_member.member.entity.CareerEntity;
import sequence.sequence_member.member.entity.EducationEntity;
import sequence.sequence_member.member.entity.ExperienceEntity;
import sequence.sequence_member.member.entity.MemberEntity;
import sequence.sequence_member.member.repository.AwardRepository;
import sequence.sequence_member.member.repository.CareerRepository;
import sequence.sequence_member.member.repository.EducationRepository;
import sequence.sequence_member.member.repository.ExperienceRepository;
import sequence.sequence_member.member.repository.MemberRepository;
import sequence.sequence_member.mypage.dto.MyPageDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyPageService {

    private final MemberRepository memberRepository;
    private final AwardRepository awardRepository;
    private final CareerRepository careerRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;

    // 마이페이지 조회
    public MyPageDTO searchMyPage(String username) {
        // 1. 회원 정보 가져오기
        MemberEntity member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        log.info("member: {}", member.getEducation());

        return convertToDTO(member);
    }

    private MyPageDTO convertToDTO(MemberEntity member) {
        MyPageDTO dto = new MyPageDTO();
        dto.setUsername(member.getUsername());
        dto.setName(member.getName());
        dto.setBirth(member.getBirth());
        dto.setGender(member.getGender());
        dto.setAddress(member.getAddress());
        dto.setPhone(member.getPhone());
        dto.setIntroduction(member.getIntroduction());
        dto.setPortfolio(member.getPortfolio());
        dto.setNickname(member.getNickname());

        dto.setAwards(member.getAwards().stream()
                .map(award -> {
                    MyPageDTO.AwardDTO awardDTO = new MyPageDTO.AwardDTO();
                    awardDTO.setAwardType(award.getAwardType());
                    awardDTO.setOrganizer(award.getOrganizer());
                    awardDTO.setAwardName(award.getAwardName());
                    awardDTO.setAwardDuration(award.getAwardDuration());
                    return awardDTO;
                })
                .collect(Collectors.toList()));

        dto.setCareers(member.getCareers().stream()
                .map(career -> {
                    MyPageDTO.CareerDTO careerDTO = new MyPageDTO.CareerDTO();
                    careerDTO.setCompanyName(career.getCompanyName());
                    careerDTO.setCareerDuration(career.getCareerDuration());
                    careerDTO.setCareerDescription(career.getCareerDescription());
                    return careerDTO;
                })
                .collect(Collectors.toList()));

        dto.setExperiences(member.getExperiences().stream()
                .map(experience -> {
                    MyPageDTO.ExperienceDTO experienceDTO = new MyPageDTO.ExperienceDTO();
                    experienceDTO.setExperienceType(experience.getExperienceType());
                    experienceDTO.setExperienceName(experience.getExperienceName());
                    experienceDTO.setExperienceDuration(experience.getExperienceDuration());
                    experienceDTO.setExperienceDescription(experience.getExperienceDescription());
                    return experienceDTO;
                })
                .collect(Collectors.toList()));

        dto.setSchoolName(member.getEducation().getSchoolName());
        dto.setMajor(member.getEducation().getMajor());
        dto.setGrade(member.getEducation().getGrade());
        dto.setEntranceDate(member.getEducation().getEntranceDate());
        dto.setGraduationDate(member.getEducation().getGraduationDate());
        dto.setDegree(member.getEducation().getDegree());
        dto.setSkillCategory(member.getEducation().getSkillCategory());
        dto.setDesiredJob(member.getEducation().getDesiredJob());

        return dto;
    }

    /**
     * 사용자 마이페이지 정보를 업데이트하는 메서드입니다.
     *
     * 현재는 기존 데이터를 삭제하고 새로 입력된 데이터를 저장하는 방식으로 구현되어 있습니다.
     * 이 방식은 입력에 없는 데이터를 삭제하기 위해 선택되었습니다. 왜냐하면, 입력된 데이터와 기존 데이터를 비교하여
     * 어떤 데이터를 삭제할지 결정하는 로직을 구현하기 어려운 상황이기 때문입니다.
     * 이 방식은 데이터가 많을 경우 성능에 비효율적일 수 있으므로, 향후 효율적인 방식으로 개선이 필요합니다.
     */
    // 마이페이지 데이터 저장
    @Transactional
    public void updateMyPage(MyPageDTO myPageDTO, String username) {
        // 1. 회원 정보 가져오기
        MemberEntity member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다."));

        // 토큰과 body의 username이 같은지 확인
        if(!Objects.equals(myPageDTO.getUsername(), username)) throw new IllegalArgumentException("아이디는 변경할 수 없습니다.");

        // 기본 정보 업데이트 (변경 감지 사용)
        // username은 변경하지 안음.
        member.setName(myPageDTO.getName());
        member.setBirth(myPageDTO.getBirth());
        member.setGender(myPageDTO.getGender());
        member.setAddress(myPageDTO.getAddress());
        member.setPhone(myPageDTO.getPhone());
        member.setIntroduction(myPageDTO.getIntroduction());
        member.setPortfolio(myPageDTO.getPortfolio());
        member.setNickname(myPageDTO.getNickname());

        // 수상 내역 업데이트
        List<AwardEntity> existingAwards = awardRepository.findByMember(member);
        awardRepository.deleteAll(existingAwards);

        for (MyPageDTO.AwardDTO dto : myPageDTO.getAwards()) {
            AwardEntity newAward = new AwardEntity(dto.getAwardType(), dto.getOrganizer(), dto.getAwardName(), dto.getAwardDuration(), member);
            awardRepository.save(newAward);
        }

        // 경력 업데이트
        List<CareerEntity> existingCareers = careerRepository.findByMember(member);
        careerRepository.deleteAll(existingCareers);

        for (MyPageDTO.CareerDTO dto : myPageDTO.getCareers()) {
            CareerEntity newCareer = new CareerEntity(dto.getCompanyName(), dto.getCareerDuration(), dto.getCareerDescription(), member);
            careerRepository.save(newCareer);
        }

        // 경험 업데이트
        List<ExperienceEntity> existingExperiences = experienceRepository.findByMember(member);
        experienceRepository.deleteAll(existingExperiences);

        for (MyPageDTO.ExperienceDTO dto : myPageDTO.getExperiences()) {
            ExperienceEntity newExperience = new ExperienceEntity(dto.getExperienceType(), dto.getExperienceName(), dto.getExperienceDuration(), dto.getExperienceDescription(), member);
            experienceRepository.save(newExperience);
        }

        // 학력 업데이트 (없으면 생성, 있으면 수정)
        educationRepository.findByMember(member).ifPresentOrElse(
                education -> {
                    education.updateEducation(
                            myPageDTO.getSchoolName(),
                            myPageDTO.getMajor(),
                            myPageDTO.getGrade(),
                            myPageDTO.getEntranceDate(),
                            myPageDTO.getGraduationDate(),
                            myPageDTO.getDegree(),
                            myPageDTO.getSkillCategory(),
                            myPageDTO.getDesiredJob()
                    );
                },
                () -> {
                    EducationEntity newEducation = new EducationEntity(
                            myPageDTO.getSchoolName(),
                            myPageDTO.getMajor(),
                            myPageDTO.getGrade(),
                            myPageDTO.getEntranceDate(),
                            myPageDTO.getGraduationDate(),
                            myPageDTO.getDegree(),
                            myPageDTO.getSkillCategory(),
                            myPageDTO.getDesiredJob(),
                            member
                    );
                    educationRepository.save(newEducation);
                }
        );
    }
}
