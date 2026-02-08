import { IdCodeDto } from './id-code.dto';

export interface AllergeneDto {
  id: number;
  code: string;
  libelle: string;
  description?: string | null;
}

export interface AllergeneRequest {
  code: string;
  libelle: string;
  description?: string | null;
}
