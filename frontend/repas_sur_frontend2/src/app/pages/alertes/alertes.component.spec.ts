import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { AlerteRisqueDto } from '../../core/models/alerte.dto';
import { ServiceRepasDto } from '../../core/models/service-repas.dto';
import { AlerteApiService } from '../../core/services/alerte-api.service';
import { ServiceRepasApiService } from '../../core/services/service-repas-api.service';
import { AlertesComponent } from './alertes.component';

describe('AlertesComponent', () => {
  let fixture: ComponentFixture<AlertesComponent>;
  let component: AlertesComponent;
  let alerteApi: jasmine.SpyObj<AlerteApiService>;
  let serviceApi: jasmine.SpyObj<ServiceRepasApiService>;
  let router: Router;
  let navigateSpy: jasmine.Spy;

  const services: ServiceRepasDto[] = [
    { id: 1, dateService: '2026-07-17T12:00:00.000Z', typeRepas: 'DEJEUNER', statut: 'PREVU', site: null, menu: { id: 1, nom: 'Menu A' } }
  ];
  const alertes: AlerteRisqueDto[] = [
    { id: 1, etat: 'NOUVELLE', niveau: 'FORT', message: 'Critique', dateCreation: '2026-07-17T10:00:00.000Z', convive: { id: 1, nom: 'Alice' }, service: services[0], allergenes: [] },
    { id: 2, etat: 'RESOLUE', niveau: 'FAIBLE', message: 'Fermee', dateCreation: '2026-07-10T10:00:00.000Z', convive: null, service: null, allergenes: [] }
  ];

  beforeEach(async () => {
    alerteApi = jasmine.createSpyObj<AlerteApiService>('AlerteApiService', ['getAll', 'generer', 'updateEtat']);
    serviceApi = jasmine.createSpyObj<ServiceRepasApiService>('ServiceRepasApiService', ['getAll']);
    alerteApi.getAll.and.returnValue(of(alertes));
    alerteApi.generer.and.returnValue(of([]));
    alerteApi.updateEtat.and.returnValue(of(alertes[0]));
    serviceApi.getAll.and.returnValue(of(services));

    await TestBed.configureTestingModule({
      imports: [AlertesComponent],
      providers: [
        provideRouter([]),
        { provide: AlerteApiService, useValue: alerteApi },
        { provide: ServiceRepasApiService, useValue: serviceApi }
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    navigateSpy = spyOn(router, 'navigate').and.resolveTo(true);
    fixture = TestBed.createComponent(AlertesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('charge les alertes et les services puis affiche les donnees', () => {
    expect(component.alertes).toEqual(alertes);
    expect(component.filtered).toEqual(alertes);
    expect(component.services).toEqual(services);
    expect(fixture.nativeElement.textContent).toContain('Critique');
  });

  it('ne genere pas si le formulaire est invalide', () => {
    component.form.patchValue({ serviceId: null as unknown as number });
    component.generer();
    expect(alerteApi.generer).not.toHaveBeenCalled();
  });

  it('genere les alertes avec le service selectionne', () => {
    component.form.patchValue({ serviceId: '1' as unknown as number });
    component.generer();
    expect(alerteApi.generer).toHaveBeenCalledWith(1);
  });

  it('met a jour l etat puis recharge', () => {
    component.updateEtat(1, 'EN_COURS');
    expect(alerteApi.updateEtat).toHaveBeenCalledWith(1, { etat: 'EN_COURS' });
  });

  it('inclut toute la journee selectionnee dans les bornes de dates puis reinitialise', () => {
    component.filterForm.setValue({ etat: 'NOUVELLE', niveau: 'FORT', dateFrom: '2026-07-17', dateTo: '2026-07-17' });
    component.applyFilters();
    expect(component.filtered).toEqual([alertes[0]]);

    component.resetFilters();
    expect(component.filterForm.getRawValue()).toEqual({ etat: 'TOUT', niveau: 'TOUT', dateFrom: '', dateTo: '' });
    expect(component.filtered.length).toBe(2);
  });

  it('n inclut pas une alerte sans date si un filtre de date est actif', () => {
    component.alertes = [{ ...alertes[0], dateCreation: '' }];
    component.filterForm.patchValue({ dateFrom: '2026-07-17' });
    component.applyFilters();
    expect(component.filtered).toEqual([]);
  });

  it('navigue vers les actions correctives avec le bon query param', () => {
    component.goToAction(9);
    expect(navigateSpy).toHaveBeenCalledWith(['/actions-correctives'], { queryParams: { alerteId: 9 } });
  });
});
