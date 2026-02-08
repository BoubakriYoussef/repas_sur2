import { IdNomDto } from './id-nom.dto';
import { SiteRestaurationDto } from './site.dto';

export interface ServiceRepasDto {
  id: number;
  dateService: string;
  typeRepas: string;
  statut: string;
  site: SiteRestaurationDto | null;
  menu: IdNomDto | null;
}

export interface ServiceRepasRequest {
  dateService: string;
  typeRepas: string;
  statut: string;
  siteId: number;
  menuId?: number | null;
}
