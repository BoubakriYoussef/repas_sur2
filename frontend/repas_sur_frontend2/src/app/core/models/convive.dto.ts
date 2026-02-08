import { IdCodeDto } from './id-code.dto';
import { RegimeDto } from './regime.dto';
import { SiteRestaurationDto } from './site.dto';

export interface ConviveDto {
  id: number;
  nom: string;
  prenom: string;
  typeConvive: string;
  site: SiteRestaurationDto | null;
  allergenes: IdCodeDto[];
  regimes: RegimeDto[];
}

export interface ConviveRequest {
  nom: string;
  prenom: string;
  typeConvive: string;
  siteId: number;
  allergeneIds?: number[];
  regimeIds?: number[];
}
