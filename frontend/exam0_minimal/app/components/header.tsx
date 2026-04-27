import Container from "react-bootstrap/Container";
import Button from "react-bootstrap/Button";
import { Form, Nav, Navbar, Modal } from "react-bootstrap";
import { useActionState, useEffect, useState } from "react";
import { useUserStore } from "~/stores/user-store";
import { Link } from "react-router";

export default function Header() {
    const [isErrorLoginDialogOpen, setErrorLoginDialogOpen] = useState(false);

    function handleShowErrorLoginDialog() {
        setErrorLoginDialogOpen(true);
    }

    function handleCloseErrorLoginDialog() {
        setErrorLoginDialogOpen(false);
    }

    let { user, loginError, loadLoggedUser, loginUser, logoutUser } = useUserStore();

    async function loginUserAction(_prevState: void | null, formData: FormData) {
        const username = formData.get("username") as string;
        const password = formData.get("password") as string;

        await loginUser(username, password);

        const error = useUserStore.getState().loginError;

        if (error) {
            handleShowErrorLoginDialog();
        }
    }

    const [, loginFormAction, isPending] = useActionState(loginUserAction, null);

    async function logoutUserAction() {
        await logoutUser();
    }

    const [, logoutFormAction, isLoggingOut] = useActionState(logoutUserAction, null);

    useEffect(() => {
        loadLoggedUser();
    }, [loadLoggedUser]);

    return (
        <>
            <Navbar expand="lg" bg="dark" data-bs-theme="dark" className="px-3">
                <Container fluid>
                    <Navbar.Brand as={Link} to="/">⚽ FutbolManager</Navbar.Brand>
                    <Navbar.Toggle aria-controls="navbarContent" />
                    <Navbar.Collapse id="navbarContent">
                        <Nav className="me-auto">
                            <Nav.Link as={Link} to="/">Inicio</Nav.Link>
                            <Nav.Link as={Link} to="/torneos">Torneos</Nav.Link>
                            {user && (user.roles?.includes("ADMIN") || user.username === "admin") && (
                                <Nav.Link as={Link} to="/admin">Panel Gestión</Nav.Link>
                            )}
                        </Nav>

                        {/* Mantenemos la estructura flexible para que no se rompa el diseño */}
                        <div className="d-flex flex-column flex-lg-row align-items-lg-center gap-2 mt-3 mt-lg-0">
                            {!user ? (
                                <>
                                    {/* Uso directo de Link con las clases de Bootstrap */}
                                    <Link to="/login" className="btn btn-success text-nowrap">
                                        Iniciar Sesión
                                    </Link>    
                                    <Link to="/register" className="btn btn-outline-light text-nowrap">
                                        Registrarse
                                    </Link>
                                </>
                            ) : (
                                <>
                                    <Navbar.Text className="text-white me-lg-2">
                                        {user.teamName || user.username || user.name}
                                    </Navbar.Text>
                                    <Form action={logoutFormAction} className="m-0">
                                        <Button
                                            variant="outline-light"
                                            type="submit"
                                            disabled={isLoggingOut}
                                            className="w-100"
                                        >
                                            {isLoggingOut ? "Saliendo..." : "Cerrar Sesión"}
                                        </Button>
                                    </Form>
                                </>
                            )}
                        </div>
                    </Navbar.Collapse>
                </Container>
            </Navbar>

            <Modal show={isErrorLoginDialogOpen} onHide={handleCloseErrorLoginDialog}>
                <Modal.Header className="bg-danger text-white" closeButton>
                    <Modal.Title>Error de inicio de sesión</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>{loginError}</p>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleCloseErrorLoginDialog}>
                        Cerrar
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}