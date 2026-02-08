import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RegimeDto, RegimeRequest } from '../models/regime.dto';

@Injectable({ providedIn: 'root' })
export class RegimeApiService {
  private readonly baseUrl = `${environment.apiUrl}/api/regimes`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<RegimeDto[]> {
    return this.http.get<RegimeDto[]>(this.baseUrl);
  }

  create(payload: RegimeRequest): Observable<RegimeDto> {
    return this.http.post<RegimeDto>(this.baseUrl, payload);
  }

  update(id: number, payload: RegimeRequest): Observable<RegimeDto> {
    return this.http.put<RegimeDto>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
