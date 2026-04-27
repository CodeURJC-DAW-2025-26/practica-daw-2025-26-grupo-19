import React, { useState } from "react";
import {
    Container, Card, ListGroup, Button, Table, Spinner, Alert, Badge,
} from "react-bootstrap";
import { useNavigate } from "react-router";
import type { Route } from "./+types/torneo-detail";
import { getTournament, generateSchedule, simulateMatch, enrollTeam } from "~/services/tournaments-service";
import { useUserStore } from "~/stores/user-store";
import { type TournamentDTO, type MatchDTO, TournamentStatus } from "~/dtos/TournamentDTO";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    return await getTournament(params.id!);
}

export default function TorneoDetail({ loaderData }: Route.ComponentProps) {
    const navigate = useNavigate();
    const { user } = useUserStore();

    const [tournament, setTournament] = useState<TournamentDTO>(loaderData);
    const [loadingSchedule, setLoadingSchedule] = useState(false);
    const [msg, setMsg] = useState<string | null>(null);

    const isAdmin = user && (user.roles?.includes("ADMIN") || user.username === "admin");
    const isUserEnrolled = user && tournament.teams?.some(t => t.username === user.username);

    const handleGenerateSchedule = async () => {
        setLoadingSchedule(true);
        try {
            const res = await generateSchedule(tournament.id);
            setTournament(res);
            setMsg("Calendario generado y partidos programados automáticamente.");
        } catch (e) {
            setMsg("Error al generar el calendario. Comprueba que el torneo tiene equipos.");
        } finally {
            setLoadingSchedule(false);
        }
    };

    const handleSimulateMatch = async (matchId: number) => {
        try {
            const res = await simulateMatch(matchId);
            const updatedMatches = tournament.matches.map((m: MatchDTO) =>
                m.id === matchId ? res : m
            );
            setTournament({ ...tournament, matches: updatedMatches });
            setMsg("¡Partido jugado! Resultados actualizados.");
        } catch (e) {
            setMsg("Fallo calculando el resultado de este partido.");
        }
    };

    const handleEnroll = async () => {
        try {
            const res = await enrollTeam(tournament.id);
            setTournament(res);
            setMsg("¡Te has inscrito en el torneo correctamente!");
        } catch (e) {
            setMsg("Error al inscribirte. Es posible que el torneo esté lleno o ya estés inscrito.");
        }
    };

    // Clasificación calculada en el cliente
    const standings = React.useMemo(() => {
        if (!tournament?.teams) return [];

        const map: Record<number, any> = {};
        tournament.teams.forEach((t: any) => {
            map[t.id] = { team: t, pts: 0, pJ: 0, pG: 0, pE: 0, pP: 0, gf: 0, gc: 0 };
        });

        if (tournament.matches) {
            tournament.matches.forEach((m: MatchDTO) => {
                if (m.played) {
                    const hid = m.homeTeam?.id;
                    const aid = m.awayTeam?.id;

                    if (hid && map[hid]) {
                        map[hid].pJ++;
                        map[hid].gf += m.homeGoals;
                        map[hid].gc += m.awayGoals;
                        if (m.homeGoals > m.awayGoals) { map[hid].pG++; map[hid].pts += 3; }
                        else if (m.homeGoals === m.awayGoals) { map[hid].pE++; map[hid].pts += 1; }
                        else { map[hid].pP++; }
                    }
                    if (aid && map[aid]) {
                        map[aid].pJ++;
                        map[aid].gf += m.awayGoals;
                        map[aid].gc += m.homeGoals;
                        if (m.awayGoals > m.homeGoals) { map[aid].pG++; map[aid].pts += 3; }
                        else if (m.awayGoals === m.homeGoals) { map[aid].pE++; map[aid].pts += 1; }
                        else { map[aid].pP++; }
                    }
                }
            });
        }

        return Object.values(map).sort((a: any, b: any) => {
            if (b.pts !== a.pts) return b.pts - a.pts;
            return (b.gf - b.gc) - (a.gf - a.gc);
        });
    }, [tournament]);

    const tournamentState = tournament.status;

    return (
        <Container className="py-5">
            <div className="mb-4 d-flex align-items-center justify-content-between">
                <div className="d-flex align-items-center gap-3">
                    <Button variant="outline-secondary" onClick={() => navigate("/torneos")}>
                        &larr; Volver
                    </Button>
                    <h1 className="mb-0 d-flex align-items-center gap-3">
                        {tournament.hasImage && (
                            <img
                                src={`/api/v1/images/tournament/${tournament.id}/image`}
                                alt="Logo Torneo"
                                width="60"
                                height="60"
                                className="rounded border shadow-sm"
                            />
                        )}
                        {tournament.name}
                    </h1>
                </div>
                {user && tournamentState === "INSCRIPCIONES_ABIERTAS" && (
                    !isUserEnrolled ? (
                        <Button variant="success" size="lg" onClick={handleEnroll}>
                            Inscribir Mi Equipo
                        </Button>
                    ) : (
                        <Button variant="secondary" size="lg" disabled>
                            Ya estás inscrito
                        </Button>
                    )
                )}
            </div>

            {msg && (
                <Alert
                    variant={msg.startsWith("Error") || msg.startsWith("Fallo") ? "danger" : "success"}
                    dismissible
                    onClose={() => setMsg(null)}
                >
                    {msg}
                </Alert>
            )}

            <Card className="mb-4 shadow-sm border-0">
                <Card.Header className="bg-light">
                    <h4 className="mb-0">Información General</h4>
                </Card.Header>
                <Card.Body>
                    <ListGroup variant="flush">
                        <ListGroup.Item>
                            <strong>Estado:</strong>{" "}
                            <Badge bg={
                                tournamentState === "INSCRIPCIONES_ABIERTAS" ? "success" : 
                                tournamentState === "EN_CURSO" ? "warning" : "dark"}>
                                {tournament.status}
                            </Badge>
                        </ListGroup.Item>
                        <ListGroup.Item><strong>Tipo:</strong> {tournament.type}</ListGroup.Item>
                        {tournament.maxParticipants && (
                            <ListGroup.Item>
                                <strong>Plazas Totales:</strong> {tournament.maxParticipants}
                            </ListGroup.Item>
                        )}
                        {tournament.owner && (
                            <ListGroup.Item>
                                <strong>Administrador:</strong>{" "}
                                {tournament.owner.username || tournament.owner.name}
                            </ListGroup.Item>
                        )}
                    </ListGroup>
                </Card.Body>
            </Card>

            {/* CLASIFICACIÓN */}
            <div className="d-flex justify-content-between align-items-center mb-3 mt-4">
                <h3 className="mb-0">Clasificación / Equipos Inscritos</h3>
            </div>
            <Card className="shadow-sm border-0 mb-5">
                <Table responsive hover className="mb-0 text-center align-middle">
                    <thead className="table-light">
                        <tr>
                            <th>Pos</th>
                            <th className="text-start">Club</th>
                            <th>Pts</th>
                            <th>PJ</th>
                            <th>PG</th>
                            <th>PE</th>
                            <th>PP</th>
                            <th>GF</th>
                            <th>GC</th>
                            <th>+/-</th>
                        </tr>
                    </thead>
                    <tbody>
                        {standings.length > 0 ? (
                            standings.map((s: any, idx: number) => (
                                <tr key={s.team.id}>
                                    <td className="fw-bold">{idx + 1}</td>
                                    <td className="fw-bold text-start text-primary">
                                        {s.team.hasImage && (
                                            <img
                                                src={`/api/v1/images/teams/${s.team.id}/image`}
                                                alt="Escudo"
                                                width="25"
                                                height="25"
                                                className="me-2 rounded-circle shadow-sm"
                                            />
                                        )}
                                        {s.team.teamName || s.team.username}
                                    </td>
                                    <td className="fw-bold fs-5 text-success">{s.pts}</td>
                                    <td>{s.pJ}</td>
                                    <td>{s.pG}</td>
                                    <td>{s.pE}</td>
                                    <td>{s.pP}</td>
                                    <td>{s.gf}</td>
                                    <td>{s.gc}</td>
                                    <td className={s.gf - s.gc > 0 ? "text-success" : s.gf - s.gc < 0 ? "text-danger" : "text-muted"}>
                                        {s.gf - s.gc > 0 ? `+${s.gf - s.gc}` : s.gf - s.gc}
                                    </td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan={10} className="text-muted p-4 text-center">
                                    No hay equipos inscritos aún.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </Table>
            </Card>

            {/* PARTIDOS */}
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h3 className="mb-0">Desarrollo de la Competición</h3>
                {isAdmin && (
                    <Button
                        variant="warning"
                        onClick={handleGenerateSchedule}
                        disabled={loadingSchedule}
                    >
                        {loadingSchedule ? (
                            <Spinner size="sm" animation="border" />
                        ) : (
                            "⚔ Generar Calendario Oficial"
                        )}
                    </Button>
                )}
            </div>

            {tournament.matches && tournament.matches.length > 0 ? (
                <Card className="shadow-sm">
                    <Table responsive hover className="mb-0 align-middle text-center">
                        <thead className="table-light">
                            <tr>
                                <th>Local</th>
                                <th>Res.</th>
                                <th>Visitante</th>
                                <th>Estado</th>
                                {isAdmin && <th>Acción</th>}
                            </tr>
                        </thead>
                        <tbody>
                            {tournament.matches.map((m: MatchDTO) => (
                                <tr key={m.id}>
                                    <td className="fw-medium text-end">
                                        {m.homeTeam?.teamName || m.homeTeam?.username || "Local"}
                                    </td>
                                    <td className="fw-bold fs-5 px-3">
                                        {m.played ? (
                                            <span className="text-primary">
                                                {m.homeGoals} - {m.awayGoals}
                                            </span>
                                        ) : (
                                            <span className="text-muted">vs</span>
                                        )}
                                    </td>
                                    <td className="fw-medium text-start">
                                        {m.awayTeam?.teamName || m.awayTeam?.username || "Visitante"}
                                    </td>
                                    <td>
                                        {m.played ? (
                                            <Badge bg="success">Finalizado</Badge>
                                        ) : (
                                            <Badge bg="warning" text="dark">Pendiente</Badge>
                                        )}
                                    </td>
                                    {isAdmin && (
                                        <td>
                                            {!m.played ? (
                                                <Button
                                                    variant="outline-success"
                                                    size="sm"
                                                    onClick={() => handleSimulateMatch(m.id)}
                                                >
                                                    ▶ Simular
                                                </Button>
                                            ) : (
                                                <Badge bg="secondary">Hecho</Badge>
                                            )}
                                        </td>
                                    )}
                                </tr>
                            ))}
                        </tbody>
                    </Table>
                </Card>
            ) : (
                <p className="text-muted bg-light p-4 text-center rounded border">
                    Aún no hay partidos programados en este torneo.
                    {isAdmin && " Genera el calendario con el botón de arriba."}
                </p>
            )}
        </Container>
    );
}
