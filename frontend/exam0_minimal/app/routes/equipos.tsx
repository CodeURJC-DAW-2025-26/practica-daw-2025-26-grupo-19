import { useState } from "react";
import { Container, Card, Button, Row, Col, Spinner, Badge, Alert } from "react-bootstrap";
import { Link } from "react-router";
import type { Route } from "./+types/equipos";
import { getTeams } from "~/services/teams-service";
import type { TeamDTO } from "~/dtos/TeamDTO";

export async function clientLoader({}: Route.ClientLoaderArgs) {
    return await getTeams(0, 9);
}

export default function EquiposList({ loaderData }: Route.ComponentProps) {
    const initialData = loaderData;
    const [teams, setTeams] = useState<TeamDTO[]>(initialData.content ?? []);
    const [page, setPage] = useState(0);
    const [isLast, setIsLast] = useState<boolean>(initialData.last ?? true);
    const [isLoadingMore, setIsLoadingMore] = useState(false);

    async function loadMore() {
        setIsLoadingMore(true);
        try {
            const nextPage = page + 1;
            const data = await getTeams(nextPage, 9);
            setTeams((prev) => [...prev, ...(data.content ?? [])]);
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
                <h2>Listado de Equipos</h2>
            </div>

            {teams.length === 0 ? (
                <p className="text-muted mt-3">Actualmente no hay equipos registrados.</p>
            ) : (
                <Row xs={1} md={2} lg={3} className="g-4">
                    {teams.map((team) => (
                        <Col key={team.id}>
                            <Card className="h-100 shadow-sm border-0">
                                <Card.Body>
                                    <Card.Title className="fw-bold">
                                        {team.teamName || team.username}
                                    </Card.Title>
                                    <Card.Text className="text-muted">
                                        Mánager: {team.username}
                                    </Card.Text>
                                    <Link
                                        to={`/team/${team.id}`}
                                        className="btn btn-primary w-100 mt-2"
                                    >
                                        Ver Plantilla y Detalles
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
                            "Cargar más equipos"
                        )}
                    </Button>
                </div>
            )}
        </Container>
    );
}
