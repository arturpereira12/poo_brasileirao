package br.ufpb.poo.brasileirao.controller;

import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.service.TeamService;
import br.ufpb.poo.brasileirao.service.WorldCupManager;
import br.ufpb.poo.brasileirao.tournament.TournamentPhase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    private WorldCupManager worldCupManager;

    @Autowired
    private TeamService teamService;

    @GetMapping("/")
    public String index(org.springframework.ui.Model model) {
        model.addAttribute("active", worldCupManager.isActive());
        model.addAttribute("phase", worldCupManager.getPhase());
        return "home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @PostMapping("/iniciar-copa")
    public String iniciarCopa(org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            List<Team> teams = teamService.getAllTeams();
            worldCupManager.initialize(teams);
            return "redirect:/groups";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Falha ao iniciar o torneio: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/status")
    @ResponseBody
    public Map<String, Object> status() {
        Map<String, Object> map = new HashMap<>();
        map.put("active", worldCupManager.isActive());
        map.put("phase", worldCupManager.getPhase().name());
        map.put("currentGroupMatchDay", worldCupManager.getCurrentGroupMatchDay());
        map.put("isTournamentCompleted", worldCupManager.isTournamentCompleted());
        return map;
    }
}
