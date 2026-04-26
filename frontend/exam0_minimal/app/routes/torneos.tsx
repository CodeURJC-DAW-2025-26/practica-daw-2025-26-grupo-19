import { useState } from "react";
import { Container, Card, Button, Row, Col, Spinner, Badge, Alert } from "react-bootstrap";
import { Link } from "react-router";
import type { Route } from "./+types/torneos";
import { getTournaments } from "~/services/tournaments-service";
import type { TournamentDTO } from "~/dtos/TournamentDTO";

export async function clientLoader({}: Route.ClientLoaderArgs) {
    return await getTournaments(0, 10);
}

export default function TorneosList({ loaderData }: Route.ComponentProps) {
    const initialData = loaderData;
    const [tournaments, setTournaments] = useState<TournamentDTO[]>(initialData.content ?? []);
    const [page, setPage] = useState(0);
    const [isLast, setIsLast] = useState<boolean>(initialData.last ?? true);
    const [isLoadingMore, setIsLoadingMore] = useState(false);

    async function loadMore() {
        setIsLoadingMore(true);
        try {
            const nextPage = page + 1;
            const data = await getTournaments(nextPage, 10);
            setTournaments((prev) => [...prev, ...(data.content ?? [])]);
            setPage(nextPage);
            setIsLast(data.last);
        } catch (err) {
            console.error(err);
        } finally {
            setIsLoadingMore(false);
        }
    }

    return (
        <Container className="py-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h2>Listado de Competiciones</h2>
            </div>

            {tournaments.length === 0 ? (
                <Alert variant="info">Actualmente no hay torneos disponibles.</Alert>
            ) : (
                <Row xs={1} md={2} lg={3} className="g-4">
                    {tournaments.map((t) => (
                        <Col key={t.id}>
                            <Card className="h-100 shadow-sm border-0">
                                <Card.Body>
                                    <Card.Title className="fw-bold">{t.name}</Card.Title>
                                    <Card.Text>
                                        <Badge
                                            bg={
                                                (t.state || t.status) === "ABIERTO" ? "success" :
                                                (t.state || t.status) === "EN_CURSO" ? "warning" : "secondary"
                                            }
                                            className="me-2"
                                        >
                                            {t.state || t.status}
                                        </Badge>
                                        <Badge bg="info">{t.type}</Badge>
                                    </Card.Text>
                                    <Link
                                        to={`/tournament/${t.id}`}
                                        className="btn btn-outline-primary w-100 mt-2"
                                    >
                                        Ver Detalles y Clasificación
                                    </Link>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            )}

            {!isLast && (
                <div className="text-center mt-4">
                    <Button
                        variant="dark"
                        onClick={loadMore}
                        disabled={isLoadingMore}
                        size="lg"
                        className="px-5"
                    >
                        {isLoadingMore ? (
                            <>
                                <Spinner as="span" animation="border" size="sm" role="status" aria-hidden="true" />{" "}
                                Cargando...
                            </>
                        ) : (
                            "Más resultados"
                        )}
                    </Button>
                </div>
            )}
        </Container>
    );
}
