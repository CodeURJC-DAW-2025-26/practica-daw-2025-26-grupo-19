import { Link } from "react-router";
import { Container, Row, Col, Card, Badge } from "react-bootstrap";
import { AgCharts } from "ag-charts-react";
import type { Route } from "./+types/index";
import { getTournaments, getTopScorers, getTopAssisters } from "~/services/tournaments-service";
import { TournamentStatus, type TournamentDTO } from "~/dtos/TournamentDTO";
import { useUserStore } from "~/stores/user-store";

export async function clientLoader({}: Route.ClientLoaderArgs) {
    const [tournamentsPage, scorers, assisters] = await Promise.all([
        getTournaments(0, 3),
        getTopScorers(),
        getTopAssisters(),
    ]);

    return {
        tournaments: (tournamentsPage?.content ?? []) as TournamentDTO[],
        scorers: Array.isArray(scorers) ? scorers : [],
        assisters: Array.isArray(assisters) ? assisters : [],
    };
}



export default function Index({ loaderData }: Route.ComponentProps) {
    const { tournaments, scorers, assisters } = loaderData;
    let { user } = useUserStore();

    return (
        <>
            {/* HERO */}
            <div className="bg-dark text-white py-5 text-center">
                <Container>
                    <h1 className="display-4 fw-bold">⚽ FutbolManager</h1>
                    <p className="lead mt-3">
                        La plataforma definitiva para organizar y gestionar torneos de fútbol.
                        ¡Crea tu equipo, inscríbete y compite!
                    </p>
                    {user ? (
                    // Si el usuario existe (está logueado), mostramos el botón al Perfil
                    <Link to="/team/1" className="btn btn-primary btn-lg mt-3 me-2">
                        Ver mi perfil
                    </Link>
                ) : (
                    // Si NO existe (user es null o false), mostramos el de Registro
                    <Link to="/register" className="btn btn-success btn-lg mt-3 me-2">
                        Crea tu equipo gratis
                    </Link>
    )}
                    <Link to="/torneos" className="btn btn-outline-light btn-lg mt-3">
                        Ver torneos
                    </Link>
                </Container>
            </div>

            {/* TORNEOS DESTACADOS */}
            <Container className="my-5">
                <h2 className="text-center mb-4">Competiciones Destacadas</h2>
                {tournaments.length === 0 ? (
                    <p className="text-center text-muted">No hay torneos disponibles actualmente.</p>
                ) : (
                    <Row xs={1} md={3} className="g-4">
                        {tournaments.map((t) => (
                            <Col key={t.id}>
                                <Card className="h-100 shadow-sm">
                                    <Card.Body>
                                        <Card.Title>{t.name}</Card.Title>
                                        <Card.Text>
                                            <Badge bg={
                                                t.status === TournamentStatus.INSCRIPCIONES ? "success" :
                                                t.status === TournamentStatus.EN_CURSO ? "warning" : "dark"
                                            } className="me-2">
                                                {t.status}
                                            </Badge>
                                            <Badge bg="info">{t.type}</Badge>
                                        </Card.Text>
                                        <Link
                                            to={`/tournament/${t.id}`}
                                            className="btn btn-outline-primary btn-sm"
                                        >
                                            Ver detalles
                                        </Link>
                                    </Card.Body>
                                </Card>
                            </Col>
                        ))}
                    </Row>
                )}
            </Container>

            {/* ESTADÍSTICAS */}
            {(scorers.length > 0 || assisters.length > 0) && (
                <Container className="my-5">
                    <h2 className="text-center mb-4">Estadísticas Globales</h2>
                    <Row>
                        {scorers.length > 0 && (
                            <Col md={6}>
                                <div style={{ height: "380px", width: "100%" }}>
                                    <AgCharts
                                        options={{
                                            data: scorers,
                                            series: [
                                                {
                                                    type: "bar" as const,
                                                    xKey: "name",
                                                    yKey: "goals",
                                                    yName: "Goles",
                                                    fill: "#0d6efd",
                                                },
                                            ],
                                            title: { text: "🏆 Top Goleadores" },
                                        } as any}
                                    />
                                </div>
                            </Col>
                        )}
                        {assisters.length > 0 && (
                            <Col md={6}>
                                <div style={{ height: "380px", width: "100%" }}>
                                    <AgCharts
                                        options={{
                                            data: assisters,
                                            series: [
                                                {
                                                    type: "bar" as const,
                                                    xKey: "name",
                                                    yKey: "assists",
                                                    yName: "Asistencias",
                                                    fill: "#198754",
                                                },
                                            ],
                                            title: { text: "🎯 Top Asistentes" },
                                        } as any}
                                    />
                                </div>
                            </Col>
                        )}
                    </Row>
                </Container>
            )}
        </>
    );
}
