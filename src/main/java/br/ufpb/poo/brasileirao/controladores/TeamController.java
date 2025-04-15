package br.ufpb.poo.brasileirao.controladores;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    private List<Team> leiaDoArquivo() {
        List<Team> teams = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            // Usando ClassPathResource para acessar o arquivo na pasta resources
            org.springframework.core.io.Resource resource = 
                new org.springframework.core.io.ClassPathResource("data/teams.json");
            teams = mapper.readValue(resource.getInputStream(), new TypeReference<List<Team>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return teams;
    }
/* 
    @PostMapping()
    public ResponseEntity<Aluno> salveAluno(@RequestBody Aluno aluno) {
        salvarAlunoEmArquivo(aluno);
        return ResponseEntity.status(HttpStatus.OK).body(aluno);
    }

    private void salvarAlunoEmArquivo(Aluno aluno) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // Suporte para LocalDate
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            // Lê o arquivo existente (se já existir)
            File arquivo = new File("alunos.json");
            List<Aluno> alunos;
            
            // Se o arquivo não existir ou estiver vazio, cria uma nova lista
            if (arquivo.exists() && arquivo.length() > 0) {
                System.out.println("Caiu no if");
                alunos = mapper.readValue(arquivo, new TypeReference<List<Aluno>>() {});
            } else {
                System.out.println("Caiu no else");
                alunos = new java.util.ArrayList<>();
            }

            // Adiciona o novo aluno à lista
            alunos.add(aluno);
            System.out.println(alunos);

            // Escreve a lista de volta no arquivo (atualizada)
            mapper.writeValue(arquivo, alunos);

        } catch (IOException e) {
            e.printStackTrace(); // ou log.error(...)
        }
    }
    

    private static Aluno criarAluno(String nome, int cra, boolean ativo, LocalDate dataNascimento) {
        Aluno aluno = new Aluno();
        aluno.setNome(nome);
        aluno.setCra(cra);
        aluno.setAtivo(ativo);
        aluno.setDataNascimento(dataNascimento);
        return aluno;
    }
*/
}
