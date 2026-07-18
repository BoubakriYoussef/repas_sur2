import { HttpClient, HttpErrorResponse, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { authInterceptor } from './auth.interceptor';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting()
      ]
    });
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('ajoute le header Authorization quand un token existe', () => {
    localStorage.setItem('safemeal_token', 'jwt-token');

    http.post('/api/test', { label: 'x' }).subscribe();

    const req = httpMock.expectOne('/api/test');
    expect(req.request.headers.get('Authorization')).toBe('Bearer jwt-token');
    expect(req.request.method).toBe('POST');
    expect(req.request.url).toBe('/api/test');
    expect(req.request.body).toEqual({ label: 'x' });
    req.flush({});
  });

  it('ne modifie pas la requete quand aucun token n existe', () => {
    http.get('/api/test').subscribe();

    const req = httpMock.expectOne('/api/test');
    expect(req.request.headers.has('Authorization')).toBeFalse();
    expect(req.request.method).toBe('GET');
    req.flush({});
  });

  it('laisse passer une reponse 401 sans la transformer', () => {
    let actualError: HttpErrorResponse | undefined;

    http.get('/api/secure').subscribe({
      error: (error: HttpErrorResponse) => {
        actualError = error;
      }
    });

    const req = httpMock.expectOne('/api/secure');
    req.flush({ message: 'unauthorized' }, { status: 401, statusText: 'Unauthorized' });

    expect(actualError?.status).toBe(401);
    expect(actualError?.error).toEqual({ message: 'unauthorized' });
  });

  it('laisse passer une reponse 403 sans la transformer', () => {
    let actualError: HttpErrorResponse | undefined;

    http.get('/api/secure').subscribe({
      error: (error: HttpErrorResponse) => {
        actualError = error;
      }
    });

    const req = httpMock.expectOne('/api/secure');
    req.flush({ message: 'forbidden' }, { status: 403, statusText: 'Forbidden' });

    expect(actualError?.status).toBe(403);
    expect(actualError?.error).toEqual({ message: 'forbidden' });
  });
});
