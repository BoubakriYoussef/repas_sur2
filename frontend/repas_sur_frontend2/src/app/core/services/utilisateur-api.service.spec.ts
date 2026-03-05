import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UtilisateurApiService } from './utilisateur-api.service';
import { environment } from '../../../environments/environment';

describe('UtilisateurApiService', () => {
  let service: UtilisateurApiService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiUrl}/api/utilisateurs`;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(UtilisateurApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('getAll should call GET /api/utilisateurs', () => {
    service.getAll().subscribe();
    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('create should call POST /api/utilisateurs', () => {
    service.create({} as any).subscribe();
    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('update should call PUT /api/utilisateurs/:id', () => {
    service.update(3, {} as any).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/3`);
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });

  it('delete should call DELETE /api/utilisateurs/:id', () => {
    service.delete(4).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/4`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});

