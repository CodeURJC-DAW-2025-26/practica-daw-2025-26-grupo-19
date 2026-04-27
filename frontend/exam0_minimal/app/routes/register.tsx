import { useActionState, useEffect } from "react";
import { Container, Card, Form, Button, Alert } from "react-bootstrap";
import { useNavigate, Link } from "react-router";
import { registerTeam, uploadTeamImage } from "~/services/teams-service";
// Importamos tu store de Zustand
import { useUserStore } from "~/stores/user-store";

export default function Register() {
    const navigate = useNavigate();

    // Extraemos la función de login del store global
    const loginUser = useUserStore((state) => state.loginUser);

    async function performRegister(
        _prevState: { success: boolean; error: string | null } | null,
        formData: FormData
    ) {
        const username = formData.get("username") as string;
        const email = formData.get("email") as string;
        const teamName = formData.get("teamName") as string;
        const teamLogo = formData.get("teamLogo") as File;
        const password = formData.get("password") as string;
        const confirmPassword = formData.get("confirmPassword") as string;

        if (password !== confirmPassword) {
            return { success: false, error: "Las contraseñas no coinciden" };
        }

        try {
            // PASO 1: Registrar el usuario en la Base de Datos
            const newTeam = await registerTeam(username, email, password, teamName);
            
            // PASO 2: Si el usuario seleccionó un escudo válido, lo subimos
            if (teamLogo && teamLogo.size > 0 && newTeam && newTeam.id) {
                await uploadTeamImage(newTeam.id, teamLogo);
            }

            // PASO 3: Iniciar sesión automáticamente
            // Al usar tu store de Zustand, esto nos loguea y actualiza la barra de navegación (Header) de inmediato
            await loginUser(username, password);

            return { success: true, error: null };
        } catch (error) {
            console.error("Error en registro o login:", error);
            return {
                success: false,
                error: "Error al registrar el usuario o iniciar sesión. Comprueba si el nombre/email ya existe.",
            };
        }
    }

    const [state, formAction, isPending] = useActionState(performRegister, {
        success: false,
        error: null,
    });

    useEffect(() => {
        // Si todo ha ido bien (registro + imagen + login), vamos a la página principal
        if (state?.success) {
            navigate("/");
        }
    }, [state?.success, navigate]);

    return (
        <Container className="py-5" style={{ maxWidth: "600px" }}>
            <Card className="shadow-sm">
                <Card.Body className="p-4">
                    <h2 className="text-center mb-4">Registro de Usuario y Equipo</h2>

                    {state?.error && <Alert variant="danger">{state.error}</Alert>}

                    {/* Importante: encType="multipart/form-data" para poder enviar la imagen */}
                    <Form action={formAction} encType="multipart/form-data">
                        <Form.Group className="mb-3" controlId="formUsername">
                            <Form.Label>Nombre de Usuario</Form.Label>
                            <Form.Control
                                type="text"
                                name="username"
                                placeholder="Tu nombre de usuario"
                                required
                                disabled={isPending}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="formEmail">
                            <Form.Label>Correo Electrónico</Form.Label>
                            <Form.Control
                                type="email"
                                name="email"
                                placeholder="ejemplo@urjc.es"
                                required
                                disabled={isPending}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="formTeamName">
                            <Form.Label>Nombre de Equipo</Form.Label>
                            <Form.Control
                                type="text"
                                name="teamName"
                                placeholder="Nombre para tu equipo"
                                required
                                disabled={isPending}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="formTeamLogo">
                            <Form.Label>Escudo de Equipo</Form.Label>
                            <Form.Control
                                type="file"
                                name="teamLogo"
                                accept="image/*"
                                disabled={isPending}
                            />
                        </Form.Group>

                        <Form.Group className="mb-3" controlId="formPassword">
                            <Form.Label>Contraseña</Form.Label>
                            <Form.Control
                                type="password"
                                name="password"
                                placeholder="Contraseña"
                                required
                                disabled={isPending}
                            />
                        </Form.Group>

                        <Form.Group className="mb-4" controlId="formConfirmPassword">
                            <Form.Label>Confirmar Contraseña</Form.Label>
                            <Form.Control
                                type="password"
                                name="confirmPassword"
                                placeholder="Repite la contraseña"
                                required
                                disabled={isPending}
                            />
                        </Form.Group>

                        <Button
                            variant="success"
                            type="submit"
                            className="w-100 mb-3"
                            disabled={isPending}
                        >
                            {isPending ? "Procesando..." : "Registrarse"}
                        </Button>
                    </Form>

                    <div className="text-center mt-3">
                        <span className="text-muted">¿Ya tienes cuenta? </span>
                        <Link to="/login" className="text-decoration-none">
                            Inicia sesión aquí
                        </Link>
                    </div>
                </Card.Body>
            </Card>
        </Container>
    );
}