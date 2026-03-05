import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ServiceRepasApiService } from './service-repas-api.service';
import { environment } from '../../../environments/environment';

describe('ServiceRepasApiService', () => {
  let service: ServiceRepasApiService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiUrl}/api/services-repas`;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(ServiceRepasApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('should call CRUD endpoints', () => {
    service.getAll().subscribe();
    let req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('GET');
    req.flush([]);

    service.create({} as any).subscribe();
    req = httpMock.expectOne(baseUrl);
    expect(req.request.method).toBe('POST');
    req.flush({});

    service.update(2, {} as any).subscribe();
    req = httpMock.expectOne(`${baseUrl}/2`);
    expect(req.request.method).toBe('PUT');
    req.flush({});

    service.delete(2).subscribe();
    req = httpMock.expectOne(`${baseUrl}/2`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});

