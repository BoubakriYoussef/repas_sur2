import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { BehaviorSubject, of } from 'rxjs';
import { ActionCorrectiveDto } from '../../core/models/action-corrective.dto';
import { AlerteRisqueDto } from '../../core/models/alerte.dto';
import { UtilisateurDto } from '../../core/models/utilisateur.dto';
import { ActionCorrectiveApiService } from '../../core/services/action-corrective-api.service';
import { AlerteApiService } from '../../core/services/alerte-api.service';
import { UtilisateurApiService } from '../../core/services/utilisateur-api.service';
import { ActionsCorrectivesComponent } from './actions-correctives.component';

describe('ActionsCorrectivesComponent', () => {
  let fixture: ComponentFixture<ActionsCorrectivesComponent>;
  let component: ActionsCorrectivesComponent;
  let actionApi: jasmine.SpyObj<ActionCorrectiveApiService>;
  let alerteApi: jasmine.SpyObj<AlerteApiService>;
  let utilisateurApi: jasmine.SpyObj<UtilisateurApiService>;
  let queryParamMap$: BehaviorSubject<ReturnType<typeof convertToParamMap>>;

  const alertes: AlerteRisqueDto[] = [
    { id: 1, etat: 'NOUVELLE', niveau: 'FORT', message: 'Critique', dateCreation: '2026-07-17T10:00:00.000Z', convive: { id: 4, nom: 'Alice' }, service: null, allergenes: [] }
  ];
  const utilisateurs: UtilisateurDto[] = [
    { id: 2, username: 'admin', email: null, telephone: null, poste: null, role: 'ADMIN', actif: true, site: null }
  ];
  const actions: ActionCorrectiveDto[] = [
    { id: 6, date: '2026-07-17T10:00:00.000Z', typeAction: 'REMPLACER_PLAT', description: 'Action', alerte: { id: 1, nom: 'Alerte 1' }, convive: { id: 4, nom: 'Alice' }, utilisateur: utilisateurs[0] }
  ];

  beforeEach(async () => {
    queryParamMap$ = new BehaviorSubject(convertToParamMap({}));
    actionApi = jasmine.createSpyObj<ActionCorrectiveApiService>('ActionCorrectiveApiService', ['getAll', 'create', 'update', 'delete']);
    alerteApi = jasmine.createSpyObj<AlerteApiService>('AlerteApiService', ['getAll']);
    utilisateurApi = jasmine.createSpyObj<UtilisateurApiService>('UtilisateurApiService', ['getAll']);
    actionApi.getAll.and.returnValue(of(actions));
    actionApi.create.and.returnValue(of(actions[0]));
    actionApi.update.and.returnValue(of(actions[0]));
    actionApi.delete.and.returnValue(of(void 0));
    alerteApi.getAll.and.returnValue(of(alertes));
    utilisateurApi.getAll.and.returnValue(of(utilisateurs));

    await TestBed.configureTestingModule({
      imports: [ActionsCorrectivesComponent],
      providers: [
        provideRouter([]),
        { provide: ActionCorrectiveApiService, useValue: actionApi },
        { provide: AlerteApiService, useValue: alerteApi },
        { provide: UtilisateurApiService, useValue: utilisateurApi },
        { provide: ActivatedRoute, useValue: { queryParamMap: queryParamMap$.asObservable() } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ActionsCorrectivesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('charge les actions, alertes et utilisateurs', () => {
    expect(component.actions).toEqual(actions);
    expect(component.filtered).toEqual(actions);
    expect(component.alertes).toEqual(alertes);
    expect(component.utilisateurs).toEqual(utilisateurs);
    expect(fixture.nativeElement.textContent).toContain('REMPLACER_PLAT');
  });

  it('preselectionne l alerte depuis la query string valide', () => {
    queryParamMap$.next(convertToParamMap({ alerteId: '1' }));
    fixture.detectChanges();

    expect(component.form.getRawValue().alerteId).toBe(1);
    expect(component.filterForm.getRawValue().alerteId).toBe('1');
  });

  it('ignore une query string invalide', () => {
    queryParamMap$.next(convertToParamMap({ alerteId: 'abc' }));
    fixture.detectChanges();

    expect(component.form.getRawValue().alerteId).not.toBeNaN();
  });

  it('bloque la soumission invalide', () => {
    component.submit();
    expect(actionApi.create).not.toHaveBeenCalled();
  });

  it('cree puis met a jour une action corrective', () => {
    component.form.setValue({
      date: '2026-07-17T10:00',
      typeAction: 'REMPLACER_PLAT',
      description: 'Action',
      alerteId: '1' as unknown as number,
      utilisateurId: '2' as unknown as number
    });
    component.submit();
    expect(actionApi.create).toHaveBeenCalledWith({
      date: new Date('2026-07-17T10:00').toISOString(),
      typeAction: 'REMPLACER_PLAT',
      description: 'Action',
      alerteId: 1,
      utilisateurId: 2
    });

    component.startEdit(actions[0]);
    component.form.patchValue({ description: 'Action 2' });
    component.submit();
    expect(actionApi.update).toHaveBeenCalledWith(6, jasmine.objectContaining({ description: 'Action 2' }));
  });

  it('filtre par alerte, utilisateur et dates puis reinitialise', () => {
    component.filterForm.setValue({ alerteId: '1', utilisateurId: '2', dateFrom: '2026-07-17', dateTo: '2026-07-18' });
    component.applyFilters();
    expect(component.filtered).toEqual(actions);

    component.resetFilters();
    expect(component.filterForm.getRawValue()).toEqual({ alerteId: 'TOUT', utilisateurId: 'TOUT', dateFrom: '', dateTo: '' });
  });

  it('gere les dates invalides dans le formulaire et la suppression', () => {
    component.startEdit({ ...actions[0], date: 'bad-date' });
    expect(component.form.getRawValue().date).toBe('');

    component.cancelEdit();
    expect(component.form.getRawValue()).toEqual({
      date: '',
      typeAction: 'REMPLACER_PLAT',
      description: '',
      alerteId: 1,
      utilisateurId: 2
    });

    component.remove(6);
    expect(actionApi.delete).toHaveBeenCalledWith(6);
  });
});
