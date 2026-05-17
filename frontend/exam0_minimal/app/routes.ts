import { type RouteConfig, index, layout, route } from "@react-router/dev/routes";

export default [
    layout("routes/home.tsx", [
        index("routes/index.tsx"),
        route("tournaments", "routes/torneos.tsx"),
        route("tournament/:id", "routes/torneo-detail.tsx"),
        route("teams", "routes/equipos.tsx"),
        route("team/:id", "routes/equipo-detail.tsx"),
        route("admin", "routes/admin.tsx"),
        route("register", "routes/register.tsx"),
        route("login", "routes/login.tsx"),
        route("*", "routes/not-found.tsx"),
    ]),
] satisfies RouteConfig;
