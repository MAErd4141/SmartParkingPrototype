import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { parkingApi } from '@/lib/api';
import { toast } from 'sonner';
import { Button } from '@/components/ui/button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';

// 1. Validasyon Kuralları
const formSchema = z.object({
  name: z.string().min(2, "İsim çok kısa"),
  code: z.string().min(2, "Kod çok kısa"),
  hourlyRate: z.coerce.number().min(0, "Fiyat 0'dan küçük olamaz"),
  capacity: z.coerce.number().min(1, "Kapasite en az 1 olmalıdır"),
  address: z.string().min(3, "Adres çok kısa"),
  latitude: z.coerce.number().default(41.0),
  longitude: z.coerce.number().default(29.0),
});

type FormValues = z.infer<typeof formSchema>;

interface AddParkingModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function AddParkingModal({ open, onOpenChange }: AddParkingModalProps) {
  const queryClient = useQueryClient();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const form = useForm<FormValues>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      name: "",
      code: "",
      hourlyRate: 0,
      capacity: 10,
      address: "",
      latitude: 41.0082,
      longitude: 28.9784,
    },
  });

  // 2. API Bağlantısı (Mutation)
  const mutation = useMutation({
    mutationFn: parkingApi.create,
    onSuccess: () => {
      toast.success("Otopark başarıyla eklendi! 🎉");
      // KRİTİK NOKTA: Listeyi anında yenile
      queryClient.invalidateQueries({ queryKey: ['parking-lots'] });
      form.reset();
      onOpenChange(false);
    },
    onError: (error: any) => {
      console.error("Ekleme Hatası:", error);
      toast.error("Hata oluştu: " + (error.response?.data?.message || "Bilinmeyen hata"));
    },
    onSettled: () => setIsSubmitting(false)
  });

  const onSubmit = (values: FormValues) => {
    setIsSubmitting(true);
    // Backend'e yolla
    mutation.mutate(values);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[500px]">
        <DialogHeader>
          <DialogTitle>Otopark Ekle</DialogTitle>
          <DialogDescription>
            Yeni otopark detaylarını aşağıya giriniz.
          </DialogDescription>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">

            {/* Otopark Adı */}
            <FormField
              control={form.control}
              name="name"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Otopark Adı</FormLabel>
                  <FormControl>
                    <Input placeholder="Örn: Merkez AVM" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="grid grid-cols-2 gap-4">
              {/* Kod */}
              <FormField
                control={form.control}
                name="code"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Kod</FormLabel>
                    <FormControl>
                      <Input placeholder="TR-34" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Kapasite */}
              <FormField
                control={form.control}
                name="capacity"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Kapasite</FormLabel>
                    <FormControl>
                      <Input type="number" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
            </div>

            {/* Fiyat */}
            <FormField
              control={form.control}
              name="hourlyRate"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Saatlik Ücret (TL)</FormLabel>
                  <FormControl>
                    <Input type="number" step="0.5" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            {/* Adres */}
            <FormField
              control={form.control}
              name="address"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Adres</FormLabel>
                  <FormControl>
                    <Input placeholder="Adres giriniz..." {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <DialogFooter>
              <Button type="button" variant="outline" onClick={() => onOpenChange(false)}>
                İptal
              </Button>
              <Button type="submit" disabled={isSubmitting}>
                {isSubmitting ? "Kaydediliyor..." : "Kaydet"}
              </Button>
            </DialogFooter>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}