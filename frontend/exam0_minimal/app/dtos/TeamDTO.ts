export interface PlayerDTO {
    id: number;
    name: string;
    position: string;
    jerseyNumber: number;
    goals: number;
    assists: number;
    hasImage: boolean;
}

export interface TournamentBasicDTO {
    id: number;
    name: string;
    state: string;
    status: string;
    type: string;
}

export interface TeamDTO {
    id: number;
    username: string;
    teamName: string;
    email: string;
    hasImage: boolean;
    players: PlayerDTO[];
    tournaments: TournamentBasicDTO[];
}
