import { useActionState, useState } from "react";
import {
    Container, Card, ListGroup, Button, Table, Row, Col, Badge, Modal, Form,
} from "react-bootstrap";
import { useNavigate } from "react-router";
import type { Route } from "./+types/equipo-detail";
import {
    getTeam,
    addPlayer,
    updatePlayer,
    deletePlayer,
    uploadTeamImage,
    uploadPlayerImage,
} from "~/services/teams-service";
import { useUserStore } from "~/stores/user-store";
import type { TeamDTO, PlayerDTO } from "~/dtos/TeamDTO";

export async function clientLoader({ params }: Route.ClientLoaderArgs) {
    return await getTeam(params.id!);
}

export default function EquipoDetail({ loaderData }: Route.ComponentProps) {
    const navigate = useNavigate();
    const { user } = useUserStore();

    const [team, setTeam] = useState<TeamDTO>(loaderData);
    const [showPlayerModal, setShowPlayerModal] = useState(false);
    const [editingPlayer, setEditingPlayer] = useState<PlayerDTO | null>(null);
    const [playerError, setPlayerError] = useState<string | null>(null);

    const isOwner = user && (user.username === team.username || user.roles.includes("ADMIN"));

    const handleEditClick = (player: PlayerDTO) => {
        setEditingPlayer(player);
        setPlayerError(null);
        setShowPlayerModal(true);
    };

    const handleCreateClick = () => {
        setEditingPlayer(null);
        setPlayerError(null);
        setShowPlayerModal(true);
    };

    async function savePlayerAction(
        _prevState: { success: boolean; error: string | null } | null,
        formData: FormData
    ) {
        const imageFile = formData.get("imageFile") as File | null;
        const hasImageUpload = imageFile && imageFile.size > 0;

        const body = {
            name: formData.get("name") as string,
            position: formData.get("position") as string,
            jerseyNumber: parseInt(formData.get("jerseyNumber") as string, 10),
            goals: parseInt(formData.get("goals") as string, 10) || 0,
            assists: parseInt(formData.get("assists") as string, 10) || 0,
            hasImage: hasImageUpload || (editingPlayer ? editingPlayer.hasImage : false),
        };

        try {
            if (editingPlayer) {
                const res = await updatePlayer(editingPlayer.id, body);
                if (hasImageUpload) {
                    await uploadPlayerImage(editingPlayer.id, imageFile!);
                }
                setTeam({ ...team, players: team.players.map((p) => p.id === editingPlayer.id ? res : p) });
            } else {
                const res = await addPlayer(body);
                if (hasImageUpload) {
                    await uploadPlayerImage(res.id, imageFile!);
                }
                setTeam({ ...team, players: [...team.players, res] });
            }
            setShowPlayerModal(false);
            return { success: true, error: null };
        } catch (err) {
            return { success: false, error: "Error al guardar jugador. Comprueba los datos." };
        }
    }

    const [playerState, playerFormAction, isPlayerPending] = useActionState(savePlayerAction, null);

    const handleDeletePlayer = async (id: number) => {
        if (!window.confirm("¿Estás seguro de eliminar a este jugador?")) return;
        try {
            await deletePlayer(id);
            setTeam({ ...team, players: team.players.filter((p) => p.id !== id) });
        } catch (e) {
            alert("Error eliminando jugador.");
        }
    };

    const handleUploadTeamImage = async (e: React.ChangeEvent<HTMLInputElement>) => {
        if (!e.target.files || e.target.files.length === 0) return;
        try {
            await uploadTeamImage(team.id, e.target.files[0]);
            setTeam({ ...team, hasImage: true });
        } catch (err) {
            alert("Error subiendo escudo.");
        }
    };

    return (
        <Container className="py-5">
            <div className="mb-4 d-flex align-items-center gap-3">
                <Button variant="outline-secondary" onClick={() => navigate("/equipos")}>
                    &larr; Volver
                </Button>
                <h1 className="mb-0 d-flex align-items-center gap-3">
                    {team.hasImage && (
                        <img
                            src={`/api/v1/images/teams/${team.id}/image`}
                            alt="Escudo"
                            width="60"
                            height="60"
                            className="rounded-circle border shadow-sm"
                        />
                    )}
                    Club: {team.teamName || team.username}
                </h1>
            </div>

            <Row>
                <Col lg={4}>
                    <Card className="mb-4 shadow-sm border-0">
                        <Card.Header className="bg-primary text-white">
                            <h5 className="mb-0">Ficha del Club</h5>
                        </Card.Header>
                        <Card.Body>
                            <ListGroup variant="flush">
                                <ListGroup.Item>
                                    <strong>Mánager:</strong> {team.username}
                                </ListGroup.Item>
                                <ListGroup.Item>
                                    <strong>Email de Contacto:</strong> {team.email}
                                </ListGroup.Item>
                            </ListGroup>
                            {isOwner && (
                                <div className="mt-3 text-center">
                                    <Form.Label className="btn btn-sm btn-outline-primary mb-0 w-100">
                                        Sube tu Escudo
                                        <Form.Control
                                            type="file"
                                            size="sm"
                                            className="d-none"
                                            accept="image/*"
                                            onChange={handleUploadTeamImage}
                                        />
                                    </Form.Label>
                                </div>
                            )}
                        </Card.Body>
                    </Card>
                </Col>

                <Col lg={8}>
                    <div className="d-flex justify-content-between align-items-center mb-3">
                        <h3 className="mb-0">Plantilla de Jugadores</h3>
                        {isOwner && (
                            <Button variant="success" size="sm" onClick={handleCreateClick}>
                                + Añadir Jugador
                            </Button>
                        )}
                    </div>

                    {team.players && team.players.length > 0 ? (
                        <Card className="shadow-sm border-0">
                            <Table responsive hover className="mb-0 align-middle text-center">
                                <thead className="table-light">
                                    <tr>
                                        <th>Dorsal</th>
                                        <th>Nombre</th>
                                        <th>Posición</th>
                                        <th>Goles</th>
                                        <th>Asistencias</th>
                                        {isOwner && <th>Acciones</th>}
                                    </tr>
                                </thead>
                                <tbody>
                                    {team.players.map((p) => (
                                        <tr key={p.id}>
                                            <td>
                                                <h4><Badge bg="dark">{p.jerseyNumber || "-"}</Badge></h4>
                                            </td>
                                            <td className="fw-bold align-middle">
                                                <div className="d-flex align-items-center justify-content-center gap-2">
                                                    {p.hasImage ? (
                                                        <img
                                                            src={`/api/v1/images/player/${p.id}/image`}
                                                            alt="Avatar"
                                                            width="35"
                                                            height="35"
                                                            className="rounded-circle shadow-sm"
                                                        />
                                                    ) : (
                                                        <div
                                                            className="rounded-circle bg-secondary text-white d-flex align-items-center justify-content-center shadow-sm"
                                                            style={{ width: "35px", height: "35px", fontSize: "12px" }}
                                                        >
                                                            {p.name.charAt(0)}
                                                        </div>
                                                    )}
                                                    {p.name}
                                                </div>
                                            </td>
                                            <td>{p.position}</td>
                                            <td className="text-success fw-bold fs-5">{p.goals}</td>
                                            <td className="text-secondary fs-5">{p.assists}</td>
                                            {isOwner && (
                                                <td className="text-nowrap">
                                                    <Button
                                                        variant="outline-warning"
                                                        size="sm"
                                                        className="me-2"
                                                        onClick={() => handleEditClick(p)}
                                                    >
                                                        Editar
                                                    </Button>
                                                    <Button
                                                        variant="outline-danger"
                                                        size="sm"
                                                        onClick={() => handleDeletePlayer(p.id)}
                                                    >
                                                        X
                                                    </Button>
                                                </td>
                                            )}
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                        </Card>
                    ) : (
                        <p className="text-muted bg-light p-4 rounded text-center">
                            Este equipo no tiene jugadores inscritos actualmente.
                        </p>
                    )}

                    <h3 className="mb-3 mt-5">Competiciones disputadas</h3>
                    {team.tournaments && team.tournaments.length > 0 ? (
                        <ListGroup>
                            {team.tournaments.map((t) => (
                                <ListGroup.Item
                                    key={t.id}
                                    action
                                    onClick={() => navigate(`/tournament/${t.id}`)}
                                >
                                    <div className="d-flex justify-content-between align-items-center">
                                        <span>
                                            <strong>{t.name}</strong> ({t.type})
                                        </span>
                                        <Badge bg="info">{t.state || t.status}</Badge>
                                    </div>
                                </ListGroup.Item>
                            ))}
                        </ListGroup>
                    ) : (
                        <p className="text-muted">
                            El equipo no se encuentra participando en torneos en este momento.
                        </p>
                    )}
                </Col>
            </Row>

            {/* MODAL CREAR/EDITAR JUGADOR */}
            <Modal show={showPlayerModal} onHide={() => setShowPlayerModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>
                        {editingPlayer ? "Editar Jugador" : "Fichar Nuevo Jugador"}
                    </Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {playerState?.error && (
                        <div className="alert alert-danger">{playerState.error}</div>
                    )}
                    <Form action={playerFormAction}>
                        <Form.Group className="mb-3">
                            <Form.Label>Nombre</Form.Label>
                            <Form.Control
                                type="text"
                                name="name"
                                required
                                defaultValue={editingPlayer?.name || ""}
                                disabled={isPlayerPending}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Dorsal</Form.Label>
                            <Form.Control
                                type="number"
                                name="jerseyNumber"
                                required
                                defaultValue={editingPlayer?.jerseyNumber || 1}
                                min="1"
                                max="99"
                                disabled={isPlayerPending}
                            />
                        </Form.Group>
                        <Form.Group className="mb-3">
                            <Form.Label>Posición</Form.Label>
                            <Form.Select
                                name="position"
                                defaultValue={editingPlayer?.position || "DELANTERO"}
                                disabled={isPlayerPending}
                            >
                                <option value="PORTERO">Portero</option>
                                <option value="DEFENSA">Defensa</option>
                                <option value="CENTROCAMPISTA">Centrocampista</option>
                                <option value="DELANTERO">Delantero</option>
                            </Form.Select>
                        </Form.Group>
                        <Row>
                            <Col>
                                <Form.Group className="mb-3">
                                    <Form.Label>Goles</Form.Label>
                                    <Form.Control
                                        type="number"
                                        name="goals"
                                        defaultValue={editingPlayer?.goals || 0}
                                        disabled={isPlayerPending}
                                    />
                                </Form.Group>
                            </Col>
                            <Col>
                                <Form.Group className="mb-3">
                                    <Form.Label>Asistencias</Form.Label>
                                    <Form.Control
                                        type="number"
                                        name="assists"
                                        defaultValue={editingPlayer?.assists || 0}
                                        disabled={isPlayerPending}
                                    />
                                </Form.Group>
                            </Col>
                        </Row>
                        <Form.Group className="mb-3">
                            <Form.Label>Foto del Jugador</Form.Label>
                            <Form.Control
                                type="file"
                                name="imageFile"
                                accept="image/*"
                                disabled={isPlayerPending}
                            />
                        </Form.Group>
                        <div className="d-flex justify-content-end gap-2">
                            <Button
                                variant="secondary"
                                type="button"
                                onClick={() => setShowPlayerModal(false)}
                                disabled={isPlayerPending}
                            >
                                Cancelar
                            </Button>
                            <Button variant="primary" type="submit" disabled={isPlayerPending}>
                                {isPlayerPending ? "Guardando..." : "Guardar Ficha"}
                            </Button>
                        </div>
                    </Form>
                </Modal.Body>
            </Modal>
        </Container>
    );
}
