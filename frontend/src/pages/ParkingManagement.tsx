import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { parkingApi } from '@/lib/api';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Skeleton } from '@/components/ui/skeleton';
import { Plus, Car } from 'lucide-react';
import { AddParkingModal } from '@/components/parking/AddParkingModal';

export default function ParkingManagement() {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const { data: parkingLots, isLoading } = useQuery({
    queryKey: ['parking-lots'],
    queryFn: parkingApi.getAll,
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-foreground">Parking Management</h1>
          <p className="text-muted-foreground">Manage your parking lots and locations</p>
        </div>
        <Button onClick={() => setIsModalOpen(true)}>
          <Plus className="mr-2 h-4 w-4" />
          Add Parking Lot
        </Button>
      </div>

      {/* Table */}
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2 text-lg">
            <Car className="h-5 w-5" />
            Parking Lots
          </CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Name</TableHead>
                <TableHead>Code</TableHead>
                <TableHead>Hourly Rate</TableHead>
                <TableHead>Capacity</TableHead>
                <TableHead>Address</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {isLoading ? (
                Array.from({ length: 5 }).map((_, i) => (
                  <TableRow key={i}>
                    <TableCell><Skeleton className="h-5 w-32" /></TableCell>
                    <TableCell><Skeleton className="h-5 w-20" /></TableCell>
                    <TableCell><Skeleton className="h-5 w-16" /></TableCell>
                    <TableCell><Skeleton className="h-5 w-12" /></TableCell>
                    <TableCell><Skeleton className="h-5 w-40" /></TableCell>
                  </TableRow>
                ))
              ) : parkingLots && parkingLots.length > 0 ? (
                parkingLots.map((lot) => (
                  <TableRow key={lot.id}>
                    <TableCell className="font-medium">{lot.name}</TableCell>
                    <TableCell>
                      <code className="rounded bg-muted px-2 py-1 text-sm">{lot.code}</code>
                    </TableCell>
                    <TableCell>${lot.hourlyRate.toFixed(2)}/hr</TableCell>
                    <TableCell>{lot.capacity} spots</TableCell>
                    <TableCell className="max-w-xs truncate text-muted-foreground">
                      {lot.address}
                    </TableCell>
                  </TableRow>
                ))
              ) : (
                <TableRow>
                  <TableCell colSpan={5} className="h-32 text-center">
                    <div className="flex flex-col items-center gap-2 text-muted-foreground">
                      <Car className="h-10 w-10 opacity-50" />
                      <p>No parking lots found</p>
                      <Button variant="outline" size="sm" onClick={() => setIsModalOpen(true)}>
                        Add your first parking lot
                      </Button>
                    </div>
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Modal */}
      <AddParkingModal open={isModalOpen} onOpenChange={setIsModalOpen} />
    </div>
  );
}
