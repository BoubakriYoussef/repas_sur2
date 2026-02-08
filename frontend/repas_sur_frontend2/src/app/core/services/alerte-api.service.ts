import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AlerteRisqueDto, AlerteRisqueRequest, AlerteEtatUpdateRequest } from '../models/alerte.dto';

@Injectable({ providedIn: 'root' })
export class AlerteApiService {
  private readonly baseUrl = `${environment.apiUrl}/api/alertes`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<AlerteRisqueDto[]> {
    return this.http.get<AlerteRisqueDto[]>(this.baseUrl);
  }

  create(payload: AlerteRisqueRequest): Observable<AlerteRisqueDto> {
    return this.http.post<AlerteRisqueDto>(this.baseUrl, payload);
  }

  update(id: number, payload: AlerteRisqueRequest): Observable<AlerteRisqueDto> {
    return this.http.put<AlerteRisqueDto>(`${this.baseUrl}/${id}`, payload);
  }

  updateEtat(id: number, payload: AlerteEtatUpdateRequest): Observable<AlerteRisqueDto> {
    return this.http.post<AlerteRisqueDto>(`${this.baseUrl}/${id}/etat`, payload);
  }

  generer(serviceId: number): Observable<AlerteRisqueDto[]> {
    return this.http.post<AlerteRisqueDto[]>(`${this.baseUrl}/generer/${serviceId}`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
