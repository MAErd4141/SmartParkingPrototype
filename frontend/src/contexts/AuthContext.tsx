import React, { createContext, useContext, useState, ReactNode } from 'react';
import { authApi } from '@/lib/api';
import { useQueryClient } from '@tanstack/react-query';
import { toast } from 'sonner';

interface AuthContextType {
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(() => {
    return !!localStorage.getItem('authToken');
  });

  const queryClient = useQueryClient();

  const login = async (email: string, password: string) => {
    try {
      const response = await authApi.login(email, password);
      const token = response.token || response.accessToken;

      localStorage.setItem('authToken', token);
      setIsAuthenticated(true);
      toast.success("Giriş başarılı!");
    } catch (error) {
      console.error("Login hatası:", error);
      throw error;
    }
  };

  const logout = () => {
    localStorage.removeItem('authToken');
    setIsAuthenticated(false);
    queryClient.clear(); // Cache temizle
    toast.info("Çıkış yapıldı.");
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}