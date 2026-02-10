"use client";
import { DataTable } from "@/components/data-table";
import Loading from "@/components/loading";
import { columns } from "@/components/raw-materials/columns";
import CreateRawMaterilForm from "@/components/raw-materials/insert-form";
import SidebarComponent from "@/components/sidebar-component";
import { useAppDispatch, useAppSelector } from "@/lib/store/hooks";
import {
  fetchRawMaterials,
  searchRawMaterials,
} from "@/lib/store/thunks/raw-material.thunks";
import { useEffect, useState } from "react";

export default function RawMaterialsPage() {
  const dispatch = useAppDispatch();
  const [formLoading, setFormLoading] = useState(false);

  const { user } = useAppSelector((state) => state.auth);
  const { items, pagination, loading } = useAppSelector(
    (state) => state.rawMaterial,
  );

  const handlePageChange = (page: number) => {
    dispatch(fetchRawMaterials({ page: page }));
  };

  const handleSearch = (name: string) => {
    dispatch(searchRawMaterials(name));
  };

  const handleSearchReset = () => {
    dispatch(fetchRawMaterials());
  };

  useEffect(() => {
    dispatch(fetchRawMaterials());
  }, [dispatch]);

  if (!user) {
    return (
      <div className="h-screen flex items-center justify-center">
        <Loading />
      </div>
    );
  }

  return (
    <SidebarComponent user={user} link="Inventory" page="Raw Materials">
      <main className="bg-zinc-50 flex-1 pb-10">
        <section className="h-full p-6 bg-zinc-50">
          <div className="flex flex-row items-center justify-between pb-6">
            <h1 className="text-2xl font-semibold">Raw Materials</h1>
            <CreateRawMaterilForm setLoading={setFormLoading} />
          </div>

          <div className="bg-white p-6 rounded-4xl">
            <DataTable
              data={items}
              columns={columns}
              page={pagination.page}
              totalPages={pagination.totalPages}
              onPagination={handlePageChange}
              loading={loading || formLoading}
              placeholder="Filter raw materials..."
              column="name"
              onSearch={handleSearch}
              onResetSearch={handleSearchReset}
            />
          </div>
        </section>
      </main>
    </SidebarComponent>
  );
}
