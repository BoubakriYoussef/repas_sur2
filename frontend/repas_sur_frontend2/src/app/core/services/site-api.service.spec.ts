import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { SiteApiService } from './site-api.service';
import { environment } from '../../../environments/environment';

describe('SiteApiService', () => {
  let service: SiteApiService;
  let httpMock: HttpTestingController;
  const baseUrl = `${environment.apiUrl}/api/sites`;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(SiteApiService);
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

    service.update(1, {} as any).subscribe();
    req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({});

    service.delete(1).subscribe();
    req = httpMock.expectOne(`${baseUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});

