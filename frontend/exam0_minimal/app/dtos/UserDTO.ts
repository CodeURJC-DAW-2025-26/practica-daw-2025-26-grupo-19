 export interface UserDTO {
    id: number;
    username: string;
    name?: string;      // Lo hacemos opcional
    teamName?: string;  // Añadimos el campo que realmente manda el backend
    roles?: string[];   // Lo hacemos opcional
}
