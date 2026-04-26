import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router";
import { useUserStore } from "~/stores/user-store";

export default function Login() {
  const navigate = useNavigate();
  
  // Estado local para los campos del formulario
  // Nota: Lo llamamos username porque tu backend y servicio esperan un "username", aunque el usuario introduzca un correo
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  // Extraemos el estado y las acciones del store
  const { loginUser, user, loginError } = useUserStore();

  // Si el usuario ya está logueado, lo redirigimos a la página de inicio
  useEffect(() => {
    if (user) {
      navigate("/");
    }
  }, [user, navigate]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    // Llamamos a la función del store que hace la petición a la API
    await loginUser(username, password);
  };

  return (
    <main className="container my-5">
      <div className="row justify-content-center">
        <div className="col-md-5">
          <div className="card shadow">
            <div className="card-body p-4">
              
              <div className="text-center mb-4">
                <div className="card-icon-custom card-icon-lg d-inline-flex align-items-center justify-content-center mb-3">
                  <span>🔑</span>
                </div>
                <h1 className="h2 fw-bold">Iniciar Sesión</h1>
                <p className="text-muted">Accede a tu cuenta</p>
              </div>

              {/* Muestra un mensaje de error si las credenciales fallan */}
              {loginError && (
                <div className="alert alert-danger text-center" role="alert">
                  {loginError}
                </div>
              )}

              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label htmlFor="username" className="form-label">Correo electrónico / Usuario</label>
                  <input 
                    type="text" 
                    className="form-control" 
                    id="username" 
                    placeholder="tu@email.com o usuario"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                  />
                </div>

                <div className="mb-4">
                  <label htmlFor="password" className="form-label">Contraseña</label>
                  <input 
                    type="password" 
                    className="form-control" 
                    id="password" 
                    placeholder="Tu contraseña"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                  />
                </div>

                <button type="submit" className="btn btn-primary w-100">
                  Iniciar Sesión
                </button>
              </form>

              <div className="text-center mt-3">
                <p className="text-muted mb-0">
                  ¿No tienes cuenta? <Link to="/register" className="text-primary text-decoration-none">Regístrate</Link>
                </p>
              </div>
              
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}