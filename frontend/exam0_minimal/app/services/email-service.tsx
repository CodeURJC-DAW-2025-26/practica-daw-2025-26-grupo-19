const API_EMAIL_URL = "/api/v1/email";

export interface MessageResponse {
    message: string;
}

export async function forgotPassword(email: string): Promise<MessageResponse> {
    const res = await fetch(`${API_EMAIL_URL}/forgot-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email }),
    });

    const data: MessageResponse = await res.json();

    if (!res.ok) {
        throw new Error(data.message ?? `HTTP ${res.status}`);
    }

    return data;
}
