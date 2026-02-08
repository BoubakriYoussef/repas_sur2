import { IdNomDto } from './id-nom.dto';

export interface MenuDto {
  id: number;
  nom: string;
  description?: string | null;
  plats: IdNomDto[];
}

export interface MenuRequest {
  nom: string;
  description?: string | null;
  platIds?: number[];
}
