import "bootstrap/dist/css/bootstrap.min.css";
import "./home.css";
import { Outlet, useNavigate, useNavigation } from "react-router";
import Header from "~/components/header";
import Footer from "~/components/footer";
import type { Route } from "./+types/home";
import Container from "react-bootstrap/esm/Container";
import Alert from "react-bootstrap/esm/Alert";
import Button from "react-bootstrap/esm/Button";

export default function Home() {
    const navigation = useNavigation();
    const isLoading = navigation.state === "loading";

    return (
        <>
            {isLoading && (
                <div className="page-spinner-overlay">
                    <div className="dot-spinner" />
                </div>
            )}

            <Header />
            <Outlet />
            <Footer />

        </>
    );
}

export function ErrorBoundary({ error }: Route.ErrorBoundaryProps) {
    const navigate = useNavigate();

    let errorMessage =
        error instanceof Error ? error.message : "An unexpected error occurred";

    return (
        <>
            <Header />
            <Container className="mt-4">
                <Alert variant="danger">
                    <Alert.Heading>Error</Alert.Heading>
                    <p>{errorMessage}</p>
                    <Button variant="outline-danger" onClick={() => navigate("/")}>
                        Volver al inicio
                    </Button>
                </Alert>
            </Container>
        </>
    );
}