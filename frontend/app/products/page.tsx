"use client";
import { DataTable } from "@/components/data-table";
import Loading from "@/components/loading";
import { columnsProducts } from "@/components/products/columns";
import InsertProductForm from "@/components/products/insert-product/insert-form";
import SidebarComponent from "@/components/sidebar-component";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { fetchProducts } from "@/store/thunks/product.thunks";
import { useEffect, useState } from "react";

export default function ProductPage() {
  const dispatch = useAppDispatch();
  const [loading, setLoading] = useState(false);

  const { user } = useAppSelector((state) => state.auth);
  const { items, pagination } = useAppSelector((state) => state.product);

  const handlePageChange = (page: number) => {
    dispatch(fetchProducts({ page: page }));
  };

  useEffect(() => {
    dispatch(fetchProducts());
  }, [dispatch]);

  if (!user) {
    return (
      <div className="h-screen flex items-center justify-center">
        <Loading />
      </div>
    );
  }

  return (
    <SidebarComponent user={user} link="Inventory" page="Products">
      <main className="bg-zinc-50 flex-1 pb-10">
        <section className="h-full p-6 bg-zinc-50">
          <div className="flex flex-row items-center justify-between pb-6">
            <h1 className="text-2xl font-semibold">Products</h1>
            <InsertProductForm setLoading={setLoading} />
          </div>

          <div className="bg-white p-6 rounded-4xl">
            <DataTable
              data={items}
              columns={columnsProducts}
              page={pagination.page}
              totalPages={pagination.totalPages}
              onPagination={handlePageChange}
              loading={loading}
              placeholder="Filter products..."
              column="name"
            />
          </div>
        </section>
      </main>
    </SidebarComponent>
  );
}
