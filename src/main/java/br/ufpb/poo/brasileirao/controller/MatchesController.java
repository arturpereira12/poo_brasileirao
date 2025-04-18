package br.ufpb.poo.brasileirao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ufpb.poo.brasileirao.match.Match;
import br.ufpb.poo.brasileirao.service.TournamentManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Controller para gerenciar a exibição de partidas do campeonato.
 */
@Controller
@RequestMapping("/matches")
public class MatchesController {

    @Autowired
    private TournamentManager tournamentManager;

    /**
     * Exibe todas as partidas do campeonato, organizadas por rodada.
     * 
     * @param round Rodada específica a ser exibida (opcional)
     * @param model Model para passar dados para a view
     * @return nome da view a ser renderizada
     */
    @GetMapping
    public String showMatches(@RequestParam(required = false) Integer round, Model model) {
        if (!tournamentManager.isActive()) {
            return "redirect:/";
        }

        int totalRounds = tournamentManager.getTotalRounds();
        int currentRound = tournamentManager.getCurrentRound();
        
        // Se uma rodada específica for solicitada, verifica se é válida
        if (round != null && (round < 1 || round > totalRounds)) {
            round = null; // Retorna para a visualização de todas as rodadas
        }
        
        // Prepara os dados para a view
        model.addAttribute("currentRound", currentRound);
        model.addAttribute("totalRounds", totalRounds);
        model.addAttribute("selectedRound", round);
        
        // Fornece a lista de todas as rodadas para o dropdown
        List<Integer> allRounds = IntStream.rangeClosed(1, totalRounds)
                                          .boxed()
                                          .collect(Collectors.toList());
        model.addAttribute("allRounds", allRounds);
        
        // Se uma rodada específica for selecionada, mostra apenas os jogos dessa rodada
        if (round != null) {
            List<Match> roundMatches = tournamentManager.getMatchesForRound(round);
            model.addAttribute("roundMatches", roundMatches);
        } else {
            // Caso contrário, organiza todos os jogos por rodada
            Map<Integer, List<Match>> matchesByRound = new HashMap<>();
            
            for (int i = 1; i <= totalRounds; i++) {
                List<Match> roundMatches = tournamentManager.getMatchesForRound(i);
                if (!roundMatches.isEmpty()) {
                    matchesByRound.put(i, roundMatches);
                }
            }
            
            model.addAttribute("matchesByRound", matchesByRound);
        }
        
        return "matches";
    }
} 