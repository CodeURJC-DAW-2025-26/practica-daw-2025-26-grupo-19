import type { TournamentDTO } from "~/dtos/TournamentDTO";

const API_URL = "/api/v1/tournaments";
const API_MATCHES_URL = "/api/v1/matches";
const API_IMAGES_URL = "/api/v1/images";

export async function getTournaments(page = 0, size = 10) {
    const res = await fetch(`${API_URL}/?page=${page}&size=${size}`);
    if (!res.ok) throw new Error("Error loading tournaments");
    return await res.json();
}

export async function getTournament(id: string | number): Promise<TournamentDTO> {
    const res = await fetch(`${API_URL}/${id}`);
    if (!res.ok) throw new Error("Tournament not found");
    return await res.json();
}

export async function addTournament(data: {
    name: string;
    type: string;
    status: string;
    maxParticipants: number;
    hasImage: boolean;
}): Promise<TournamentDTO> {
    const res = await fetch(`${API_URL}/`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error("Error creating tournament");
    return await res.json();
}

export async function updateTournament(id: number, data: {
    name: string;
    type: string;
    status: string;
    maxParticipants: number;
    hasImage: boolean;
}): Promise<TournamentDTO> {
    const res = await fetch(`${API_URL}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
    });
    if (!res.ok) throw new Error("Error updating tournament");
    return await res.json();
}

export async function deleteTournament(id: number): Promise<void> {
    const res = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
    if (!res.ok) throw new Error("Error deleting tournament");
}

export async function generateSchedule(tournamentId: number): Promise<TournamentDTO> {
    const res = await fetch(`${API_URL}/${tournamentId}/schedule`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
    });
    if (!res.ok) throw new Error("Error generating schedule");
    return await res.json();
}

export async function simulateMatch(matchId: number): Promise<any> {
    const res = await fetch(`${API_MATCHES_URL}/${matchId}/simulate`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
    });
    if (!res.ok) throw new Error("Error simulating match");
    return await res.json();
}

export async function enrollTeam(tournamentId: number): Promise<TournamentDTO> {
    const res = await fetch(`${API_URL}/${tournamentId}/enroll`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
    });
    if (!res.ok) throw new Error("Error enrolling team");
    return await res.json();
}

export async function uploadTournamentImage(tournamentId: number, imageFile: File): Promise<void> {
    const formData = new FormData();
    formData.append("imageFile", imageFile);
    const res = await fetch(`${API_IMAGES_URL}/tournament/${tournamentId}/image`, {
        method: "POST",
        body: formData,
    });
    if (!res.ok) throw new Error("Error uploading tournament image");
}

export async function getTopScorers(): Promise<any[]> {
    const res = await fetch("/api/v1/statistics/scorers");
    if (!res.ok) return [];
    return await res.json();
}

export async function getTopAssisters(): Promise<any[]> {
    const res = await fetch("/api/v1/statistics/assisters");
    if (!res.ok) return [];
    return await res.json();
}
