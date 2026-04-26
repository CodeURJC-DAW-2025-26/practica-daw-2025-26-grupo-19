import { TournamentStatus, TournamentType } from "./TournamentDTO";

export enum Role {
    USER = "USER",
    ADMIN = "ADMIN"
}

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
    status: TournamentStatus;
    type: TournamentType;
}

export interface TeamDTO {
    id: number;
    username: string;
    teamName: string;
    email: string;
    hasImage: boolean;
    roles?: Role[];
    players: PlayerDTO[];
    tournaments: TournamentBasicDTO[];
}
