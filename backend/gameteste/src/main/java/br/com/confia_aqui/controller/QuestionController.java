
package br.com.confia_aqui.controller;

import br.com.confia_aqui.model.Question;
import br.com.confia_aqui.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("question")
@CrossOrigin(origins = "*") // Quando hospedarmos o projeto, mudar para a URL REAL PROD
public class QuestionController {

    @Autowired
    QuestionService questionService;

    // Busca todas as questões
    @GetMapping("allQuestions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    // FIX: Validação da categoria
    @GetMapping("category/{category}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable String category) {

        // Validação: categoria não pode ser null
        if (category == null || category.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return questionService.getQuestionsByCategory(category.trim());
    }

    // FIX: Validações completas da pergunta
    @PostMapping("add")
    public ResponseEntity<String> addQuestion(@RequestBody Question question) {

        // Validação: campos obrigatórios não podem ser null ou vazios
        if (question.getQuestionTitle() == null || question.getQuestionTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("O título da pergunta é obrigatório");
        }

        if (question.getOption1() == null || question.getOption1().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A opção 1 é obrigatória");
        }

        if (question.getOption2() == null || question.getOption2().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A opção 2 é obrigatória");
        }

        if (question.getOption3() == null || question.getOption3().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A opção 3 é obrigatória");
        }

        if (question.getOption4() == null || question.getOption4().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A opção 4 é obrigatória");
        }

        if (question.getRightAnswer() == null || question.getRightAnswer().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A resposta correta é obrigatória");
        }

        if (question.getCategory() == null || question.getCategory().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("A categoria é obrigatória");
        }

        if (question.getDifficultyLevel() == null || question.getDifficultyLevel().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("O nível de dificuldade é obrigatório");
        }

        // Validação: resposta correta deve ser uma das opções
        String rightAnswer = question.getRightAnswer().trim();
        if (!rightAnswer.equals(question.getOption1().trim()) &&
                !rightAnswer.equals(question.getOption2().trim()) &&
                !rightAnswer.equals(question.getOption3().trim()) &&
                !rightAnswer.equals(question.getOption4().trim())) {
            return ResponseEntity.badRequest()
                    .body("A resposta correta deve ser uma das 4 opções fornecidas");
        }

        return questionService.addQuestion(question);
    }
}
