export interface ProductionCapacityItem {
  productId: number;
  productCode: string;
  productName: string;
  unitPrice: number;
  producibleQuantity: number;
  totalValue: number;
}

export interface ProductionCapacity {
  items: ProductionCapacityItem[];
  grandTotalValue: number;
}
