import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MenuDto, MenuRequest } from '../models/menu.dto';

@Injectable({ providedIn: 'root' })
export class MenuApiService {
  private readonly baseUrl = `${environment.apiUrl}/api/menus`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<MenuDto[]> {
    return this.http.get<MenuDto[]>(this.baseUrl);
  }

  create(payload: MenuRequest): Observable<MenuDto> {
    return this.http.post<MenuDto>(this.baseUrl, payload);
  }

  update(id: number, payload: MenuRequest): Observable<MenuDto> {
    return this.http.put<MenuDto>(`${this.baseUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
