import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ConviveDto, ConviveRequest } from '../models/convive.dto';

@Injectable({ providedIn: 'root' })
export class ConviveApiService {
  private readonly baseUrl = `${environment.apiUrl}/api/convives`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<ConviveDto[]> {
    return this.http.get<ConviveDto[]>(this.baseUrl);
  }

  create(payload: ConviveRequest): Observable<ConviveDto> {
    return this.http.post<ConviveDto>(this.baseUrl, payload);
  }

  update(id: number, payload: ConviveRequest): Observable<ConviveDto> {
    return this.http.put<ConviveDto>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
