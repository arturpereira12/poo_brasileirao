package br.ufpb.poo.brasileirao.controller;

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
import java.util.Map;

@Controller
@RequestMapping("/bracket")
public class BracketController {

    @Autowired
    private WorldCupManager worldCupManager;

    @GetMapping
    public String showBracket(Model model) {
        TournamentPhase phase = worldCupManager.getPhase();
        boolean groupStageComplete = phase != TournamentPhase.GROUP_STAGE
                && phase != TournamentPhase.NOT_STARTED;

        model.addAttribute("r32", worldCupManager.getR32Matches());
        model.addAttribute("r16", worldCupManager.getR16Matches());
        model.addAttribute("quarterFinals", worldCupManager.getQuarterFinals());
        model.addAttribute("semiFinals", worldCupManager.getSemiFinals());
        model.addAttribute("thirdPlace", worldCupManager.getThirdPlaceMatch());
        model.addAttribute("finalMatch", worldCupManager.getFinalMatch());
        model.addAttribute("champion", worldCupManager.getChampion());
        model.addAttribute("phase", phase);
        model.addAttribute("groupStageComplete", groupStageComplete);

        return "bracket";
    }

    @PostMapping("/simulate-round")
    @ResponseBody
    public Map<String, Object> simulateRound() {
        Map<String, Object> response = new HashMap<>();
        boolean simulated = worldCupManager.simulateCurrentKnockoutRound();
        TournamentPhase phase = worldCupManager.getPhase();

        if (simulated) {
            response.put("status", "success");
            response.put("message", "Rodada simulada com sucesso!");
        } else {
            response.put("status", "error");
            response.put("message", "Nao foi possivel simular: verifique a fase atual.");
        }

        response.put("phase", phase.name());
        response.put("isCompleted", worldCupManager.isTournamentCompleted());
        return response;
    }
}
