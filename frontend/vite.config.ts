import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";
import path from "path";
// componentTagger importunu kaldırıyoruz, production'da gerek yok.

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => ({
  server: {
    host: "::",
    port: 5173,
  },
  // JÜRİ DÜZELTMESİ: Sadece react() plugin'i kalsın, tagger'ı kaldırdık.
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
}));