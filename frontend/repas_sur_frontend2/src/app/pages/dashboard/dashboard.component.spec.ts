import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';
import { AlerteRisqueDto } from '../../core/models/alerte.dto';
import { ConviveDto } from '../../core/models/convive.dto';
import { MenuDto } from '../../core/models/menu.dto';
import { ServiceRepasDto } from '../../core/models/service-repas.dto';
import { AlerteApiService } from '../../core/services/alerte-api.service';
import { ConviveApiService } from '../../core/services/convive-api.service';
import { MenuApiService } from '../../core/services/menu-api.service';
import { ServiceRepasApiService } from '../../core/services/service-repas-api.service';
import { DashboardComponent } from './dashboard.component';

describe('DashboardComponent', () => {
  let conviveApi: jasmine.SpyObj<ConviveApiService>;
  let serviceApi: jasmine.SpyObj<ServiceRepasApiService>;
  let alerteApi: jasmine.SpyObj<AlerteApiService>;
  let menuApi: jasmine.SpyObj<MenuApiService>;

  const convives: ConviveDto[] = [
    { id: 1, nom: 'Durand', prenom: 'Alice', typeConvive: 'ENFANT_SCOLAIRE', site: null, allergenes: [], regimes: [] },
    { id: 2, nom: 'Martin', prenom: 'Bob', typeConvive: 'ETUDIANT', site: null, allergenes: [], regimes: [] }
  ];

  const services: ServiceRepasDto[] = [
    { id: 1, dateService: '2026-07-18T12:00:00.000Z', typeRepas: 'DEJEUNER', statut: 'PREVU', site: { id: 1, nom: 'Site A', type: 'SCOLAIRE' }, menu: { id: 1, nom: 'Menu A' } },
    { id: 2, dateService: '2026-07-17T08:00:00.000Z', typeRepas: 'DINER', statut: 'PREVU', site: null, menu: null },
    { id: 3, dateService: '', typeRepas: 'DINER', statut: 'ANNULE', site: null, menu: null },
    { id: 4, dateService: '2026-07-19T18:00:00.000Z', typeRepas: 'DINER', statut: 'SERVI', site: null, menu: null },
    { id: 5, dateService: '2026-07-20T18:00:00.000Z', typeRepas: 'DINER', statut: 'SERVI', site: null, menu: null },
    { id: 6, dateService: '2026-07-21T18:00:00.000Z', typeRepas: 'DINER', statut: 'SERVI', site: null, menu: null }
  ];

  const alertes: AlerteRisqueDto[] = [
    { id: 1, etat: 'NOUVELLE', niveau: 'FAIBLE', message: 'Trace', dateCreation: '2026-07-17T10:00:00.000Z', convive: { id: 1, nom: 'Alice' }, service: null, allergenes: [] },
    { id: 2, etat: 'EN_COURS', niveau: 'FORT', message: 'Critique', dateCreation: '2026-07-17T11:00:00.000Z', convive: { id: 2, nom: 'Bob' }, service: null, allergenes: [] },
    { id: 3, etat: 'RESOLUE', niveau: 'MOYEN', message: 'Fermee', dateCreation: '2026-07-16T11:00:00.000Z', convive: null, service: null, allergenes: [] },
    { id: 4, etat: 'NOUVELLE', niveau: 'INCONNU', message: 'Divers', dateCreation: '2026-07-15T11:00:00.000Z', convive: null, service: null, allergenes: [] }
  ];

  const menus: MenuDto[] = [
    { id: 1, nom: 'Menu A', description: null, plats: [] },
    { id: 2, nom: 'Menu B', description: null, plats: [] }
  ];

  async function createComponent(): Promise<ComponentFixture<DashboardComponent>> {
    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: ConviveApiService, useValue: conviveApi },
        { provide: ServiceRepasApiService, useValue: serviceApi },
        { provide: AlerteApiService, useValue: alerteApi },
        { provide: MenuApiService, useValue: menuApi }
      ]
    }).compileComponents();

    const fixture = TestBed.createComponent(DashboardComponent);
    fixture.detectChanges();
    return fixture;
  }

  beforeEach(() => {
    conviveApi = jasmine.createSpyObj<ConviveApiService>('ConviveApiService', ['getAll']);
    serviceApi = jasmine.createSpyObj<ServiceRepasApiService>('ServiceRepasApiService', ['getAll']);
    alerteApi = jasmine.createSpyObj<AlerteApiService>('AlerteApiService', ['getAll']);
    menuApi = jasmine.createSpyObj<MenuApiService>('MenuApiService', ['getAll']);

    conviveApi.getAll.and.returnValue(of(convives));
    serviceApi.getAll.and.returnValue(of(services));
    alerteApi.getAll.and.returnValue(of(alertes));
    menuApi.getAll.and.returnValue(of(menus));
  });

  it('charge les donnees au demarrage et calcule les indicateurs', async () => {
    const fixture = await createComponent();
    const component = fixture.componentInstance;

    expect(conviveApi.getAll).toHaveBeenCalled();
    expect(serviceApi.getAll).toHaveBeenCalled();
    expect(alerteApi.getAll).toHaveBeenCalled();
    expect(menuApi.getAll).toHaveBeenCalled();
    expect(component.stats).toEqual([
      { label: 'Convives actifs', value: '2', trend: 'Total' },
      { label: 'Services planifies', value: '6', trend: 'Total' },
      { label: 'Alertes ouvertes', value: '3', trend: 'En cours' },
      { label: 'Menus en base', value: '2', trend: 'Total' }
    ]);
    expect(component.nextServices.map((item) => item.id)).toEqual([2, 1, 4, 5]);
    expect(component.criticalAlerts.map((item) => item.id)).toEqual([2, 3, 1]);
    expect(fixture.nativeElement.textContent).toContain('Alertes critiques');
  });

  it('affiche les etats vides quand il n y a ni services dates ni alertes', async () => {
    serviceApi.getAll.and.returnValue(of([]));
    alerteApi.getAll.and.returnValue(of([]));
    menuApi.getAll.and.returnValue(of([]));
    conviveApi.getAll.and.returnValue(of([]));

    const fixture = await createComponent();
    const text = fixture.nativeElement.textContent as string;

    expect(text).toContain('Aucun service planifie');
    expect(text).toContain('Aucune alerte');
  });

  it('laisse les tableaux a vide si une API echoue', async () => {
    conviveApi.getAll.and.returnValue(throwError(() => new Error('boom')));
    serviceApi.getAll.and.returnValue(of([]));
    alerteApi.getAll.and.returnValue(of([]));
    menuApi.getAll.and.returnValue(of([]));

    const fixture = await createComponent();
    const component = fixture.componentInstance;

    expect(component.stats).toEqual([]);
    expect(component.nextServices).toEqual([]);
    expect(component.criticalAlerts).toEqual([]);
  });
});
