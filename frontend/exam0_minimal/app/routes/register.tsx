import { useActionState, useEffect } from "react";
import { Container, Card, Form, Button, Alert } from "react-bootstrap";
import { useNavigate, Link } from "react-router";
import { registerTeam } from "~/services/teams-service";

export default function Register() {
    const navigate = useNavigate();

    async function performRegister(
        _prevState: { success: boolean; error: string | null } | null,
        formData: FormData
    ) {
        const username = formData.get("username") as string;
        const email = formData.get("email") as string;
        const password = formData.get("password") as string;
        const confirmPassword = formData.get("confirmPassword") as string;

        if (password !== confirmPassword) {
            return { success: false, error: "Las contraseñas no coinciden" };
        }

        try {
            await registerTeam(username, email, password, username);
            return { success: true, error: null };
        } catch (error) {
            return {
                success: false,
                error: "Error al registrar el usuario. Comprueba si ya existe.",
            };
        }
    }

    const [state, formAction, isPending] = useActionState(performRegister, {
        success: false,
        error: null,
    });

    useEffect(() => {
        if (state?.success) {
            navigate("/");
        }
    }, [state?.success, navigate]);

    return (
        <Container className="py-5" style={{ maxWidth: "600px" }}>
            <Card className="shadow-sm">
                <Card.Body className="p-4">
                    <h2 className="text-center mb-4">Registro de Usuario</h2>

                    {state?.error && <Alert variant="danger">{state.error}</Alert>}

                    <Form action={formAction}>
                        <Form.Group className="mb-3" controlId="formUsername">
                            <Form.Label>Nombre de Usuario / Equipo</Form.Label>
                            <Form.Control
                                type="text"
                                name="username"
                                placeholder="Nombre del usuario o equipo"
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
                            {isPending ? "Registrando..." : "Registrarse"}
                        </Button>
                    </Form>

                    <div className="text-center mt-3">
                        <span className="text-muted">¿Ya tienes cuenta? </span>
                        <span className="text-muted">Usa el formulario de inicio de sesión en la barra superior.</span>
                    </div>
                </Card.Body>
            </Card>
        </Container>
    );
}

