import { useActionState, useState } from "react";
import {
    Container, Card, Table, Button, Tabs, Tab, Alert, Badge, Modal, Form, Spinner
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
import { 
    getTeams, 
    deleteTeam, 
    updateTeam, 
    uploadTeamImage 
} from "~/services/teams-service";
import { useUserStore } from "~/stores/user-store";

export async function clientLoader({}: Route.ClientLoaderArgs) {
    const [tournamentsPage, teamsPage] = await Promise.all([
        getTournaments(0, 20),
        getTeams(0, 10),
    ]);
    return {
        initialTournaments: tournamentsPage?.content ?? [],
        initialTeams: teamsPage?.content ?? [],
        initialIsLastTeams: teamsPage?.last ?? true,
    };
}

export default function AdminDashboard({ loaderData }: Route.ComponentProps) {
    const { initialTournaments, initialTeams, initialIsLastTeams } = loaderData;
    const { user } = useUserStore();
    const navigate = useNavigate();

    const [tournaments, setTournaments] = useState<any[]>(initialTournaments);
    const [statusMessage, setStatusMessage] = useState<string | null>(null);
    
    // Estados para Torneos
    const [showTournamentModal, setShowTournamentModal] = useState(false);
    const [editingTournament, setEditingTournament] = useState<any>(null);

    // Estados para Equipos 
    const [teams, setTeams] = useState<any[]>(initialTeams);
    const [showTeamModal, setShowTeamModal] = useState(false);
    const [editingTeam, setEditingTeam] = useState<any>(null);
    
    const [teamsPage, setTeamsPage] = useState(0);
    const [isLastTeams, setIsLastTeams] = useState<boolean>(initialIsLastTeams);
    const [isLoadingMoreTeams, setIsLoadingMoreTeams] = useState(false);

    // Verificación de permisos básica
    if (!user || (!user.roles?.includes("ADMIN") && !user.roles?.includes("USER"))) {
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

    const loadMoreTeams = async () => {
        setIsLoadingMoreTeams(true);
        try {
            const nextPage = teamsPage + 1;
            const data = await getTeams(nextPage, 10);
            setTeams((prev) => [...prev, ...(data.content ?? [])]);
            setTeamsPage(nextPage);
            setIsLastTeams(data.last);
        } catch (err) {
            console.error("Error cargando más equipos:", err);
        } finally {
            setIsLoadingMoreTeams(false);
        }
    };

    // --- MANEJO DE TORNEOS ---
    const handleEditTournamentClick = (t: any) => {
        setEditingTournament(t);
        setShowTournamentModal(true);
    };

    const handleCreateTournamentClick = () => {
        setEditingTournament(null);
        setShowTournamentModal(true);
    };

    async function saveTournamentAction(
        _prevState: any,
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
                setTournaments(tournaments.map((t) => (t.id === editingTournament.id ? { ...res, ...body } : t)));
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


    // --- MANEJO DE EQUIPOS ---
    const handleEditTeamClick = (team: any) => {
        setEditingTeam(team);
        setShowTeamModal(true);
    };

    async function saveTeamAction(
        _prevState: any,
        formData: FormData
    ) {
        const teamLogo = formData.get("teamLogo") as File | null;
        const hasImageUpload = teamLogo && teamLogo.size > 0;
        
        const body = {
            username: formData.get("username") as string,
            email: formData.get("email") as string,
            teamName: formData.get("teamName") as string,
            hasImage: hasImageUpload || (editingTeam ? editingTeam.hasImage : false),
        };

        try {
            if (editingTeam) {
                await updateTeam(editingTeam.id, body);
                
                if (hasImageUpload) {
                    await uploadTeamImage(editingTeam.id, teamLogo!);
                }
                
                setTeams(teams.map((t) => (t.id === editingTeam.id ? { ...t, ...body } : t)));
                setStatusMessage("Equipo actualizado correctamente.");
                setShowTeamModal(false);
                return { success: true, error: null };
            }
            return { success: false, error: "No se ha seleccionado ningún equipo." };
        } catch (err) {
            console.error("Fallo al actualizar:", err);
            return { success: false, error: "Error al actualizar el equipo. Verifica que los datos sean únicos." };
        }
    }

    const [teamState, teamFormAction, isTeamPending] = useActionState(
        saveTeamAction,
        null
    );

    const handleDeleteTeam = async (id: number) => {
        if (!window.confirm("¿Estás seguro de eliminar este equipo? Esta acción no se puede deshacer.")) return;
        try {
            await deleteTeam(id);
            setTeams(teams.filter((t) => t.id !== id));
            setStatusMessage("Equipo eliminado con éxito.");
        } catch (e) {
            alert("No se pudo eliminar el equipo.");
        }
    };


    return (
        <Container className="py-5">
            <div className="d-flex justify-content-between align-items-center mb-4">
                <h1>Panel de Administración</h1>
                <Badge bg="dark" className="p-2">
                    Usuario: {user.username}
                </Badge>
            </div>

            {statusMessage && (
                <Alert variant="success" onClose={() => setStatusMessage(null)} dismissible>
                    {statusMessage}
                </Alert>
            )}

            <Tabs defaultActiveKey="teams" className="mb-4 shadow-sm p-2 bg-white rounded">
                {/* TAB TORNEOS */}
                <Tab eventKey="tournaments" title="Torneos">
                    <Card className="border-0 shadow-sm">
                        <Card.Header className="bg-white d-flex justify-content-between align-items-center py-3">
                            <h5 className="mb-0">Gestión de Competiciones</h5>
                            {user.roles.includes("ADMIN") && (
                                <Button variant="success" size="sm" onClick={handleCreateTournamentClick}>
                                    Añadir Torneo
                                </Button>
                            )}
                        </Card.Header>
                        <Table hover responsive className="mb-0 align-middle">
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
                                        <td className="fw-bold">{t.name}</td>
                                        <td><Badge bg="secondary">{t.type}</Badge></td>
                                        <td>{t.status || t.state}</td>
                                        <td className="text-end">
                                            <Link to={`/tournament/${t.id}`} className="btn btn-sm btn-outline-info me-2">Ver</Link>
                                            {user.roles.includes("ADMIN") && (
                                                <>
                                                    <Button variant="outline-warning" size="sm" className="me-2" onClick={() => handleEditTournamentClick(t)}>Editar</Button>
                                                    <Button variant="outline-danger" size="sm" onClick={() => handleDeleteTournament(t.id)}>Borrar</Button>
                                                </>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    </Card>
                </Tab>

                {/* TAB EQUIPOS */}
                <Tab eventKey="teams" title="Equipos">
                    <Card className="border-0 shadow-sm">
                        <Card.Header className="bg-white py-3">
                            <h5 className="mb-0">Gestión de Clubes Registrados</h5>
                        </Card.Header>
                        <Table hover responsive className="mb-0 align-middle">
                            <thead className="table-light">
                                <tr>
                                    <th>ID</th>
                                    <th>Nombre del Club</th>
                                    <th>Usuario</th>
                                    <th>Email</th>
                                    <th className="text-end">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {teams.map((team) => (
                                    <tr key={team.id}>
                                        <td>{team.id}</td>
                                        <td className="fw-bold">{team.teamName}</td>
                                        <td>{team.username}</td>
                                        <td>{team.email}</td>
                                        <td className="text-end">
                                            <Link to={`/team/${team.id}`} className="btn btn-sm btn-outline-primary me-2">Ver</Link>
                                            {user.roles.includes("ADMIN") && (
                                                <>
                                                    <Button variant="outline-warning" size="sm" className="me-2" onClick={() => handleEditTeamClick(team)}>Editar</Button>
                                                    <Button variant="outline-danger" size="sm" onClick={() => handleDeleteTeam(team.id)}>Eliminar</Button>
                                                </>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </Table>
                    </Card>
                    
                    {!isLastTeams && (
                        <div className="text-center mt-4 mb-3">
                            <Button
                                variant="dark"
                                onClick={loadMoreTeams}
                                disabled={isLoadingMoreTeams}
                                size="lg"
                                className="px-5"
                            >
                                {isLoadingMoreTeams ? (
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
                </Tab>
            </Tabs>

            {/* MODAL TORNEO */}
            <Modal show={showTournamentModal} onHide={() => setShowTournamentModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>{editingTournament ? "Editar Torneo" : "Nuevo Torneo"}</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {tournamentState?.error && <Alert variant="danger">{tournamentState.error}</Alert>}
                    <Form action={tournamentFormAction}>
                        <Form.Group className="mb-3">
                            <Form.Label>Nombre</Form.Label>
                            <Form.Control type="text" name="name" defaultValue={editingTournament?.name || ""} required />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Tipo</Form.Label>
                            <Form.Select name="type" defaultValue={editingTournament?.type || "LIGA"}>
                                <option value="LIGA">Liga</option>
                                <option value="ELIMINATORIA">Eliminatoria</option>
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Estado</Form.Label>
                            <Form.Select name="status" defaultValue={editingTournament?.status || "INSCRIPCIONES_ABIERTAS"}>
                                <option value="INSCRIPCIONES_ABIERTAS">Inscripciones Abiertas</option>
                                <option value="EN_CURSO">En Curso</option>
                                <option value="FINALIZADO">Finalizado</option>
                            </Form.Select>
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Máx. Participantes</Form.Label>
                            <Form.Control type="number" name="maxParticipants" defaultValue={editingTournament?.maxParticipants || 8} />
                        </Form.Group>
                        <Form.Group className="mb-4">
                            <Form.Label>Logo / Imagen</Form.Label>
                            <Form.Control type="file" name="imageFile" accept="image/*" />
                        </Form.Group>
                        <div className="text-end">
                            <Button variant="secondary" onClick={() => setShowTournamentModal(false)} className="me-2">Cancelar</Button>
                            <Button variant="primary" type="submit" disabled={isTournamentPending}>Guardar</Button>
                        </div>
                    </Form>
                </Modal.Body>
            </Modal>

            {/* MODAL EQUIPO */}
            <Modal show={showTeamModal} onHide={() => setShowTeamModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Editar Equipo</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {teamState?.error && <Alert variant="danger">{teamState.error}</Alert>}
                    <Form action={teamFormAction}>
                        <Form.Group className="mb-3">
                            <Form.Label>Nombre del Club</Form.Label>
                            <Form.Control type="text" name="teamName" defaultValue={editingTeam?.teamName || ""} required />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Usuario</Form.Label>
                            <Form.Control type="text" name="username" defaultValue={editingTeam?.username || ""} required />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Email</Form.Label>
                            <Form.Control type="email" name="email" defaultValue={editingTeam?.email || ""} required />
                        </Form.Group>
                        <Form.Group className="mb-4">
                            <Form.Label>Actualizar Escudo</Form.Label>
                            <Form.Control type="file" name="teamLogo" accept="image/*" />
                        </Form.Group>
                        <div className="text-end">
                            <Button variant="secondary" onClick={() => setShowTeamModal(false)} className="me-2">Cancelar</Button>
                            <Button variant="primary" type="submit" disabled={isTeamPending}>Actualizar Equipo</Button>
                        </div>
                    </Form>
                </Modal.Body>
            </Modal>
        </Container>
    );
}