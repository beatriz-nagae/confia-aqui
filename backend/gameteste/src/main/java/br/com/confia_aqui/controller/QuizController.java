package br.com.confia_aqui.controller;

import br.com.confia_aqui.model.Question;
import br.com.confia_aqui.model.QuestionWrapper;
import br.com.confia_aqui.model.Response;
import br.com.confia_aqui.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
@CrossOrigin(origins = "*") // Quando hospedarmos o projeto, mudar para a URL REAL PROD
public class QuizController {

    @Autowired
    QuizService quizService;

    //FIX: Validações completas dos parâmetros
    @PostMapping("create")
    public ResponseEntity<String> createQuiz(
            @RequestParam String category,
            @RequestParam int numQ,
            @RequestParam String title) {

        // Validação: numero questions deve estar entre 1 e 50
        if (numQ <= 0) {
            return ResponseEntity.badRequest()
                    .body("O número de questões deve ser maior que zero");
        }

        if (numQ > 50) {
            return ResponseEntity.badRequest()
                    .body("O número máximo de questões é 50");
        }

        // Validação: título não pode ser null
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("O título do quiz não pode ser vazio");
        }

        if (title.length() > 200) {
            return ResponseEntity.badRequest()
                    .body("O título do quiz não pode ter mais de 200 caracteres");
        }

        // Validação: categoria não pode ser null
        if (category == null || category.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A categoria não pode ser vazia");
        }

        // IF PASSED ALL VALIDATIONS, ENTAO cria o quiz
        return quizService.createQuiz(category.trim(), numQ, title.trim());
    }

    //FIX: Validação do ID
    @GetMapping("get/{id}")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(@PathVariable Integer id) {

        // Validação: ID deve ser positivo
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        return quizService.getQuizQuestions(id);
    }

    // FIX: Validações do ID e das respostas
    @PostMapping("submit/{id}")
    public ResponseEntity<Integer> submitQuiz(
            @PathVariable Integer id,
            @RequestBody List<Response> responses) {

        // Validação: ID deve ser positivo
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        // Validação: Lista de respostas não pode ser nula ou vazia
        if (responses == null || responses.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Validação: Verifica se todas as respostas têm ID e resposta válidos
        for (Response response : responses) {
            if (response.getId() == null || response.getResponse() == null ||
                    response.getResponse().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
        }

        return quizService.calculateResult(id, responses);
    }
}
