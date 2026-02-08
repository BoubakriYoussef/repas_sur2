import { IdCodeDto } from './id-code.dto';

export interface PlatDto {
  id: number;
  nom: string;
  categorie?: string | null;
  description?: string | null;
  contientPorc: boolean;
  estVegetarien: boolean;
  allergenes: IdCodeDto[];
}

export interface PlatRequest {
  nom: string;
  categorie?: string | null;
  description?: string | null;
  contientPorc: boolean;
  estVegetarien: boolean;
  allergeneIds?: number[];
}
