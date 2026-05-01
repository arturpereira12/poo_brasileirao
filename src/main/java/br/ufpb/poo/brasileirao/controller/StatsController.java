package br.ufpb.poo.brasileirao.controller;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.service.WorldCupManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/stats")
public class StatsController {

    @Autowired
    private WorldCupManager worldCupManager;

    @GetMapping
    public String showStats(Model model) {
        List<Match> allGroupMatches = worldCupManager.getAllGroupMatches().stream()
                .filter(Match::isPlayed)
                .collect(java.util.stream.Collectors.toList());

        List<Match> allKnockoutMatches = worldCupManager.getAllKnockoutMatches().stream()
                .filter(Match::isPlayed)
                .collect(java.util.stream.Collectors.toList());

        int totalGoals = allGroupMatches.stream().mapToInt(m -> m.getHomeScore() + m.getAwayScore()).sum()
                + allKnockoutMatches.stream().mapToInt(m -> m.getHomeScore() + m.getAwayScore()).sum();
        int totalMatches = allGroupMatches.size() + allKnockoutMatches.size();
        double avgGoals = totalMatches > 0
                ? Math.round((double) totalGoals / totalMatches * 10.0) / 10.0
                : 0.0;

        model.addAttribute("topScorers", worldCupManager.getTopScorers().getTopScorers(20));
        model.addAttribute("stats", worldCupManager.getTournamentStats());
        model.addAttribute("phase", worldCupManager.getPhase());
        model.addAttribute("groups", worldCupManager.getGroups());
        model.addAttribute("champion", worldCupManager.getChampion());
        model.addAttribute("allGroupMatches", allGroupMatches);
        model.addAttribute("allKnockoutMatches", allKnockoutMatches);
        model.addAttribute("totalGoals", totalGoals);
        model.addAttribute("totalMatches", totalMatches);
        model.addAttribute("avgGoals", avgGoals);

        return "stats";
    }
}
