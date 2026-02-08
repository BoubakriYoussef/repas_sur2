export interface RegimeDto {
  id: number;
  code: string;
  libelle: string;
  type: string;
  description?: string | null;
}

export interface RegimeRequest {
  code: string;
  libelle: string;
  type: string;
  description?: string | null;
}
