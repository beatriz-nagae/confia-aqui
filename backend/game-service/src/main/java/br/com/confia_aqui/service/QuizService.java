package br.com.confia_aqui.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.confia_aqui.dao.QuestionDao;
import br.com.confia_aqui.dao.QuizDao;
import br.com.confia_aqui.exception.InsufficientQuestionsException;
import br.com.confia_aqui.exception.QuestionNotFoundException;
import br.com.confia_aqui.exception.QuizNotFoundException;
import br.com.confia_aqui.model.Question;
import br.com.confia_aqui.model.QuestionWrapper;
import br.com.confia_aqui.model.Quiz;
import br.com.confia_aqui.model.QuizResult;
import br.com.confia_aqui.model.Response;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuestionDao questionDao;

    private static final Logger logger = LoggerFactory.getLogger(QuizService.class);

    //obtem as perguntas que vao compor o quiz
    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {
        List<Question> questions = questionDao.findRandomQuestionsByCategory(category, numQ);

        if (questions.isEmpty()) {
            throw new InsufficientQuestionsException(category, numQ, 0);
        }

        if (questions.size() < numQ) {
            throw new InsufficientQuestionsException(category, numQ, questions.size());
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Quiz criado com sucesso! ID: " + quiz.getId(), HttpStatus.CREATED);
    }

    //puxa um quiz existente pelo ID e retorna as perguntas (sem as respostas corretas)
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Optional<Quiz> quizOptional = quizDao.findById(id);

        if (quizOptional.isEmpty()) {
            throw new QuizNotFoundException(id);
        }

        Quiz quiz = quizOptional.get();
        List<Question> questionsFromDB = quiz.getQuestions();
        List<QuestionWrapper> questionsForUser = new ArrayList<>();

        //question wrapper (responsavel para nao enviar a resposta correta pro user)
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

    //calcula o resultado do quiz - MUDANCA REALIZADA AQUI (array tirado, 1st pergunta estava como indice 0)
    public ResponseEntity<QuizResult> calculateResult(Integer id, List<Response> responses) {
        Optional<Quiz> quizOptional = quizDao.findById(id);

        if (quizOptional.isEmpty()) {
            throw new QuizNotFoundException(id);
        }

        Quiz quiz = quizOptional.get();
        List<Question> questions = quiz.getQuestions();

        if (responses == null || responses.isEmpty()) {
            throw new IllegalArgumentException("Lista de respostas não pode ser vazia");
        }

        int correctAnswers = 0;

        // iterate as respostas e busca a pergunta correspondente pelo ID
        for (Response response : responses) {
            if (response.getId() == null || response.getResponse() == null) {
                continue;
            }

            // busca a pergunta pelo ID em vez de usar índice
            for (Question question : questions) {
                if (question.getId().equals(response.getId())) {
                    if (question.getRightAnswer() != null) {
                        String respTrim = response.getResponse().trim();
                        String rightTrim = question.getRightAnswer().trim();
                        if (respTrim.equalsIgnoreCase(rightTrim)) {
                            correctAnswers++;
                        }
                    }
                    break;
                }
            }
        }

        QuizResult result = new QuizResult(id, questions.size(), correctAnswers);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // verifica se ta certo ou errado
    public boolean checkAnswer(Integer quizId, Integer questionId, String answer) {
        logger.debug("[QuizService] checkAnswer: quizId={}, questionId={}, answer='{}'", quizId, questionId, (answer==null?"null":answer));
        Optional<Quiz> quizOptional = quizDao.findById(quizId);
        if (quizOptional.isEmpty()) {
            throw new QuizNotFoundException(quizId);
        }

        Quiz quiz = quizOptional.get();
        List<Question> questions = quiz.getQuestions();
        if (questions == null || questions.isEmpty()) {
            // erro quiz sem perguntas
            throw new QuestionNotFoundException("Question not found for quizId=" + quizId);
        }

        for (Question q : questions) {
            // protege contra NPEs
            if (Objects.equals(q.getId(), questionId)) {
                String right = q.getRightAnswer();
                if (right == null || answer == null) return false;
                return right.trim().equalsIgnoreCase(answer.trim());
            }
        }
        // question not found erro
        throw new QuestionNotFoundException("Question id " + questionId + " not found in quiz " + quizId);
    }

}
