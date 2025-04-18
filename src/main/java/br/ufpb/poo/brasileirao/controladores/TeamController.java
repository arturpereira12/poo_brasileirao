package br.ufpb.poo.brasileirao.controladores;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.ufpb.poo.brasileirao.model.Team;



@Controller
@RequestMapping("/teams")
public class TeamController {

    @GetMapping
    public ModelAndView leiaTeams(ModelAndView modelAndView) {
        List<Team> teams = leiaDoArquivo();
        // Calcula a maior força dos jogadores de cada time
        for (Team team : teams) {
            int maxStrength = team.getPlayers().stream()
                .mapToInt(player -> player.getStrength())
                .max()
                .orElse(0);
            team.setMaxPlayerStrength(maxStrength);
        }
        System.out.println("Teams: "+teams);
        System.out.println("Teams size: "+teams.size());
        if (teams.get(0).getPlayers() == null) {
            System.out.println("No players found in the file.");
        } else {
            System.out.println("Teams found: " + teams.get(0).getPlayers());
        }
        modelAndView.setViewName("teams");
        modelAndView.addObject("teams", teams);
        return modelAndView;
    }
    @GetMapping("/att")
    public ModelAndView leiaTeamsAtt(ModelAndView modelAndView) {
        List<Team> teams = leiaDoArquivo();
        // Calcula a maior força dos jogadores de cada time
        for (Team team : teams) {
            int maxStrength = team.getPlayers().stream()
                .mapToInt(player -> player.getStrength())
                .max()
                .orElse(0);
            team.setMaxPlayerStrength(maxStrength);
        }
        System.out.println("Carregando visualização atualizada");
        modelAndView.setViewName("teams_att");
        modelAndView.addObject("teams", teams);
        return modelAndView;
    }

    @GetMapping("/att/{name}")
    public ModelAndView leiaTeamAtt(@PathVariable String name, ModelAndView modelAndView) {
        List<Team> teams = leiaDoArquivo();
        Team team = teams.stream()
                .filter(t -> t.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Time não encontrado"));
        
        modelAndView.setViewName("team_att");
        modelAndView.addObject("team", team);
        return modelAndView;
    }

    public List<Team> leiaDoArquivo() {
        List<Team> teams = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Usando ClassPathResource para acessar o arquivo na pasta resources
            org.springframework.core.io.Resource resource = 
                new org.springframework.core.io.ClassPathResource("data/teams_and_players.json");
            teams = mapper.readValue(resource.getInputStream(), new TypeReference<List<Team>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return teams;
    }

}
