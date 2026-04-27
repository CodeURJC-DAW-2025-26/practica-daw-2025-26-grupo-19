import { useState } from "react";
import { Link } from "react-router";
import { Container, Row, Col, Card, Badge, Button } from "react-bootstrap";
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

    const initialTournaments = (tournamentsPage?.content ?? []) as TournamentDTO[];
    // Verificamos si es la última página (por defecto si vienen menos de 3 asumimos que no hay más)
    const isLastPage = tournamentsPage?.last ?? initialTournaments.length < 3;

    return {
        initialTournaments,
        isLastPage,
        scorers: Array.isArray(scorers) ? scorers : [],
        assisters: Array.isArray(assisters) ? assisters : [],
    };
}

export default function Index({ loaderData }: Route.ComponentProps) {
    const { initialTournaments, isLastPage, scorers, assisters } = loaderData;
    let { user } = useUserStore();

    // Estados para controlar la paginación de torneos
    const [tournaments, setTournaments] = useState<TournamentDTO[]>(initialTournaments);
    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(!isLastPage);
    const [isLoadingMore, setIsLoadingMore] = useState(false);

    // Función para cargar los siguientes 3 torneos
    const handleLoadMore = async () => {
        setIsLoadingMore(true);
        try {
            const nextPage = page + 1;
            const newPageData = await getTournaments(nextPage, 3);
            const newTournaments = (newPageData?.content ?? []) as TournamentDTO[];
            
            // Añadimos los nuevos a la lista existente
            setTournaments((prev) => [...prev, ...newTournaments]);
            setPage(nextPage);
            
            // Si el backend marca que es la última (last) o han llegado menos de 3, no hay más que cargar
            const isLast = newPageData?.last ?? newTournaments.length < 3;
            setHasMore(!isLast);
        } catch (error) {
            console.error("Error al cargar más torneos:", error);
        } finally {
            setIsLoadingMore(false);
        }
    };

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
                        <Link to={`/team/${user.id}`} className="btn btn-success btn-lg mt-3 me-2">
                            Ver mi perfil
                        </Link>
                    ) : (
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
                    <>
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
                        
                        {/* Botón de Cargar Más */}
                        {hasMore && (
                            <div className="text-center mt-4">
                                <Button 
                                    variant="outline-primary" 
                                    onClick={handleLoadMore} 
                                    disabled={isLoadingMore}
                                >
                                    {isLoadingMore ? "Cargando..." : "Mostrar más"}
                                </Button>
                            </div>
                        )}
                    </>
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