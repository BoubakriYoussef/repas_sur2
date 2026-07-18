import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { MenuDto } from '../../core/models/menu.dto';
import { ServiceRepasDto } from '../../core/models/service-repas.dto';
import { SiteRestaurationDto } from '../../core/models/site.dto';
import { MenuApiService } from '../../core/services/menu-api.service';
import { ServiceRepasApiService } from '../../core/services/service-repas-api.service';
import { SiteApiService } from '../../core/services/site-api.service';
import { ServicesComponent } from './services.component';

describe('ServicesComponent', () => {
  let fixture: ComponentFixture<ServicesComponent>;
  let component: ServicesComponent;
  let serviceApi: jasmine.SpyObj<ServiceRepasApiService>;
  let siteApi: jasmine.SpyObj<SiteApiService>;
  let menuApi: jasmine.SpyObj<MenuApiService>;

  const sites: SiteRestaurationDto[] = [{ id: 1, nom: 'Site A', type: 'SCOLAIRE', adresse: null }];
  const menus: MenuDto[] = [{ id: 2, nom: 'Menu A', description: null, plats: [] }];
  const services: ServiceRepasDto[] = [
    { id: 8, dateService: '2026-07-17T12:00:00.000Z', typeRepas: 'DEJEUNER', statut: 'PREVU', site: sites[0], menu: { id: 2, nom: 'Menu A' } }
  ];

  beforeEach(async () => {
    serviceApi = jasmine.createSpyObj<ServiceRepasApiService>('ServiceRepasApiService', ['getAll', 'create', 'update', 'delete']);
    siteApi = jasmine.createSpyObj<SiteApiService>('SiteApiService', ['getAll']);
    menuApi = jasmine.createSpyObj<MenuApiService>('MenuApiService', ['getAll']);
    serviceApi.getAll.and.returnValue(of(services));
    serviceApi.create.and.returnValue(of(services[0]));
    serviceApi.update.and.returnValue(of(services[0]));
    serviceApi.delete.and.returnValue(of(void 0));
    siteApi.getAll.and.returnValue(of(sites));
    menuApi.getAll.and.returnValue(of(menus));

    await TestBed.configureTestingModule({
      imports: [ServicesComponent],
      providers: [
        { provide: ServiceRepasApiService, useValue: serviceApi },
        { provide: SiteApiService, useValue: siteApi },
        { provide: MenuApiService, useValue: menuApi }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ServicesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('charge les services, sites et menus', () => {
    expect(component.services).toEqual(services);
    expect(component.sites).toEqual(sites);
    expect(component.menus).toEqual(menus);
    expect(fixture.nativeElement.textContent).toContain('Menu A');
  });

  it('bloque la soumission invalide', () => {
    component.submit();
    expect(serviceApi.create).not.toHaveBeenCalled();
  });

  it('cree un service avec les conversions attendues', () => {
    component.form.setValue({
      dateService: '2026-07-17T12:00',
      typeRepas: 'DEJEUNER',
      statut: 'PREVU',
      siteId: '1' as unknown as number,
      menuId: '2' as unknown as number
    });

    component.submit();

    expect(serviceApi.create).toHaveBeenCalledWith({
      dateService: new Date('2026-07-17T12:00').toISOString(),
      typeRepas: 'DEJEUNER',
      statut: 'PREVU',
      siteId: 1,
      menuId: 2
    });
  });

  it('passe en edition puis met a jour', () => {
    component.startEdit(services[0]);
    expect(component.editingId).toBe(8);
    expect(component.form.getRawValue().dateService).toContain('2026-07-17T12:00');

    component.form.patchValue({ statut: 'SERVI' });
    component.submit();

    expect(serviceApi.update).toHaveBeenCalledWith(8, jasmine.objectContaining({ statut: 'SERVI' }));
  });

  it('annule l edition, gere les dates invalides et supprime', () => {
    component.startEdit({ ...services[0], dateService: 'bad-date' });
    expect(component.form.getRawValue().dateService).toBe('');

    component.cancelEdit();
    expect(component.form.getRawValue()).toEqual({
      dateService: '',
      typeRepas: 'DEJEUNER',
      statut: 'PREVU',
      siteId: 1,
      menuId: null
    });

    component.remove(8);
    expect(serviceApi.delete).toHaveBeenCalledWith(8);
  });
});
