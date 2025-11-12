package br.com.confia_aqui.service;

import br.com.confia_aqui.dao.QuizDao;
import br.com.confia_aqui.dao.QuestionDao;
import br.com.confia_aqui.exception.InsufficientQuestionsException;
import br.com.confia_aqui.exception.QuizNotFoundException;
import br.com.confia_aqui.model.Question;
import br.com.confia_aqui.model.QuestionWrapper;
import br.com.confia_aqui.model.Quiz;
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
        // Busca perguntas aleatórias da categoria
        List<Question> questions = questionDao.findRandomQuestionsByCategory(category, numQ);
        
        // Verifica se encontrou perguntas suficientes
        // E Lança exceção se não houver perguntas suficientes
        if (questions.isEmpty()) {
            throw new InsufficientQuestionsException(category, numQ, 0);
        }

        if (questions.size() < numQ) {
            throw new InsufficientQuestionsException(category, numQ, questions.size());
        }
        
        // Cria e salva o quiz
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Quiz criado com sucesso! ID: " + quiz.getId(), HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Optional<Quiz> quizOptional = quizDao.findById(id);


        // Verifica se o quiz existe E
        // Lança exceção se quiz não for encontrado
        if (quizOptional.isEmpty()) {
            throw new QuizNotFoundException(id);
        }

        Quiz quiz = quizOptional.get();
        List<Question> questionsFromDB = quiz.getQuestions();
        List<QuestionWrapper> questionsForUser = new ArrayList<>();

        // Converte Question para QuestionWrapper (sem mostrar a resposta correta)
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


    //HOTFIX CORREÇÃO BUG: Verifica se quiz existe e tb protege contra IndexOutOfBoundsException
    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        Optional<Quiz> quizOptional = quizDao.findById(id);


        // Verifica se o quiz existe E
        // Lança exceção se quiz não for encontrado
        if (quizOptional.isEmpty()) {
            throw new QuizNotFoundException(id);
        }

        Quiz quiz = quizOptional.get();
        List<Question> questions = quiz.getQuestions();

        // Validação de segurança contra null e vazio
        if (responses == null || responses.isEmpty()) {
            throw new IllegalArgumentException("Lista de respostas não pode ser vazia");
        }

        int right = 0;
        //HOTFIX CORREÇÃO BUG: Usa o menor tamanho para evitar IndexOutOfBoundsException
        int maxIndex = Math.min(responses.size(), questions.size());

        for (int i = 0; i < maxIndex; i++) {
            Response response = responses.get(i);
            Question question = questions.get(i);
            
            // Verifica se a resposta está correta
            if (response.getResponse() != null &&
                    response.getResponse().equals(question.getRightAnswer())) {
                right++;
            }
        }

        return new ResponseEntity<>(right, HttpStatus.OK);
    }
}
