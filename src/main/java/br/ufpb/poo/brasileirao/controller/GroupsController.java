package br.ufpb.poo.brasileirao.controller;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.service.WorldCupManager;
import br.ufpb.poo.brasileirao.tournament.TournamentPhase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/groups")
public class GroupsController {

    @Autowired
    private WorldCupManager worldCupManager;

    @GetMapping
    public String showGroups(Model model) {
        boolean active = worldCupManager.isActive();
        model.addAttribute("active", active);
        model.addAttribute("notStarted", !active);

        if (!active) {
            return "groups";
        }

        int currentDay = worldCupManager.getCurrentGroupMatchDay();
        TournamentPhase phase = worldCupManager.getPhase();

        model.addAttribute("groups", worldCupManager.getGroups());
        model.addAttribute("phase", phase);
        model.addAttribute("currentDay", currentDay);
        model.addAttribute("stats", worldCupManager.getTournamentStats());

        // Show matches: if day > 1 show the last simulated day, otherwise show pending day 1
        List<Match> dayMatches;
        if (currentDay > 1 && currentDay <= 4) {
            // last simulated day is currentDay - 1
            dayMatches = worldCupManager.getGroupMatchesForDay(currentDay - 1);
        } else if (currentDay == 1) {
            dayMatches = worldCupManager.getGroupMatchesForDay(1);
        } else {
            // group stage finished (currentDay > 3 advanced to next phase)
            dayMatches = worldCupManager.getGroupMatchesForDay(3);
        }
        model.addAttribute("dayMatches", dayMatches);

        return "groups";
    }

    @PostMapping("/simulate-day")
    @ResponseBody
    public Map<String, Object> simulateDay() {
        Map<String, Object> response = new HashMap<>();
        int dayBefore = worldCupManager.getCurrentGroupMatchDay();
        boolean simulated = worldCupManager.simulateCurrentGroupMatchDay();
        TournamentPhase phase = worldCupManager.getPhase();

        if (simulated) {
            response.put("status", "success");
            response.put("message", "Rodada " + dayBefore + " da fase de grupos simulada com sucesso!");
        } else {
            response.put("status", "error");
            response.put("message", "Nao foi possivel simular: fase de grupos ja concluida ou torneio nao iniciado.");
        }

        response.put("phase", phase.name());
        response.put("currentDay", worldCupManager.getCurrentGroupMatchDay());
        response.put("groupStageComplete", phase != TournamentPhase.GROUP_STAGE && phase != TournamentPhase.NOT_STARTED);
        return response;
    }

    @PostMapping("/simulate-all")
    @ResponseBody
    public Map<String, Object> simulateAll() {
        Map<String, Object> response = new HashMap<>();
        int rounds = 0;

        while (worldCupManager.getPhase() == TournamentPhase.GROUP_STAGE) {
            boolean ok = worldCupManager.simulateCurrentGroupMatchDay();
            if (!ok) break;
            rounds++;
        }

        TournamentPhase phase = worldCupManager.getPhase();

        if (rounds > 0) {
            response.put("status", "success");
            response.put("message", rounds + " rodada(s) da fase de grupos simulada(s).");
        } else {
            response.put("status", "error");
            response.put("message", "Nenhuma rodada disponivel para simular.");
        }

        response.put("phase", phase.name());
        response.put("currentDay", worldCupManager.getCurrentGroupMatchDay());
        response.put("groupStageComplete", phase != TournamentPhase.GROUP_STAGE && phase != TournamentPhase.NOT_STARTED);
        return response;
    }
}
