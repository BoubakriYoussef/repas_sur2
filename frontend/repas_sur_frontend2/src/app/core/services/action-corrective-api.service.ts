import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { ActionCorrectiveDto, ActionCorrectiveRequest } from '../models/action-corrective.dto';

@Injectable({ providedIn: 'root' })
export class ActionCorrectiveApiService {
  private readonly baseUrl = `${environment.apiUrl}/api/actions-correctives`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<ActionCorrectiveDto[]> {
    return this.http.get<ActionCorrectiveDto[]>(this.baseUrl);
  }

  create(payload: ActionCorrectiveRequest): Observable<ActionCorrectiveDto> {
    return this.http.post<ActionCorrectiveDto>(this.baseUrl, payload);
  }

  update(id: number, payload: ActionCorrectiveRequest): Observable<ActionCorrectiveDto> {
    return this.http.put<ActionCorrectiveDto>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
