import { useState } from "react"
import { Link } from "react-router-dom"
import imagemLogo from "../../images/imagemLogoTransparente.png"
import { useAuth } from "../../components/auth/AuthContext"
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome"
import { faUser } from "@fortawesome/free-regular-svg-icons"
import { faBars, faTimes, faArrowRightFromBracket } from "@fortawesome/free-solid-svg-icons"

function HeaderAdmin() {
  const [menuAberto, setMenuAberto] = useState(false)
  const [dropDownAberto, setDropDownAberto] = useState(false)
  const {logout} = useAuth()

  return (
    <header className="fixed top-0 left-0 w-full flex items-center justify-between px-4 py-3 bg-white shadow-md z-50">
      <button
        className="md:hidden text-azul flex items-center justify-center w-10 h-10 rounded-lg hover:bg-gray-100 transition"
        onClick={() => setMenuAberto(!menuAberto)}
        >
        <FontAwesomeIcon
          icon={menuAberto ? faTimes : faBars}
          size="lg"
          className="transition-all duration-200"
        />
      </button>

      <Link 
        to="/home" 
        className="flex items-center gap-2 text-2xl font-bold text-azul hover:opacity-95 absolute left-1/2 transform -translate-x-1/2 md:static md:transform-none">
        <img src={imagemLogo} alt="Imagem logo do Confia Aqui" className="w-8 h-8 object-contain" />
        Confia Aqui
      </Link>

      <div className="relative flex items-center justify-center w-10 h-10 cursor-pointer" onClick={() => setDropDownAberto(!dropDownAberto)}>
        <FontAwesomeIcon
          icon={faArrowRightFromBracket}
          className="text-azul text-lg hover:bg-gray-300 hover:rounded-full p-1 hover:opacity-80"/>

          {dropDownAberto && (
            <div className="absolute right-0 top-12 w-48 bg-white border border-gray-200 rounded-lg shadow-lg text-sm animate-fadeIn z-50">
                <ul className="py-2 text-red-700 font-semibold">
                  <li className="block w-full px-4 py-2 hover:bg-red-200 rounded-md cursor-pointer" onClick={logout}><span>Sair</span>
                    <FontAwesomeIcon icon={faArrowRightFromBracket} className="px-1"/>
                  </li>
                </ul>
              </div>
          )}
      </div>

      <nav className="hidden md:flex gap-6 absolute right-20">
        <Link to="/admin/home" className="font-bold text-azul hover:opacity-95">Dashboard</Link>
      </nav>

      {menuAberto && (
        <div className="absolute top-full left-0 w-full bg-white border-t border-gray-200 shadow-md md:hidden animate-slideDown">
          <nav className="flex flex-col px-6 py-4 space-y-3 text-azul">
            <Link
              to="/admin/home"
              className="font-medium hover:opacity-95 transition"
              onClick={() => setMenuAberto(false)}
            >
              Dashboard
            </Link>
          </nav>
        </div>
      )}
    </header>
  )
}

export default HeaderAdmin