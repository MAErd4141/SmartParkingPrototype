import { Toaster } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "@/contexts/AuthContext";
import { DashboardLayout } from "@/components/layout/DashboardLayout";

// Sayfalar (Import yollarının doğruluğundan emin ol)
import Login from "./pages/Login";
import Dashboard from "./pages/Dashboard";
import ParkingManagement from "./pages/ParkingManagement";
import UserManagement from "./pages/UserManagement";
import NotFound from "./pages/NotFound";

// React Query Ayarları
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1, // Hata olursa 1 kere tekrar dene
      refetchOnWindowFocus: false, // Başka sekmeye gidip gelince sayfayı yenileme
      staleTime: 1000 * 60 * 5, // Veriyi 5 dakika "taze" kabul et
    },
  },
});

const App = () => (
  <QueryClientProvider client={queryClient}>
    <TooltipProvider>
      <Toaster position="top-right" richColors closeButton />

      {/* Router En Dışta Olmalı */}
      <BrowserRouter>
        {/* AuthProvider Router'ın içinde olmalı ki useNavigate kullanabilsin */}
        <AuthProvider>
          <Routes>
            {/* Public Rotalar */}
            <Route path="/login" element={<Login />} />

            {/* Kök dizine gelen isteği Dashboard'a yönlendir */}
            <Route path="/" element={<Navigate to="/dashboard" replace />} />

            {/* Korumalı (Private) Rotalar */}
            <Route
              path="/dashboard"
              element={
                <DashboardLayout>
                  <Dashboard />
                </DashboardLayout>
              }
            />

            <Route
              path="/parking"
              element={
                <DashboardLayout>
                  <ParkingManagement />
                </DashboardLayout>
              }
            />

            <Route
              path="/users"
              element={
                <DashboardLayout>
                  <UserManagement />
                </DashboardLayout>
              }
            />

            {/* 404 Sayfası */}
            <Route path="*" element={<NotFound />} />
          </Routes>
        </AuthProvider>
      </BrowserRouter>

    </TooltipProvider>
  </QueryClientProvider>
);

export default App;