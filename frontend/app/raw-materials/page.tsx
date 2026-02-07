"use client";
import { columns } from "@/components/raw-materials/columns";
import { DataTable } from "@/components/raw-materials/data-table";
import SidebarComponent from "@/components/sidebar-component";
import { Button } from "@/components/ui/button";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { fetchRawMaterials } from "@/store/thunks/raw-material.thunks";
import { Plus } from "lucide-react";
import { useEffect } from "react";

export default function RawMaterials() {
  const dispatch = useAppDispatch();

  const { user } = useAppSelector((state) => state.auth);
  const { items, pagination, loading, error } = useAppSelector(
    (state) => state.rawMaterial,
  );

  const handlePageChange = (page: number) => {
    dispatch(fetchRawMaterials({ page: page }));
  };

  useEffect(() => {
    dispatch(fetchRawMaterials());
  }, [dispatch]);

  console.log(pagination.totalPages);
  console.log(pagination.totalElements);
  console.log(pagination.page);

  if (!user) {
    return (
      <div className="flex h-screen items-center justify-center">
        <div className="text-muted-foreground">Loading...</div>
      </div>
    );
  }

  return (
    <SidebarComponent user={user} link="Inventory" page="Raw Materials">
      <section className="h-full p-6 bg-gray-50">
        <div className="flex flex-row items-center justify-between pb-6">
          <h1 className="text-2xl font-semibold">Raw Materials</h1>
          <Button variant="default">
            <Plus />
            New Raw Material
          </Button>
        </div>

        <div className="py-10">
          {loading ? (
            <div className="flex justify-center">
              <div className="text-muted-foreground">
                Carregando matérias-primas...
              </div>
            </div>
          ) : error ? (
            <div className="flex justify-center">
              <div className="text-red-500">Erro: {error}</div>
            </div>
          ) : (
            <DataTable
              data={items}
              columns={columns}
              page={pagination.page}
              totalPages={pagination.totalPages}
              onPagination={handlePageChange}
            />
          )}
        </div>
      </section>
    </SidebarComponent>
  );
}
