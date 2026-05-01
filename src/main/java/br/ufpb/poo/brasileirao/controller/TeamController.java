package br.ufpb.poo.brasileirao.controller;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.service.TeamService;
import br.ufpb.poo.brasileirao.service.WorldCupManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private WorldCupManager worldCupManager;

    @Autowired
    private TeamService teamService;

    @GetMapping
    public String showTeams(Model model) {
        List<Team> teams = worldCupManager.isActive()
                ? worldCupManager.getAllTeams()
                : teamService.getAllTeams();

        // Sort by group then FIFA ranking
        List<Team> sorted = teams.stream()
                .sorted(Comparator.comparing((Team t) -> t.getGroup() == null ? "" : t.getGroup())
                        .thenComparingInt(Team::getFifaRanking))
                .collect(Collectors.toList());

        // Group by group letter
        Map<String, List<Team>> groupedByGroup = new LinkedHashMap<>();
        for (Team t : sorted) {
            String g = t.getGroup() == null ? "?" : t.getGroup();
            groupedByGroup.computeIfAbsent(g, k -> new ArrayList<>()).add(t);
        }

        model.addAttribute("teams", sorted);
        model.addAttribute("groupedByGroup", groupedByGroup);
        return "teams";
    }

    @GetMapping("/{code}")
    public String showTeamDetail(@PathVariable String code, Model model) {
        List<Team> allTeams = worldCupManager.isActive()
                ? worldCupManager.getAllTeams()
                : teamService.getAllTeams();

        Optional<Team> teamOpt = allTeams.stream()
                .filter(t -> code.equalsIgnoreCase(t.getCountryCode())
                        || code.equalsIgnoreCase(t.getName()))
                .findFirst();

        if (teamOpt.isEmpty()) {
            return "redirect:/teams";
        }

        Team team = teamOpt.get();

        // Collect all matches where this team participated
        List<Match> matches = Stream.concat(
                        worldCupManager.getAllGroupMatches().stream(),
                        worldCupManager.getAllKnockoutMatches().stream())
                .filter(m -> m.getHomeTeam().getName().equals(team.getName())
                        || m.getAwayTeam().getName().equals(team.getName()))
                .collect(Collectors.toList());

        model.addAttribute("team", team);
        model.addAttribute("matches", matches);
        return "team_detail";
    }
}
