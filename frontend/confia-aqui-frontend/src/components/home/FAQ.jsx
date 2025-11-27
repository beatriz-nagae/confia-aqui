import { useState, useEffect } from "react"
import { faAngleDown, faAngleUp } from "@fortawesome/free-solid-svg-icons"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { useGerenciarFaq } from "../../hooks/gerenciarFaq"

function FAQ() {
    const [perguntaAtiva, setPerguntaAtiva] = useState(null)

    const { faqs, loading, error, fetchFaqs } = useGerenciarFaq()

    useEffect(() => {
        fetchFaqs()
    }, [])

    const abrirPergunta = (id) => {
        const idStr = String(id ?? "")
        setPerguntaAtiva((prev) => (prev === idStr ? null : idStr))
    }

    return (
        <section className="max-w-5xl mx-auto py-12 px-4 mb-4 text-left bg-white">
            <h2 className="text-lg font-bold text-cinza-600 mb-6 text-left">
                Dúvidas Frequentes (FAQ)
            </h2>

            {error && <p className="text-red-600 mb-4 text-left">Erro ao carregar FAQ.</p>}
            {loading && <p className="text-cinza-500 text-left">Carregando...</p>}
            {!loading && faqs.length === 0 && (
                <p className="text-cinza-500 text-left">Nenhuma FAQ cadastrada ainda.</p>
            )}

            <div className="grid md:grid-cols-2 gap-6">

                {!loading &&
                    faqs.map((faq, index) => (
                        <div key={`${faq.id ?? index}-${index}`}>
                            <div
                                className={`bg-white rounded-xl shadow-lg transition-all duration-300 flex flex-col justify-between min-h-20 ${
                                    perguntaAtiva === String(faq.id ?? index)
                                        ? "border border-azul shadow-xl"
                                        : "border border-gray-200"
                                }`}
                            >
                                <button
                                    onClick={() => abrirPergunta(String(faq.id ?? index))}
                                    className="w-full flex justify-between items-center p-3 font-bold text-lg text-azul text-left focus:outline-none"
                                >
                                    {faq.pergunta}

                                    <div className="bg-white shadow-md rounded-full p-2 flex items-center justify-center">
                                        <FontAwesomeIcon
                                            icon={perguntaAtiva === String(faq.id ?? index) ? faAngleUp : faAngleDown}
                                            className="text-azul transition-transform duration-300"
                                        />
                                    </div>
                                </button>

                                <div
                                    className={`overflow-hidden transition-all duration-300 ease-in-out ${
                                        perguntaAtiva === String(faq.id ?? index)
                                            ? "max-h-[500px] opacity-100 px-4 pb-4"
                                            : "max-h-0 opacity-0 p-0"
                                    }`}
                                >
                                    {/* Renderização da resposta */}
                                    {(() => {
                                        const r = faq.resposta || ""

                                        if (r.includes("•")) {
                                            const isStartingWithBullet = r.trim().startsWith("•")
                                            const parts = r
                                                .split("•")
                                                .map((p) => p.trim())
                                                .filter(Boolean)

                                            return (
                                                <div className="text-cinza-500 font-normal">
                                                    {!isStartingWithBullet && parts.length > 0 && (
                                                        <p className="mb-2">{parts[0]}</p>
                                                    )}

                                                    {parts.length > (isStartingWithBullet ? 0 : 1) && (
                                                        <ul className="list-disc pl-6">
                                                            {parts
                                                                .slice(isStartingWithBullet ? 0 : 1)
                                                                .map((item, i) => (
                                                                    <li key={i} className="mb-1">
                                                                        {item}
                                                                    </li>
                                                                ))}
                                                        </ul>
                                                    )}
                                                </div>
                                            )
                                        }

                                        if (r.includes("\n")) {
                                            const lines = r
                                                .split(/\r?\n/)
                                                .map((l) => l.trim())
                                                .filter(Boolean)

                                            if (lines.length === 1)
                                                return (
                                                    <p className="text-cinza-500 font-normal">{lines[0]}</p>
                                                )

                                            return (
                                                <div className="text-cinza-500 font-normal">
                                                    <p className="mb-2">{lines[0]}</p>
                                                    <ul className="list-disc pl-6">
                                                        {lines.slice(1).map((l, i) => (
                                                            <li key={i} className="mb-1">
                                                                {l}
                                                            </li>
                                                        ))}
                                                    </ul>
                                                </div>
                                            )
                                        }

                                        return (
                                            <p className="text-cinza-500 font-normal">
                                                {r}
                                            </p>
                                        )
                                    })()}
                                </div>
                            </div>
                        </div>
                    ))}
            </div>
        </section>
    )
}

export default FAQ