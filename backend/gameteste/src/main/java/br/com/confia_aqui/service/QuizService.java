package br.com.confia_aqui.service;

import br.com.confia_aqui.dao.QuizDao;
import br.com.confia_aqui.dao.QuestionDao;
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
        if (questions.isEmpty()) {
            return new ResponseEntity<>("Nenhuma pergunta encontrada para a categoria: " + category, HttpStatus.NOT_FOUND);
        }

        if (questions.size() < numQ) {
            return new ResponseEntity<>("Apenas " + questions.size() + " perguntas disponíveis para esta categoria", HttpStatus.BAD_REQUEST);
        }

        // Cria e salva o quiz
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Quiz criado com sucesso! ID: " + quiz.getId(), HttpStatus.CREATED);
    }

    //HOTFIX CORREÇÃO BUG: Agora verifica se o quiz existe antes de usar .get()
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Optional<Quiz> quizOptional = quizDao.findById(id);

        // Verifica se o quiz existe
        if (quizOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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

        // Verifica se o quiz existe
        if (quizOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Quiz quiz = quizOptional.get();
        List<Question> questions = quiz.getQuestions();

        // Validação de segurança
        if (responses == null || responses.isEmpty()) {
            return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
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
