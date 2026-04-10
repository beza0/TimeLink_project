package com.timebank.timebank.skill;

import com.timebank.timebank.skill.dto.CreateSkillRequest;
import com.timebank.timebank.skill.dto.SkillResponse;
import com.timebank.timebank.skill.dto.UpdateSkillRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(
            @Valid @RequestBody CreateSkillRequest req,
            Authentication authentication
    ) {
        SkillResponse response = skillService.createSkill(req, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    @GetMapping("/{skillId}")
    public ResponseEntity<SkillResponse> getSkillById(@PathVariable UUID skillId) {
        return ResponseEntity.ok(skillService.getSkillById(skillId));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<SkillResponse>> getMySkills(Authentication authentication) {
        return ResponseEntity.ok(skillService.getMySkills(authentication.getName()));
    }

    @PutMapping("/{skillId}")
    public ResponseEntity<SkillResponse> updateSkill(
            @PathVariable UUID skillId,
            @Valid @RequestBody UpdateSkillRequest req,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                skillService.updateSkill(skillId, req, authentication.getName())
        );
    }

    @DeleteMapping("/{skillId}")
    public ResponseEntity<Void> deleteSkill(
            @PathVariable UUID skillId,
            Authentication authentication
    ) {
        skillService.deleteSkill(skillId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}