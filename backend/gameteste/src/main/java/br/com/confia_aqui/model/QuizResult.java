package br.com.confia_aqui.model;

import lombok.Data;

@Data
public class QuizResult {
    private Integer quizId;
    private Integer totalQuestions;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Double scorePercentage;
    private String performanceLevel; // "BAIXO", "MEDIO", "ALTO"
    private String feedbackMessage;
    private String recommendation;


    //construtor do QuizREsult
    public QuizResult(Integer quizId, Integer totalQuestions, Integer correctAnswers) {
        this.quizId = quizId;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = totalQuestions - correctAnswers;
        this.scorePercentage = calculatePercentage(correctAnswers, totalQuestions);
        this.performanceLevel = determinePerformanceLevel(this.scorePercentage);
        this.feedbackMessage = generateFeedbackMessage(this.performanceLevel);
        this.recommendation = generateRecommendation(this.performanceLevel);
    }

    private Double calculatePercentage(Integer correct, Integer total) {
        if (total == 0) return 0.0;
        return Math.round((correct * 100.0 / total) * 100.0) / 100.0;
    }

    private String determinePerformanceLevel(Double percentage) {
        if (percentage < 50) {
            return "BAIXO";
        } else if (percentage < 75) {
            return "MEDIO";
        } else {
            return "ALTO";
        }
    }

    private String generateFeedbackMessage(String level) {
        switch (level) {
            case "BAIXO":
                return "ATENÇÃO: Você está propenso a cair em no Golpe Do Presente!";
            case "MEDIO":
                return "ALERTA: Preste atenção! Você precisa reforçar seus conhecimentos para evitar cair em Golpes.";
            case "ALTO":
                return "PARABÉNS: Você está preparado contra o golpe do presente. Se mantenha assim!";
            default:
                return "Resultado processado.";
        }
    }

    private String generateRecommendation(String level) {
        switch (level) {
            case "BAIXO":
                return "Recomendamos que você revise o conteúdo sobre golpes online, " +
                        "especialmente sobre o golpe do presente. Fique atento a ofertas " +
                        "suspeitas e sempre verifique a autenticidade dos sites antes de " +
                        "fornecer dados pessoais ou realizar pagamentos.";
            case "MEDIO":
                return "Você tem conhecimento básico, mas ainda há pontos importantes a " +
                        "melhorar. Preste mais atenção aos sinais de alerta como ofertas " +
                        "suspeitas, pressão para decisões rápidas e pedidos de dados " +
                        "sensíveis. Refaça o quiz para reforçar seu aprendizado.";
            case "ALTO":
                return "Excelente trabalho! Você demonstra bom conhecimento sobre como " +
                        "identificar e evitar golpes. Continue atualizado sobre novas " +
                        "táticas de golpistas e compartilhe esse conhecimento com amigos " +
                        "e familiares para protegê-los também.";
            default:
                return "Continue estudando sobre segurança digital.";
        }
    }
}
