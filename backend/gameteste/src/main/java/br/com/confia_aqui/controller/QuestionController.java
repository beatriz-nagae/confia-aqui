package br.com.confia_aqui.controller;

import br.com.confia_aqui.model.Question;
import br.com.confia_aqui.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("question")
@CrossOrigin(origins = "*") //substituir por URL real se hospedado
public class QuestionController {

    @Autowired
    QuestionService questionService;

    //busca perguntas
    @GetMapping("allQuestions")
    public ResponseEntity<List<Question>> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    //busca perguntas de uma categoria especificada
    @GetMapping("category/{category}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable String category) {
        if (category == null || category.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        return questionService.getQuestionsByCategory(category.trim());
    }

    //busca as categorias em si
    @GetMapping("categories")
    public ResponseEntity<List<String>> getAllCategories() {
        return questionService.getAllCategories();
    }
}