export enum TournamentType {
    LIGA = "LIGA",
    ELIMINATORIA = "ELIMINATORIA"
}

export enum TournamentStatus{
    INSCRIPCIONES = "INSCRIPCIONES_ABIERTAS",
    EN_CURSO = "EN_CURSO",
    FINALIZADO = "FINALIZADO"
}

export interface TeamBasicDTO {
    id: number;
    username: string;
    teamName: string;
    hasImage: boolean;
    goals?: number;
    assists?: number;
}

export interface MatchDTO {
    id: number;
    homeTeam: TeamBasicDTO;
    awayTeam: TeamBasicDTO;
    homeGoals: number;
    awayGoals: number;
    played: boolean;
}

export interface TournamentDTO {
    id: number;
    name: string;
    status: TournamentStatus;
    type: TournamentType;
    maxParticipants: number;
    hasImage: boolean;
    teams: TeamBasicDTO[];
    matches: MatchDTO[];
    owner?: { username: string; name: string; };
}
