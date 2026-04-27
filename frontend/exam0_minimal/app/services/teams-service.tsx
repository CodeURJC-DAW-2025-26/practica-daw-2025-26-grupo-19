import type { TeamDTO } from "~/dtos/TeamDTO";

const API_URL = "/api/v1/teams";
const API_IMAGES_URL = "/api/v1/images";
const API_PLAYERS_URL = "/api/v1/players";

export async function getTeams(page = 0, size = 9) {
    const res = await fetch(`${API_URL}/?page=${page}&size=${size}`);
    if (!res.ok) throw new Error("Error loading teams");
    return await res.json();
}

export async function getTeam(id: string | number): Promise<TeamDTO> {
    const res = await fetch(`${API_URL}/${id}`);
    if (!res.ok) throw new Error("Team not found");
    return await res.json();
}

export async function deleteTeam(id: number): Promise<void> {
    const res = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error("Error deleting team");
}

export async function registerTeam(
    username: string,
    email: string,
    password: string,
    teamName: string
): Promise<void> {
    const res = await fetch(`${API_URL}/register`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, email, password, teamName }),
    });
    if (!res.ok) throw new Error("Error registering team");
}

// --- PLAYERS ---

export async function addPlayer(playerData: {
    name: string;
    position: string;
    jerseyNumber: number;
    goals: number;
    assists: number;
    hasImage: boolean;
}): Promise<any> {
    const res = await fetch(`${API_PLAYERS_URL}/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(playerData),
    });
    if (!res.ok) throw new Error("Error adding player");
    return await res.json();
}

export async function updatePlayer(id: number, playerData: {
    name: string;
    position: string;
    jerseyNumber: number;
    goals: number;
    assists: number;
    hasImage: boolean;
}): Promise<any> {
    const res = await fetch(`${API_PLAYERS_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(playerData),
    });
    if (!res.ok) throw new Error("Error updating player");
    return await res.json();
}

export async function deletePlayer(id: number): Promise<void> {
    const res = await fetch(`${API_PLAYERS_URL}/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error("Error deleting player");
}

export async function uploadTeamImage(teamId: number, imageFile: File): Promise<void> {
    const formData = new FormData();
    formData.append("imageFile", imageFile);
    const res = await fetch(`${API_IMAGES_URL}/teams/${teamId}/image`, {
        method: "POST",
        body: formData,
    });
    if (!res.ok) throw new Error("Error uploading team image");
}

export async function uploadPlayerImage(playerId: number, imageFile: File): Promise<void> {
    const formData = new FormData();
    formData.append("imageFile", imageFile);
    const res = await fetch(`${API_IMAGES_URL}/player/${playerId}/image`, {
        method: "POST",
        body: formData,
    });
    if (!res.ok) throw new Error("Error uploading player image");
}
