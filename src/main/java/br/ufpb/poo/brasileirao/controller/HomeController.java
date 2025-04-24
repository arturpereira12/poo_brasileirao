package br.ufpb.poo.brasileirao.controller;

import br.ufpb.poo.brasileirao.model.Team;
import br.ufpb.poo.brasileirao.service.TournamentManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class HomeController {

    @Autowired
    private TeamController teamController;
    
    @Autowired
    private TournamentManager tournamentManager;

    @GetMapping("/")
    public String index() {
        return "home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @PostMapping("/iniciar-campeonato")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> iniciarCampeonato() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Team> allTeams = teamController.leiaDoArquivo();
            if (allTeams == null || allTeams.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Não foi possível carregar os times para o campeonato!");
                return ResponseEntity.ok(response);
            }
            
            // Limitar a 20 times
            List<Team> selectedTeams = allTeams.subList(0, Math.min(20, allTeams.size()));
            
            // Inicializar o campeonato no TournamentManager
            tournamentManager.addTeams(selectedTeams);
            tournamentManager.startTournament();
            
            response.put("status", "success");
            response.put("message", "Campeonato inicializado com sucesso! Redirecionando...");
            
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Erro ao iniciar o campeonato: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/teams-page")
    public String teamsPage(Model model) {
        List<Team> teams = teamController.leiaDoArquivo();
        model.addAttribute("teams", teams);
        return "teams_att";
    }
} 