import axios from 'axios';

// JÜRİ DÜZELTMESİ:
// URL'i "import.meta.env" üzerinden alıyoruz.
// Böylece Docker'da çalışırken farklı, local'de çalışırken farklı adres kullanabiliriz.
const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor (Token ekleme) - AYNEN KALIYOR
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor (401 Logout) - AYNEN KALIYOR
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// API Metodları (Aynen koruyoruz)
export const authApi = {
  login: async (email: string, password: string) => {
    const response = await api.post('/auth/login', { email, password });
    return response.data;
  },
};

export const dashboardApi = {
  getStats: async () => {
    const response = await api.get('/admin/dashboard');
    return response.data;
  },
};

// ... Diğer interface ve exportlar aynen kalacak ...
// (ParkingLot, User interface'leri vs. buraya ekli kalmalı)

export interface ParkingLot {
  id: string; // UUID olduğu için string olması daha güvenli
  name: string;
  code: string;
  hourlyRate: number;
  capacity: number;
  latitude: number;
  longitude: number;
  address: string;
}

export interface CreateParkingLotPayload {
  name: string;
  code: string;
  hourlyRate: number;
  capacity: number;
  latitude: number;
  longitude: number;
  address: string;
}

export const parkingApi = {
  getAll: async (): Promise<ParkingLot[]> => {
    const response = await api.get('/parking-lots');
    return response.data;
  },
  create: async (payload: CreateParkingLotPayload): Promise<ParkingLot> => {
    const response = await api.post('/parking-lots', payload);
    return response.data;
  },
};

export interface User {
  id: string;
  name: string;
  email: string;
  role: 'SUPERVISOR' | 'ADMIN' | 'BASIC';
}

export const usersApi = {
  getAll: async (): Promise<User[]> => {
    const response = await api.get('/users');
    return response.data;
  },
  delete: async (id: string): Promise<void> => {
    await api.delete(`/users/${id}`);
  },
};

export default api;