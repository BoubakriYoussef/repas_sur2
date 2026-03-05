import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AlerteApiService } from './alerte-api.service';
import { environment } from '../../../environments/environment';

describe('AlerteApiService', () => {
  let service: AlerteApiService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiUrl}/api/alertes`;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(AlerteApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('getAll should call GET /api/alertes', () => {
    service.getAll().subscribe();
    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush([]);
  });

  it('create should call POST /api/alertes', () => {
    service.create({} as any).subscribe();
    const req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('update should call PUT /api/alertes/:id', () => {
    service.update(3, {} as any).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/3`);
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });

  it('updateEtat should call POST /api/alertes/:id/etat', () => {
    service.updateEtat(5, { etat: 'VUE' } as any).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/5/etat`);
    expect(req.request.method).toBe('POST');
    req.flush({});
  });

  it('generer should call POST /api/alertes/generer/:serviceId', () => {
    service.generer(8).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/generer/8`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    req.flush([]);
  });

  it('delete should call DELETE /api/alertes/:id', () => {
    service.delete(9).subscribe();
    const req = httpMock.expectOne(`${baseUrl}/9`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});

