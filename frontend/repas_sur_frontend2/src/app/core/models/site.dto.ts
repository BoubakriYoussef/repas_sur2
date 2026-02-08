export interface SiteRestaurationDto {
  id: number;
  nom: string;
  type: string;
  adresse?: string | null;
}

export interface SiteRestaurationRequest {
  nom: string;
  type: string;
  adresse?: string | null;
}
