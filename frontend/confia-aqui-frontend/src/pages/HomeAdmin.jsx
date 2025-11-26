import { useState, useEffect } from "react";
import MessageBox from "../components/ui/MessageBox";
import { useGerenciarFaq } from "../hooks/gerenciarFaq";
import HeaderAdmin from "../components/ui/HeaderAdmin";

function HomeAdmin() {
  const { faqs, loading, error, fetchFaqs, addFaq, editFaq, removeFaq } = useGerenciarFaq();

  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({ pergunta: "", resposta: "" });

  const [mensagem, setMensagem] = useState("");
  const [tipoMensagem, setTipoMensagem] = useState("");

  useEffect(() => {
    const carregar = async () => {
      try {
        await fetchFaqs();
      } catch (err) {
        setMensagem("Erro ao conectar com o servidor. Tente novamente.");
        setTipoMensagem("erro");
      }
    };

    carregar();
  }, [fetchFaqs]);

  // Auto-hide mensagem após 5 segundos
  useEffect(() => {
    if (mensagem && !showModal) {
      const timer = setTimeout(() => {
        setMensagem("");
        setTipoMensagem("");
      }, 5000);
      return () => clearTimeout(timer);
    }
  }, [mensagem, showModal]);

  //  PROTEÇÃO CONTRA faqs não ser array
  const faqsArray = Array.isArray(faqs) ? faqs : [];

  const handleOpenModal = (faq = null) => {
    if (faq) {
      setEditingId(faq.id);
      setFormData({ pergunta: faq.pergunta, resposta: faq.resposta });
    } else {
      setEditingId(null);
      setFormData({ pergunta: "", resposta: "" });
    }

    setMensagem("");
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingId(null);
    setFormData({ pergunta: "", resposta: "" });
    setMensagem("");
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setMensagem("");

    const pergunta = formData.pergunta.trim();
    const resposta = formData.resposta.trim();

    if (!pergunta || !resposta) {
      setMensagem("Pergunta e resposta são obrigatórias.");
      setTipoMensagem("erro");
      return;
    }

    const existePergunta = faqsArray.some(
      (faq) =>
        faq.pergunta.toLowerCase() === pergunta.toLowerCase() &&
        faq.id !== editingId
    );

    if (existePergunta) {
      setMensagem("Já existe uma FAQ com essa pergunta.");
      setTipoMensagem("erro");
      return;
    }

    if (!editingId && faqsArray.length >= 6) {
      setMensagem("Você só pode criar no máximo 6 FAQs.");
      setTipoMensagem("erro");
      return;
    }

    try {
      if (editingId) {
        await editFaq(editingId, pergunta, resposta);
        setMensagem("FAQ atualizada com sucesso!");
        setTipoMensagem("sucesso");
      } else {
        await addFaq(pergunta, resposta);
        setMensagem("FAQ criada com sucesso!");
        setTipoMensagem("sucesso");
      }

      handleCloseModal();
    } catch (err) {
      setMensagem("Erro ao salvar FAQ.");
      setTipoMensagem("erro");
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm("Tem certeza que deseja deletar esta FAQ?")) {
      try {
        await removeFaq(id);
        setMensagem("FAQ deletada com sucesso!");
        setTipoMensagem("sucesso");
      } catch (err) {
        setMensagem("Erro ao deletar FAQ.");
        setTipoMensagem("erro");
      }
    }
  };

  return (
    <div className="flex flex-col w-screen h-screen">
      {/* Header Admin */}
      <HeaderAdmin />

      <main className="w-full justify-center items-center px-4 sm:px-6 lg:px-8 py-8 mt-16">
        {/* Cabeçalho da Página */}
        <div className="mb-8">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
            <div>
              <h1 className="text-3xl font-bold text-cinza-600">Gerenciar FAQs</h1>
              <p className="text-cinza-500 mt-1">Crie e gerencie perguntas frequentes</p>
            </div>
            <button
              onClick={() => handleOpenModal()}
              className="inline-flex items-center justify-center gap-2 bg-azul text-white px-6 py-3 rounded-lg hover:bg-azul/90 transition-all font-semibold shadow-lg shadow-azul/30 hover:shadow-xl hover:shadow-azul/40 hover:-translate-y-0.5"
            >
              + Nova FAQ
            </button>
          </div>
        </div>

        {/* Mensagem de Feedback */}
        {mensagem && !showModal && <MessageBox type={tipoMensagem} mensagem={mensagem} />}

        {/* Estado de Carregamento */}
        {loading && (
          <div className="flex flex-col items-center justify-center py-16">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-azul mb-4"></div>
            <p className="text-cinza-500">Carregando FAQs...</p>
          </div>
        )}

        {/* Estado Vazio */}
        {!loading && faqsArray.length === 0 && (
          <div className="flex flex-col items-center justify-center py-16 bg-white rounded-xl border-2 border-dashed border-gray-300">
            <h3 className="text-xl font-semibold text-cinza-600 mb-2">Nenhuma FAQ cadastrada</h3>
            <p className="text-cinza-500 mb-6">Comece criando sua primeira pergunta frequente</p>
            <button
              onClick={() => handleOpenModal()}
              className="inline-flex items-center gap-2 bg-azul text-white px-6 py-3 rounded-lg hover:bg-azul/90 transition-all font-semibold"
            >
              + Criar primeira FAQ
            </button>
          </div>
        )}

        {/* Lista de FAQs */}
        {!loading && faqsArray.length > 0 && (
          <div className="grid gap-4">
            {faqsArray.map((faq, index) => (
              <div
                key={faq.id}
                className="bg-white p-6 rounded-xl border border-gray-200 hover:shadow-lg hover:border-azul/30 transition-all duration-200 group"
                style={{
                  animation: `fadeIn 0.3s ease-out ${index * 0.05}s both`
                }}
              >
                <div className="flex gap-4">
                  <div className="flex-1 min-w-0">
                    <h3 className="text-lg font-bold text-azul mb-2 group-hover:text-azul/80 transition-colors">
                      {faq.pergunta}
                    </h3>
                    <p className="text-cinza-500 leading-relaxed">{faq.resposta}</p>
                  </div>
                  <div className="flex flex-col gap-2 flex-shrink-0">
                    <button
                      onClick={() => handleOpenModal(faq)}
                      className="flex items-center justify-center px-4 py-2 text-sm text-blue-600 hover:bg-blue-50 rounded-lg transition-colors border border-blue-200 hover:border-blue-300 font-semibold"
                    >
                      Editar
                    </button>
                    <button
                      onClick={() => handleDelete(faq.id)}
                      className="flex items-center justify-center px-4 py-2 text-sm text-red-600 hover:bg-red-50 rounded-lg transition-colors border border-red-200 hover:border-red-300 font-semibold"
                    >
                      Deletar
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </main>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-black/60 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full">
            {/* Modal Header */}
            <div className="flex items-center justify-between p-6 border-b border-gray-200">
              <h2 className="text-2xl font-bold text-cinza-600">
                {editingId ? "Editar FAQ" : "Nova FAQ"}
              </h2>
              <button
                onClick={handleCloseModal}
                className="p-2 hover:bg-gray-100 rounded-lg transition-colors text-xl"
              >
                ×
              </button>
            </div>

            {/* Modal Body */}
            <div className="p-6">
              {mensagem && (
                <div className="mb-4">
                  <MessageBox type={tipoMensagem} mensagem={mensagem} />
                </div>
              )}

              <div className="space-y-5">
                <div>
                  <label className="block text-sm font-semibold text-cinza-600 mb-2">
                    Pergunta <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    name="pergunta"
                    value={formData.pergunta}
                    onChange={handleInputChange}
                    placeholder="Digite a pergunta"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:border-azul focus:ring-4 focus:ring-azul/10 transition-all"
                  />
                </div>

                <div>
                  <label className="block text-sm font-semibold text-cinza-600 mb-2">
                    Resposta <span className="text-red-500">*</span>
                  </label>
                  <textarea
                    name="resposta"
                    value={formData.resposta}
                    onChange={handleInputChange}
                    placeholder="Digite a resposta"
                    rows="6"
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:outline-none focus:border-azul focus:ring-4 focus:ring-azul/10 transition-all resize-none"
                  />
                </div>
              </div>

              {/* Modal Footer */}
              <div className="flex justify-end gap-3 mt-6 pt-6 border-t border-gray-200">
                <button
                  type="button"
                  onClick={handleCloseModal}
                  className="px-6 py-2.5 border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors font-semibold text-cinza-600"
                >
                  Cancelar
                </button>
                <button
                  type="button"
                  onClick={handleSubmit}
                  className="px-6 py-2.5 bg-azul text-white rounded-lg hover:bg-azul/90 transition-all font-semibold shadow-lg shadow-azul/30"
                >
                  {editingId ? "Atualizar" : "Criar"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      <style>{`
        @keyframes fadeIn {
          from { opacity: 0; }
          to { opacity: 1; }
        }
      `}</style>
    </div>
  );
}

export default HomeAdmin;