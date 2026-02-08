import { SiteRestaurationDto } from './site.dto';

export interface UtilisateurDto {
  id: number;
  username: string;
  email?: string | null;
  telephone?: string | null;
  poste?: string | null;
  role: string;
  actif: boolean;
  site: SiteRestaurationDto | null;
}

export interface UtilisateurRequest {
  username: string;
  password?: string | null;
  email?: string | null;
  telephone?: string | null;
  poste?: string | null;
  role: string;
  actif: boolean;
  siteId?: number | null;
}
