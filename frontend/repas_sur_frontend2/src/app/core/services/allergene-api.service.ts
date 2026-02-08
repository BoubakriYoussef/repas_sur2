import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AllergeneDto, AllergeneRequest } from '../models/allergene.dto';

@Injectable({ providedIn: 'root' })
export class AllergeneApiService {
  private readonly baseUrl = `${environment.apiUrl}/api/allergenes`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<AllergeneDto[]> {
    return this.http.get<AllergeneDto[]>(this.baseUrl);
  }

  create(payload: AllergeneRequest): Observable<AllergeneDto> {
    return this.http.post<AllergeneDto>(this.baseUrl, payload);
  }

  update(id: number, payload: AllergeneRequest): Observable<AllergeneDto> {
    return this.http.put<AllergeneDto>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
