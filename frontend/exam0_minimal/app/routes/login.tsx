import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router";
import { useUserStore } from "~/stores/user-store";
import { forgotPassword } from "~/services/email-service";

export default function Login() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [showForgot, setShowForgot] = useState(false);
  const [forgotEmail, setForgotEmail] = useState("");
  const [forgotLoading, setForgotLoading] = useState(false);
  const [forgotMessage, setForgotMessage] = useState<string | null>(null);
  const [forgotError, setForgotError] = useState<string | null>(null);

  const { loginUser, user, loginError } = useUserStore();

  useEffect(() => {
    if (user) navigate("/");
  }, [user, navigate]);

  const handleLoginSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await loginUser(username, password);
  };

  const handleForgotSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setForgotLoading(true);
    setForgotMessage(null);
    setForgotError(null);

    try {
      const result = await forgotPassword(forgotEmail);
      setForgotMessage(result.message);
      setForgotEmail("");
    } catch (err: any) {
      setForgotError(err.message ?? "Error al enviar el correo. Inténtalo de nuevo.");
    } finally {
      setForgotLoading(false);
    }
  };

  const handleToggleForgot = () => {
    setShowForgot((prev) => !prev);
    setForgotEmail("");
    setForgotMessage(null);
    setForgotError(null);
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

              {loginError && (
                <div className="alert alert-danger text-center" role="alert">
                  {loginError}
                </div>
              )}

              <form onSubmit={handleLoginSubmit}>
                <div className="mb-3">
                  <label htmlFor="username" className="form-label">
                    Correo electrónico / Usuario
                  </label>
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

                <div className="mb-2">
                  <label htmlFor="password" className="form-label">
                    Contraseña
                  </label>
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

                <div className="text-end mb-4">
                  <button
                    type="button"
                    className="btn btn-link btn-sm p-0 text-decoration-none"
                    onClick={handleToggleForgot}
                  >
                    ¿Olvidaste tu contraseña?
                  </button>
                </div>

                <button type="submit" className="btn btn-primary w-100">
                  Iniciar Sesión
                </button>
              </form>

              <div className="text-center mt-3">
                <p className="text-muted mb-0">
                  ¿No tienes cuenta?{" "}
                  <Link to="/register" className="text-primary text-decoration-none">
                    Regístrate
                  </Link>
                </p>
              </div>
            </div>
          </div>

          {showForgot && (
            <div className="card shadow mt-3">
              <div className="card-body p-4">
                <h2 className="h5 fw-bold mb-1">Restablecer contraseña</h2>
                <p className="text-muted small mb-3">
                  Escribe tu correo y te enviaremos un enlace para cambiar tu contraseña.
                </p>

                {forgotMessage && (
                  <div className="alert alert-success" role="alert">
                    {forgotMessage}
                  </div>
                )}

                {forgotError && (
                  <div className="alert alert-danger" role="alert">
                    {forgotError}
                  </div>
                )}

                <form onSubmit={handleForgotSubmit}>
                  <div className="mb-3">
                    <label htmlFor="forgotEmail" className="form-label">
                      Correo electrónico
                    </label>
                    <input
                      type="email"
                      className="form-control"
                      id="forgotEmail"
                      placeholder="tu@email.com"
                      value={forgotEmail}
                      onChange={(e) => setForgotEmail(e.target.value)}
                      required
                      disabled={forgotLoading}
                    />
                  </div>

                  <button
                    type="submit"
                    className="btn btn-outline-primary w-100"
                    disabled={forgotLoading}
                  >
                    {forgotLoading ? (
                      <>
                        <span
                          className="spinner-border spinner-border-sm me-2"
                          role="status"
                          aria-hidden="true"
                        />
                        Enviando…
                      </>
                    ) : (
                      "Enviar enlace de recuperación"
                    )}
                  </button>
                </form>
              </div>
            </div>
          )}

        </div>
      </div>
    </main>
  );
}