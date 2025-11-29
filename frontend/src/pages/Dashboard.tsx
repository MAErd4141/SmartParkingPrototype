import { useQuery } from '@tanstack/react-query';
import { dashboardApi } from '@/lib/api';
import { Users, DollarSign, Car, CalendarCheck } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

// Mock revenue data for chart
const revenueData = [
  { month: 'Jan', revenue: 4200 },
  { month: 'Feb', revenue: 5100 },
  { month: 'Mar', revenue: 4800 },
  { month: 'Apr', revenue: 6200 },
  { month: 'May', revenue: 7100 },
  { month: 'Jun', revenue: 6800 },
];

interface StatCardProps {
  title: string;
  value: string | number;
  icon: React.ElementType;
  colorClass: string;
  loading?: boolean;
}

function StatCard({ title, value, icon: Icon, colorClass, loading }: StatCardProps) {
  return (
    <div className={`stat-card ${colorClass}`}>
      <div className="flex items-center justify-between">
        <div>
          <p className="text-sm font-medium text-muted-foreground">{title}</p>
          {loading ? (
            <div className="mt-2 h-8 w-20 animate-pulse rounded bg-muted" />
          ) : (
            <p className="mt-1 text-3xl font-bold text-foreground">{value}</p>
          )}
        </div>
        <div className="rounded-lg bg-muted p-3">
          <Icon className="h-6 w-6 text-muted-foreground" />
        </div>
      </div>
    </div>
  );
}

export default function Dashboard() {
  const { data: stats, isLoading } = useQuery({
    queryKey: ['dashboard-stats'],
    queryFn: dashboardApi.getStats,
    // Fallback to mock data if API fails
    placeholderData: {
      totalUsers: 0,
      totalRevenue: 0,
      totalLots: 0,
      todaysReservations: 0,
    },
  });

  return (
    <div className="space-y-8">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold text-foreground">Dashboard</h1>
        <p className="text-muted-foreground">Overview of your parking management system</p>
      </div>

      {/* Stat Cards */}
      <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-4">
        <StatCard
          title="Total Users"
          value={stats?.totalUsers ?? 0}
          icon={Users}
          colorClass="stat-card-blue"
          loading={isLoading}
        />
        <StatCard
          title="Total Revenue"
          value={`$${(stats?.totalRevenue ?? 0).toLocaleString()}`}
          icon={DollarSign}
          colorClass="stat-card-green"
          loading={isLoading}
        />
        <StatCard
          title="Parking Lots"
          value={stats?.totalLots ?? 0}
          icon={Car}
          colorClass="stat-card-orange"
          loading={isLoading}
        />
        <StatCard
          title="Today's Reservations"
          value={stats?.todaysReservations ?? 0}
          icon={CalendarCheck}
          colorClass="stat-card-purple"
          loading={isLoading}
        />
      </div>

      {/* Revenue Chart */}
      <Card>
        <CardHeader>
          <CardTitle className="text-lg font-semibold">Revenue Trends</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={revenueData} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
                <CartesianGrid strokeDasharray="3 3" className="stroke-border" />
                <XAxis 
                  dataKey="month" 
                  axisLine={false}
                  tickLine={false}
                  className="text-xs fill-muted-foreground"
                />
                <YAxis 
                  axisLine={false}
                  tickLine={false}
                  className="text-xs fill-muted-foreground"
                  tickFormatter={(value) => `$${value}`}
                />
                <Tooltip
                  contentStyle={{
                    backgroundColor: 'hsl(var(--card))',
                    border: '1px solid hsl(var(--border))',
                    borderRadius: '8px',
                  }}
                  labelStyle={{ color: 'hsl(var(--foreground))' }}
                  formatter={(value: number) => [`$${value.toLocaleString()}`, 'Revenue']}
                />
                <Bar 
                  dataKey="revenue" 
                  fill="hsl(var(--primary))" 
                  radius={[4, 4, 0, 0]}
                />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
