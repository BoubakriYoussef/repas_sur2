import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PlatDto, PlatRequest } from '../models/plat.dto';

@Injectable({ providedIn: 'root' })
export class PlatApiService {
  private readonly baseUrl = `${environment.apiUrl}/api/plats`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<PlatDto[]> {
    return this.http.get<PlatDto[]>(this.baseUrl);
  }

  create(payload: PlatRequest): Observable<PlatDto> {
    return this.http.post<PlatDto>(this.baseUrl, payload);
  }

  update(id: number, payload: PlatRequest): Observable<PlatDto> {
    return this.http.put<PlatDto>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
