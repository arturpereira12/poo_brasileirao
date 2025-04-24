package br.ufpb.poo.brasileirao.service;

import br.ufpb.poo.brasileirao.model.Team;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    public List<Team> getAllTeams() {
        return leiaDoArquivo();
    }

    private List<Team> leiaDoArquivo() {
        List<Team> times = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("times.csv"))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                String[] dados = linha.split(",");
                if (dados.length >= 2) {
                    String nome = dados[0].trim();
                    int forca = Integer.parseInt(dados[1].trim());
                    Team time = new Team(nome);
                    time.setStrength(forca);
                    // Outros atributos podem ser lidos se existirem
                    times.add(time);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de times: " + e.getMessage());
        }
        return times;
    }
} 