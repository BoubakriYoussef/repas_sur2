import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SiteRestaurationDto } from '../models/site.dto';
import { SiteRestaurationRequest } from '../models/site.dto';

@Injectable({ providedIn: 'root' })
export class SiteApiService {
  private readonly baseUrl = `${environment.apiUrl}/api/sites`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<SiteRestaurationDto[]> {
    return this.http.get<SiteRestaurationDto[]>(this.baseUrl);
  }

  create(payload: SiteRestaurationRequest): Observable<SiteRestaurationDto> {
    return this.http.post<SiteRestaurationDto>(this.baseUrl, payload);
  }

  update(id: number, payload: SiteRestaurationRequest): Observable<SiteRestaurationDto> {
    return this.http.put<SiteRestaurationDto>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
