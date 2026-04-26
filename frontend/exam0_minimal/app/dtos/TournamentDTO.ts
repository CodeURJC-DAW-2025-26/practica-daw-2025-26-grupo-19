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
    state: string;
    status: string;
    type: string;
    maxParticipants: number;
    hasImage: boolean;
    teams: TeamBasicDTO[];
    matches: MatchDTO[];
    owner?: { username: string; name: string; };
}
