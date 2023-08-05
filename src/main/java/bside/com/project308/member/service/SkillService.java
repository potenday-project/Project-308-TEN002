package bside.com.project308.member.service;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.entity.Skill;
import bside.com.project308.member.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class SkillService {
    private final SkillRepository skillRepository;

    public List<String> getSkill(Position position) {
        List<Skill> skills = skillRepository.findByPosition(position);
        return skills.stream().map(Skill::getSkillName).toList();
    }
}
