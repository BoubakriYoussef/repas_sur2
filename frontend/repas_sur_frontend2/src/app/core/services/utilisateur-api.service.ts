import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { UtilisateurDto, UtilisateurRequest } from '../models/utilisateur.dto';

@Injectable({ providedIn: 'root' })
export class UtilisateurApiService {
  private readonly baseUrl = `${environment.apiUrl}/api/utilisateurs`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<UtilisateurDto[]> {
    return this.http.get<UtilisateurDto[]>(this.baseUrl);
  }

  create(payload: UtilisateurRequest): Observable<UtilisateurDto> {
    return this.http.post<UtilisateurDto>(this.baseUrl, payload);
  }

  update(id: number, payload: UtilisateurRequest): Observable<UtilisateurDto> {
    return this.http.put<UtilisateurDto>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
