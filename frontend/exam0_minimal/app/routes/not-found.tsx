import { Container, Button, Alert } from "react-bootstrap";
import { useNavigate } from "react-router";

export default function NotFound() {
    const navigate = useNavigate();

    return (
        <Container className="mt-4">
            <Alert variant="danger">
                <Alert.Heading>Error 404</Alert.Heading>
                <p>Página no encontrada.</p>
                <Button variant="outline-danger" onClick={() => navigate("/")}>
                    Volver al inicio
                </Button>
            </Alert>
        </Container>
    );
}
