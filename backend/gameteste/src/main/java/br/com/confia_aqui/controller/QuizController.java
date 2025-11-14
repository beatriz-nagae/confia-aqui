package br.com.confia_aqui.controller;

import br.com.confia_aqui.model.QuestionWrapper;
import br.com.confia_aqui.model.Response;
import br.com.confia_aqui.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("quiz")
@CrossOrigin(origins = "*") //substituir por URL certa caso hospedado
public class QuizController {

    @Autowired
    QuizService quizService;

    //cria novo quiz com perguntas aleatorias do banco de dados
    @PostMapping("create")
    public ResponseEntity<String> createQuiz(
            @RequestParam String category,
            @RequestParam int numQ,
            @RequestParam String title) {

        //valida se numQuestoes esta entre 1 e 50
        if (numQ <= 0) {
            return ResponseEntity.badRequest()
                    .body("O número de questões deve ser maior que zero");
        }

        if (numQ > 50) {
            return ResponseEntity.badRequest()
                    .body("O número máximo de questões é 50");
        }

        //valida se titulo esta vazio
        if (title == null || title.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("O título do quiz não pode ser vazio");
        }

        if (title.length() > 200) {
            return ResponseEntity.badRequest()
                    .body("O título do quiz não pode ter mais de 200 caracteres");
        }

        //valida de categoria esta vazia
        if (category == null || category.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A categoria não pode ser vazia");
        }

        return quizService.createQuiz(category.trim(), numQ, title.trim());
    }

    //getta perguntas do quiz SEM exibir as respostas certas p/ o user
    @GetMapping("get/{id}")
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(@PathVariable Integer id) {

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        return quizService.getQuizQuestions(id);
    }

    //envia respostas para calcular resultado
    @PostMapping("submit/{id}")
    public ResponseEntity<Integer> submitQuiz(
            @PathVariable Integer id,
            @RequestBody List<Response> responses) {

        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().build();
        }

        if (responses == null || responses.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        //valida se todas as respostas tem id e resposta validos
        for (Response response : responses) {
            if (response.getId() == null || response.getResponse() == null ||
                    response.getResponse().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
        }
// retorna o resultado para cada porcentagem de acertos
return quizService.calculateResult(id, responses);
    }
}

