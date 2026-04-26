import { reactRouter } from "@react-router/dev/vite";
import tailwindcss from "@tailwindcss/vite";
import tsconfigPaths from "vite-tsconfig-paths";
import { defineConfig } from "vite";

export default defineConfig(({ mode }) => ({
  // ¡No pierdas esto! Es vital para cuando compiles para producción (Tema 6)
  base: mode === "production" ? "/new/" : "/", 
  
  // Mantén tailwindcss() si vas a usar clases de Tailwind en algún sitio
  plugins: [tailwindcss(), reactRouter(), tsconfigPaths()],
  
  server: {
    proxy: {
      '/api': {
        target: 'https://localhost:8443/api',
        secure: false, // Necesario porque tu backend usa https autogenerado (localhost)
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, "")
      }
    }
  },
}));