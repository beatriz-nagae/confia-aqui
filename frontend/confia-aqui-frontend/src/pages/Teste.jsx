import Header from "../components/ui/Header"
import CardPerguntas from "../components/teste/CardPerguntas";
import ResultadoTeste from "../components/teste/ResultadoTeste";
import IniciarTeste from "../components/teste/IniciarTeste";
import { useGerenciarTeste } from "../hooks/gerenciarTeste";
import Footer from "../components/ui/Footer";

function Teste() {
    const {
        iniciado,
        iniciar,
        perguntas,
        indexAtual,
        animacao,
        responder,
        progresso,
        finalizado,
        resultado,
        carregando
        , selectedOptionId, correctOptionId, wrongOptionId
    } = useGerenciarTeste();

    return (
        <>
            <Header />

            {!iniciado && !finalizado && (
                <IniciarTeste onIniciar={iniciar} carregando={carregando} />
            )}

            {finalizado && (
                <ResultadoTeste resultado={resultado} />
            )}

            {iniciado && !finalizado && perguntas.length > 0 && (
                <div className="pt-20 px-4 w-screen h-screen flex justify-center items-center">
                    <div className="w-full max-w-xl bg-white p-8 rounded-2xl shadow-xl">

                        <div className="w-full bg-gray-200 rounded-full h-3 mb-6 overflow-hidden">
                            <div
                                className="bg-azul h-3 rounded-full transition-all duration-300"
                                style={{ width: `${progresso}%` }}
                            ></div>
                        </div>

                        <CardPerguntas
                            pergunta={perguntas[indexAtual]}
                            onResponder={responder}
                            animacao={animacao}
                            selectedOptionId={selectedOptionId}
                            correctOptionId={correctOptionId}
                            wrongOptionId={wrongOptionId}
                        />
                    </div>
                </div>
            )}
            <Footer />
        </>
    );
}

export default Teste;