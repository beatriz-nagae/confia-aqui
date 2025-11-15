package br.com.confia_aqui.service;

import br.com.confia_aqui.dao.QuizDao;
import br.com.confia_aqui.dao.QuestionDao;
import br.com.confia_aqui.exception.InsufficientQuestionsException;
import br.com.confia_aqui.exception.QuizNotFoundException;
import br.com.confia_aqui.model.Question;
import br.com.confia_aqui.model.QuestionWrapper;
import br.com.confia_aqui.model.Quiz;
import br.com.confia_aqui.model.QuizResult;
import br.com.confia_aqui.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        List<Question> questions = questionDao.findRandomQuestionsByCategory(category, numQ);
// teste   List<Question> questions = new ArrayList<>(); // teste rapido caso de erro e quer saber se com a lista vazia funciona

        //envia mensagem de erro caso nao tenha perguntass suficientes
        if (questions.isEmpty()) {
            throw new InsufficientQuestionsException(category, numQ, 0);
        }

        if (questions.size() < numQ) {
            throw new InsufficientQuestionsException(category, numQ, questions.size());
        }

        //cria o quiz e salva no banco
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Quiz criado com sucesso! ID: " + quiz.getId(), HttpStatus.CREATED);
    }

    //busca o quiz e devolve ele logicamente sem a resposta certa e envia ao
    //usuario via ResponseEntity
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Optional<Quiz> quizOptional = quizDao.findById(id);

        if (quizOptional.isEmpty()) {
            throw new QuizNotFoundException(id);
        }

        //pega o quiz do optional
        Quiz quiz = quizOptional.get();
        //pega as perguntas do quiz
        List<Question> questionsFromDB = quiz.getQuestions();
        //cria uma lista vazia para o QWrapper
        List<QuestionWrapper> questionsForUser = new ArrayList<>();


        //converte as perguntas em QWrapper sem a resposta certa
        for (Question q : questionsFromDB) {
            QuestionWrapper qw = new QuestionWrapper(
                    q.getId(),
                    q.getQuestionTitle(),
                    q.getOption1(),
                    q.getOption2(),
                    q.getOption3(),
                    q.getOption4()
            );
            questionsForUser.add(qw);
        }

        return new ResponseEntity<>(questionsForUser, HttpStatus.OK);
    }
    }
