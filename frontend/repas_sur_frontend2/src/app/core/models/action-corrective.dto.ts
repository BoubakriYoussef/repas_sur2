import { IdNomDto } from './id-nom.dto';
import { UtilisateurDto } from './utilisateur.dto';

export interface ActionCorrectiveDto {
  id: number;
  date: string;
  typeAction: string;
  description?: string | null;
  alerte: IdNomDto | null;
  convive?: IdNomDto | null;
  utilisateur: UtilisateurDto | null;
}

export interface ActionCorrectiveRequest {
  date: string;
  typeAction: string;
  description?: string | null;
  alerteId: number;
  utilisateurId: number;
}
