import { IdCodeDto } from './id-code.dto';
import { IdNomDto } from './id-nom.dto';
import { ServiceRepasDto } from './service-repas.dto';

export interface AlerteRisqueDto {
  id: number;
  etat: string;
  niveau: string;
  message?: string | null;
  dateCreation: string;
  convive: IdNomDto | null;
  service: ServiceRepasDto | null;
  allergenes: IdCodeDto[];
}

export interface AlerteRisqueRequest {
  etat: string;
  niveau: string;
  message?: string | null;
  dateCreation?: string | null;
  conviveId: number;
  serviceId: number;
  allergeneIds?: number[];
}

export interface AlerteEtatUpdateRequest {
  etat: string;
}
