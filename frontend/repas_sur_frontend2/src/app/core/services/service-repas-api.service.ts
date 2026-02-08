import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ServiceRepasDto, ServiceRepasRequest } from '../models/service-repas.dto';

@Injectable({ providedIn: 'root' })
export class ServiceRepasApiService {
  private readonly baseUrl = `${environment.apiUrl}/api/services-repas`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<ServiceRepasDto[]> {
    return this.http.get<ServiceRepasDto[]>(this.baseUrl);
  }

  create(payload: ServiceRepasRequest): Observable<ServiceRepasDto> {
    return this.http.post<ServiceRepasDto>(this.baseUrl, payload);
  }

  update(id: number, payload: ServiceRepasRequest): Observable<ServiceRepasDto> {
    return this.http.put<ServiceRepasDto>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
