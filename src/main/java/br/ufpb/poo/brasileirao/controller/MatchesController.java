package br.ufpb.poo.brasileirao.controller;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.service.WorldCupManager;
import br.ufpb.poo.brasileirao.tournament.TournamentPhase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/matches")
public class MatchesController {

    @Autowired
    private WorldCupManager worldCupManager;

    @GetMapping
    public String showMatches(
            @RequestParam(required = false, defaultValue = "GROUP") String phase,
            @RequestParam(required = false, defaultValue = "1") int day,
            Model model) {

        List<Match> matches = getAllMatchesByPhase(phase, day);

        model.addAttribute("matches", matches);
        model.addAttribute("selectedPhase", phase);
        model.addAttribute("selectedDay", day);
        model.addAttribute("phase", worldCupManager.getPhase());
        model.addAttribute("groupDays", Arrays.asList(1, 2, 3));

        return "matches";
    }

    private List<Match> getAllMatchesByPhase(String phase, int day) {
        return switch (phase.toUpperCase()) {
            case "GROUP" -> {
                int safeDay = Math.max(1, Math.min(day, 3));
                yield worldCupManager.getAllGroupMatches().stream()
                        .filter(m -> m.getRound() == safeDay || safeDay == 0)
                        .collect(Collectors.toList());
            }
            case "R32"   -> worldCupManager.getR32Matches();
            case "R16"   -> worldCupManager.getR16Matches();
            case "QF"    -> worldCupManager.getQuarterFinals();
            case "SF"    -> worldCupManager.getSemiFinals();
            case "FINAL" -> {
                List<Match> finals = new java.util.ArrayList<>();
                if (worldCupManager.getThirdPlaceMatch() != null)
                    finals.add(worldCupManager.getThirdPlaceMatch());
                if (worldCupManager.getFinalMatch() != null)
                    finals.add(worldCupManager.getFinalMatch());
                yield finals;
            }
            default -> worldCupManager.getAllGroupMatches();
        };
    }
}
