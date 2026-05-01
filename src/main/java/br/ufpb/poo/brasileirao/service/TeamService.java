package br.ufpb.poo.brasileirao.service;

import br.ufpb.poo.brasileirao.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    private static final Logger log = LoggerFactory.getLogger(TeamService.class);
    private static final String DATA_FILE = "data/national_teams.json";

    public List<Team> getAllTeams() {
        List<Team> teams = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource(DATA_FILE);
            InputStream inputStream = resource.getInputStream();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(inputStream);

            for (JsonNode teamNode : root) {
                Team team = new Team();
                team.setName(teamNode.path("name").asText());
                team.setCountryCode(teamNode.path("countryCode").asText());
                team.setConfederation(teamNode.path("confederation").asText());
                team.setGroup(teamNode.path("group").asText());
                team.setStrength(teamNode.path("strength").asInt());
                team.setAttackStrength(teamNode.path("attackStrength").asInt());
                team.setDefenseStrength(teamNode.path("defenseStrength").asInt());
                team.setMidfieldStrength(teamNode.path("midfieldStrength").asInt());
                team.setFifaRanking(teamNode.path("fifaRanking").asInt());
                team.setFlagEmoji(teamNode.path("flagEmoji").asText(""));

                JsonNode playersNode = teamNode.path("players");
                if (playersNode.isArray()) {
                    for (JsonNode playerNode : playersNode) {
                        String playerName = playerNode.path("name").asText();
                        int playerStrength = playerNode.path("strength").asInt();
                        String positionStr = playerNode.path("position").asText().toUpperCase();

                        Player player;
                        switch (positionStr) {
                            case "GOALKEEPER" -> player = new Goalkeeper(playerName, playerStrength);
                            case "DEFENDER"   -> player = new Defender(playerName, playerStrength);
                            case "MIDFIELDER" -> player = new Midfielder(playerName, playerStrength);
                            case "FORWARD"    -> player = new Forward(playerName, playerStrength);
                            default -> {
                                player = new Forward(playerName, playerStrength);
                                log.warn("Unknown position '{}' for player '{}'; defaulting to FORWARD", positionStr, playerName);
                            }
                        }
                        team.addPlayer(player);
                    }
                }

                teams.add(team);
            }
        } catch (Exception e) {
            log.error("Failed to load teams from {}: {}", DATA_FILE, e.getMessage(), e);
        }
        return teams;
    }
}
