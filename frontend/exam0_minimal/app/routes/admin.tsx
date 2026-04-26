import { useActionState, useState } from "react";
import {
    Container, Card, Table, Button, Tabs, Tab, Alert, Badge, Modal, Form,
} from "react-bootstrap";
import { Link, useNavigate } from "react-router";
import type { Route } from "./+types/admin";
import {
    getTournaments,
    addTournament,
    updateTournament,
    deleteTournament,
    uploadTournamentImage,
} from "~/services/tournaments-service";
import { getTeams, deleteTeam } from "~/services/teams-service";
import { useUserStore } from "~/stores/user-store";

export async function clientLoader({}: Route.ClientLoaderArgs) {
    const [tournamentsPage, teamsPage] = await Promise.all([
        getTournaments(0, 20),
        getTeams(0, 20),
    ]);
    return {
        initialTournaments: tournamentsPage?.content ?? [],
        initialTeams: teamsPage?.content ?? [],
    };
}

export default function AdminDashboard({ loaderData }: Route.ComponentProps) {
    const { initialTournaments, initialTeams } = loaderData;
    const { user } = useUserStore();
    const navigate = useNavigate();

    const [tournaments, setTournaments] = useState<any[]>(initialTournaments);
    const [teams, setTeams] = useState<any[]>(initialTeams);
    const [statusMessage, setStatusMessage] = useState<string | null>(null);
    const [showTournamentModal, setShowTournamentModal] = useState(false);
    const [editingTournament, setEditingTournament] = useState<any>(null);

    if (!user || (!user.roles.includes("ADMIN") && !user.roles.includes("MANAGER"))) {
        return (
            <Container className="py-5 text-center">
                <Alert variant="danger">
                    <h3>Acceso Denegado</h3>
                    <p>No tienes permisos suficientes para ver esta pantalla.</p>
                </Alert>
                <Button onClick={() => navigate("/")}>Volver al Inicio</Button>
            </Container>
        );
    }

    const handleEditClick = (t: any) => {
        setEditingTournament(t);
        setShowTournamentModal(true);
    };

    const handleCreateClick = () => {
        setEditingTournament(null);
        setShowTournamentModal(true);
    };

    async function saveTournamentAction(
        _prevState: { success: boolean; error: string | null } | null,
        formData: FormData
    ) {
        const imageFile = formData.get("imageFile") as File | null;
        const hasImageUpload = imageFile && imageFile.size > 0;
        const body = {
            name: formData.get("name") as string,
            type: formData.get("type") as string,
            status: formData.get("status") as string,
            maxParticipants: parseInt(formData.get("maxParticipants") as string, 10),
            hasImage: hasImageUpload || (editingTournament ? editingTournament.hasImage : false),
        };

        try {
            if (editingTournament) {
                const res = await updateTournament(editingTournament.id, body);
                if (hasImageUpload) {
                    await uploadTournamentImage(editingTournament.id, imageFile!);
                }
                setTournaments(tournaments.map((t) => (t.id === editingTournament.id ? res : t)));
                setStatusMessage("Torneo actualizado correctamente.");
            } else {
                const res = await addTournament(body);
                if (hasImageUpload) {
                    await uploadTournamentImage(res.id, imageFile!);
                }
                setTournaments([...tournaments, res]);
                setStatusMessage("Torneo creado correctamente.");
            }
            setShowTournamentModal(false);
            return { success: true, error: null };
        } catch (err) {
            return { success: false, error: "Error al guardar torneo. Compruebe los datos." };
        }
    }

    const [tournamentState, tournamentFormAction, isTournamentPending] = useActionState(
        saveTournamentAction,
        null
    );

    const handleDeleteTournament = async (id: number) => {
        if (!window.confirm("¿Seguro que deseas eliminar este torneo?")) return;
        try {
            await deleteTournament(id);
            setTournaments(tournaments.filter((t) => t.id !== id));
            setStatusMessage("Torneo eliminado correctamente.");
        } catch (e) {
            alert("Error al eliminar torneo.");
        }
    };

    const handleDeleteTeam = async (id: number) => {
        if (!window.confirm("¿Seguro que deseas eliminar este equipo y usuario?")) return;
        try {
            await deleteTeam(id);
            setTeams(teams.filter((t) => t.id !== id));
            setStatusMessage("Equipo eliminado correctamente.");
        } catch (e) {
            alert("Error al eliminar equipo.");
        }
    };

    return (
        <Container className="py-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h1>Panel de Gestión</h1>
                <Badge bg="primary" className="fs-6">
                    Sesión: {user.username}
                </Badge>
            </div>

            {statusMessage && (
                <Alert variant="success" onClose={() => setStatusMessage(null)} dismissible>
                    {statusMessage}
                </Alert>
            )}

            <Tabs defaultActiveKey="tournaments" id="admin-tabs" className="mb-4">
                {/* PESTAÑA DE TORNEOS */}
                <Tab eventKey="tournaments" title="Torneos (Admin)">
                    <Card className="shadow-sm border-0">
                        <Card.Header className="bg-white d-flex justify-content-between align-items-center py-3">
                            <h5 className="mb-0">Gestión de Torneos</h5>
                            {user.roles.includes("ADMIN") && (
                                <Button variant="success" size="sm" onClick={handleCreateClick}>
                                    Crear Torneo
                                </Button>
                            )}
                        </Card.Header>
                        <Table responsive hover className="mb-0 align-middle">
                            <thead className="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Nombre</th>
                                    <th>Tipo</th>
                                    <th>Estado</th>
                                    <th className="text-end">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {tournaments.map((t) => (
                                    <tr key={t.id}>
                                        <td>{t.id}</td>
                                        <td>{t.name}</td>
                                        <td>{t.type}</td>
                                        <td>{t.status || t.state}</td>
                                        <td className="text-end text-nowrap">
                                            <Link
                                                to={`/tournament/${t.id}`}
                                                className="btn btn-sm btn-outline-info me-2"
                                            >
                                                Ver
                                            </Link>
                                            {user.roles.includes("ADMIN") && (
                                                <>
                                                    <Button
                                                        variant="outline-warning"
                                                        size="sm"
                                                        className="me-2"
                                                        onClick={() => handleEditClick(t)}
                                                    >
                                                        Editar
                                                    </Button>
                                                    <Button
                                                        variant="outline-danger"
                                                        size="sm"
                                                        onClick={() => handleDeleteTournament(t.id)}
                                                    >
                                                        Borrar
                                                    </Button>
                                                </>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    </Card>
                </Tab>

                {/* PESTAÑA DE EQUIPOS */}
                <Tab eventKey="teams" title="Equipos e Inscripciones">
                    <Card className="shadow-sm border-0">
                        <Card.Header className="bg-white py-3">
                            <h5 className="mb-0">Gestión de Equipos Registrados</h5>
                        </Card.Header>
                        <Table responsive hover className="mb-0 align-middle">
                            <thead className="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Club</th>
                                    <th>Usuario</th>
                                    <th>Contacto</th>
                                    <th className="text-end">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {teams.map((team) => (
                                    <tr key={team.id}>
                                        <td>{team.id}</td>
                                        <td className="fw-bold">{team.teamName || team.username}</td>
                                        <td>{team.username}</td>
                                        <td>{team.email}</td>
                                        <td className="text-end text-nowrap">
                                            <Link
                                                to={`/team/${team.id}`}
                                                className="btn btn-sm btn-outline-primary me-2"
                                            >
                                                Ver Plantilla
                                            </Link>
                                            {user.roles.includes("ADMIN") && (
                                                <Button
                                                    variant="outline-danger"
                                                    size="sm"
                                                    onClick={() => handleDeleteTeam(team.id)}
                                                >
                                                    Eliminar
                                                </Button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    </Card>
                </Tab>
            </Tabs>

            {/* MODAL CREAR / EDITAR TORNEO */}
            <Modal show={showTournamentModal} onHide={() => setShowTournamentModal(false)}>
                <Modal.Header closeButton className="bg-light">
                    <Modal.Title>
                        {editingTournament ? "Editar Torneo" : "Crear Nuevo Torneo"}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {tournamentState?.error && (
                        <Alert variant="danger">{tournamentState.error}</Alert>
                    )}
                    <Form action={tournamentFormAction}>
                        <Form.Group className="mb-3">
                            <Form.Label>Nombre del Torneo</Form.Label>
                            <Form.Control
                                type="text"
                                name="name"
                                required
                                defaultValue={editingTournament?.name || ""}
                                disabled={isTournamentPending}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Tipo</Form.Label>
                            <Form.Select
                                name="type"
                                defaultValue={editingTournament?.type || "LIGA"}
                                disabled={isTournamentPending}
                            >
                                <option value="LIGA">Liga Regular</option>
                                <option value="ELIMINATORIA">Fase Eliminatoria</option>
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Estado</Form.Label>
                            <Form.Select
                                name="status"
                                defaultValue={editingTournament?.status || editingTournament?.state || "ABIERTO"}
                                disabled={isTournamentPending}
                            >
                                <option value="ABIERTO">Abierto (Inscripciones)</option>
                                <option value="EN_CURSO">En Curso</option>
                                <option value="FINALIZADO">Finalizado</option>
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Max Participantes</Form.Label>
                            <Form.Control
                                type="number"
                                name="maxParticipants"
                                required
                                defaultValue={editingTournament?.maxParticipants || 10}
                                disabled={isTournamentPending}
                            />
                        </Form.Group>
                        <Form.Group className="mb-4">
                            <Form.Label>Logo del Torneo</Form.Label>
                            <Form.Control
                                type="file"
                                name="imageFile"
                                accept="image/*"
                                disabled={isTournamentPending}
                            />
                        </Form.Group>
                        <div className="d-flex justify-content-end gap-2">
                            <Button
                                variant="secondary"
                                type="button"
                                onClick={() => setShowTournamentModal(false)}
                                disabled={isTournamentPending}
                            >
                                Cancelar
                            </Button>
                            <Button variant="primary" type="submit" disabled={isTournamentPending}>
                                {isTournamentPending ? "Guardando..." : "Guardar Torneo"}
                            </Button>
                        </div>
                    </Form>
                </Modal.Body>
            </Modal>
        </Container>
    );
}
    