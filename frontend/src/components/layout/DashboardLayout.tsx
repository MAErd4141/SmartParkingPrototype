import { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { Sidebar } from './Sidebar'; // Sidebar dosyasının yeri doğru olmalı
import { useAuth } from '@/contexts/AuthContext';

interface DashboardLayoutProps {
  children: ReactNode;
}

export function DashboardLayout({ children }: DashboardLayoutProps) {
  const { isAuthenticated, isLoading } = useAuth();

  // 1. Yükleniyor Durumu (Şık bir spinner)
  if (isLoading) {
    return (
      <div className="flex h-screen w-full items-center justify-center bg-background">
        <div className="flex flex-col items-center gap-2">
          <div className="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent" />
          <p className="text-sm text-muted-foreground">Yükleniyor...</p>
        </div>
      </div>
    );
  }

  // 2. Güvenlik Duvarı
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // 3. Ana Layout (Sidebar + İçerik)
  return (
    <div className="flex min-h-screen bg-background">
      {/* Sidebar Sabit */}
      <Sidebar />

      {/* İçerik Alanı (Sidebar genişliği kadar soldan boşluk bırakıyoruz: ml-64) */}
      <main className="flex-1 ml-64 p-8 transition-all duration-300 ease-in-out">
        <div className="animate-fade-in space-y-6">
          {children}
        </div>
      </main>
    </div>
  );
}