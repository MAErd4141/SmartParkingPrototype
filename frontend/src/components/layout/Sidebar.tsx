import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Car, 
  Users, 
  LogOut,
  ParkingSquare
} from 'lucide-react';
import { useAuth } from '@/contexts/AuthContext';
import { cn } from '@/lib/utils';

const navItems = [
  { to: '/dashboard', icon: LayoutDashboard, label: 'Dashboard' },
  { to: '/parking', icon: Car, label: 'Parking Lots' },
  { to: '/users', icon: Users, label: 'Users' },
];

export function Sidebar() {
  const { logout } = useAuth();

  return (
    <aside className="fixed left-0 top-0 z-40 h-screen w-64 border-r bg-card text-card-foreground shadow-sm">
      <div className="flex h-full flex-col">
        {/* Logo Alanı */}
        <div className="flex h-16 items-center gap-3 border-b px-6">
          <div className="flex h-8 w-8 items-center justify-center rounded bg-primary text-primary-foreground">
            <ParkingSquare className="h-5 w-5" />
          </div>
          <span className="text-lg font-bold tracking-tight">SmartPark</span>
        </div>

        {/* Menü Linkleri */}
        <nav className="flex-1 space-y-1 px-3 py-6">
          {navItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                cn(
                  "flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors",
                  isActive
                    ? "bg-primary text-primary-foreground shadow-sm"
                    : "text-muted-foreground hover:bg-accent hover:text-accent-foreground"
                )
              }
            >
              <item.icon className="h-4 w-4" />
              <span>{item.label}</span>
            </NavLink>
          ))}
        </nav>

        {/* Çıkış Butonu */}
        <div className="border-t p-4">
          <button
            onClick={logout}
            className="flex w-full items-center gap-3 rounded-md px-3 py-2 text-sm font-medium text-destructive transition-colors hover:bg-destructive/10"
          >
            <LogOut className="h-4 w-4" />
            <span>Çıkış Yap</span>
          </button>
        </div>
      </div>
    </aside>
  );
}