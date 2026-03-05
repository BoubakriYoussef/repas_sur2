import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('login should call API and store token', () => {
    service.login({ username: 'admin', password: 'secret' }).subscribe((res) => {
      expect(res.role).toBe('ADMIN');
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush({ token: 'jwt-token', role: 'ADMIN' });

    expect(localStorage.getItem('safemeal_token')).toBe('jwt-token');
    expect(service.isAuthenticated()).toBeTrue();
  });

  it('logout should remove token', () => {
    localStorage.setItem('safemeal_token', 'jwt-token');
    service.logout();
    expect(service.getToken()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
  });
});

