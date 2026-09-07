import { Component } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActionCorrectiveApiService } from '../../core/services/action-corrective-api.service';
import { AlerteApiService } from '../../core/services/alerte-api.service';
import { ActionCorrectiveDto } from '../../core/models/action-corrective.dto';
import { AlerteRisqueDto } from '../../core/models/alerte.dto';
import { UtilisateurDto } from '../../core/models/utilisateur.dto';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-actions-correctives',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe, ReactiveFormsModule],
  templateUrl: './actions-correctives.component.html'
})
export class ActionsCorrectivesComponent {
  actions: ActionCorrectiveDto[] = [];
  filtered: ActionCorrectiveDto[] = [];
  alertes: AlerteRisqueDto[] = [];
  utilisateurs: UtilisateurDto[] = [];
  editingId: number | null = null;
  errorMessage = '';
  typeActions: string[] = [
    'REMPLACER_PLAT',
    'MENU_ALTERNATIF',
    'RETIRER_INGREDIENT',
    'INFORMER_EQUIPE',
    'NETTOYAGE_ZONE'
  ];

  form!: FormGroup;
  filterForm!: FormGroup;

  get alertesDisponibles(): AlerteRisqueDto[] {
    return this.alertes.filter((alerte) =>
      !this.actions.some((action) => action.alerte?.id === alerte.id && action.id !== this.editingId)
    );
  }

  constructor(
    private readonly fb: FormBuilder,
    private readonly actionApi: ActionCorrectiveApiService,
    private readonly alerteApi: AlerteApiService,
    private readonly route: ActivatedRoute
  ) {
    this.form = this.fb.nonNullable.group({
      date: ['', [Validators.required]],
      typeAction: ['REMPLACER_PLAT', [Validators.required]],
      description: [''],
      alerteId: [0, [Validators.required]]
    });

    this.filterForm = this.fb.nonNullable.group({
      alerteId: ['TOUT'],
      utilisateurId: ['TOUT'],
      dateFrom: [''],
      dateTo: ['']
    });
    this.load();
    this.route.queryParamMap.subscribe((params) => {
      const alerteId = params.get('alerteId');
      if (alerteId) {
        const parsed = Number(alerteId);
        if (!Number.isNaN(parsed)) {
          this.form.patchValue({ alerteId: parsed });
          this.filterForm.patchValue({ alerteId });
          this.applyFilters();
        }
      }
    });
  }

  load(): void {
    this.actionApi.getAll().subscribe({
      next: (data) => {
        this.actions = data;
        this.utilisateurs = Array.from(
          new Map(
            data
              .map((action) => action.utilisateur)
              .filter((utilisateur): utilisateur is UtilisateurDto => utilisateur !== null)
              .map((utilisateur) => [utilisateur.id, utilisateur] as const)
          ).values()
        );
        this.applyFilters();
      }
    });
    this.alerteApi.getAll().subscribe({ next: (data) => (this.alertes = data) });
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    const raw = this.form.getRawValue();
    const alerteId = Number(raw.alerteId);
    const alerteDejaTraitee = this.actions.some(
      (action) => action.alerte?.id === alerteId && action.id !== this.editingId
    );
    if (alerteDejaTraitee) {
      this.form.controls['alerteId'].setErrors({ alreadyHasAction: true });
      return;
    }
    const payload = {
      ...raw,
      date: new Date(raw.date).toISOString(),
      alerteId
    };

    const request$ = this.editingId
      ? this.actionApi.update(this.editingId, payload)
      : this.actionApi.create(payload);

    request$.subscribe({
      next: () => {
        this.resetForm();
        this.load();
      }
    });
  }

  startEdit(item: ActionCorrectiveDto): void {
    this.editingId = item.id;
    this.form.patchValue({
      date: this.toInputDate(item.date),
      typeAction: item.typeAction,
      description: item.description ?? '',
      alerteId: item.alerte?.id ?? 0
    });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  remove(id: number): void {
    this.errorMessage = '';
    this.actionApi.delete(id).subscribe({
      next: () => this.load(),
      error: (error: HttpErrorResponse) => {
        this.errorMessage = error.status === 403
          ? "Suppression refusee : le role Cuisine n'est pas autorise a supprimer une action corrective."
          : "Une erreur est survenue pendant la suppression de l'action corrective.";
      }
    });
  }

  applyFilters(): void {
    const { alerteId, utilisateurId, dateFrom, dateTo } = this.filterForm.getRawValue();
    const from = dateFrom ? new Date(`${dateFrom}T00:00:00`) : null;
    const to = dateTo ? new Date(`${dateTo}T23:59:59.999`) : null;
    this.filtered = this.actions.filter((item) => {
      const matchAlerte = alerteId === 'TOUT' || item.alerte?.id === Number(alerteId);
      const matchUtilisateur = utilisateurId === 'TOUT' || item.utilisateur?.id === Number(utilisateurId);
      const itemDate = item.date ? new Date(item.date) : null;
      const matchFrom = !from || (itemDate && itemDate >= from);
      const matchTo = !to || (itemDate && itemDate <= to);
      return matchAlerte && matchUtilisateur && matchFrom && matchTo;
    });
  }

  resetFilters(): void {
    this.filterForm.reset({ alerteId: 'TOUT', utilisateurId: 'TOUT', dateFrom: '', dateTo: '' });
    this.applyFilters();
  }

  private resetForm(): void {
    this.editingId = null;
    this.form.reset({
      date: '',
      typeAction: 'REMPLACER_PLAT',
      description: '',
      alerteId: this.alertesDisponibles[0]?.id ?? 0
    });
  }

  private toInputDate(value: string | Date | null): string {
    if (!value) {
      return '';
    }
    const date = new Date(value);
    return Number.isNaN(date.getTime()) ? '' : date.toISOString().slice(0, 16);
  }
}
