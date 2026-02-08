import { Component } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { UtilisateurApiService } from '../../core/services/utilisateur-api.service';
import { SiteApiService } from '../../core/services/site-api.service';
import { UtilisateurDto } from '../../core/models/utilisateur.dto';
import { SiteRestaurationDto } from '../../core/models/site.dto';

@Component({
  selector: 'app-utilisateurs',
  standalone: true,
  imports: [NgFor, NgIf, ReactiveFormsModule],
  templateUrl: './utilisateurs.component.html'
})
export class UtilisateursComponent {
  utilisateurs: UtilisateurDto[] = [];
  filtered: UtilisateurDto[] = [];
  sites: SiteRestaurationDto[] = [];
  editingId: number | null = null;

  form!: FormGroup;
  filterForm!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly utilisateurApi: UtilisateurApiService,
    private readonly siteApi: SiteApiService
  ) {
    this.form = this.fb.nonNullable.group({
      username: ['', [Validators.required]],
      password: [''],
      email: [''],
      telephone: [''],
      poste: [''],
      role: ['RESPONSABLE', [Validators.required]],
      actif: [true],
      siteId: [null as number | null]
    });

    this.filterForm = this.fb.nonNullable.group({
      role: ['TOUT'],
      actif: ['TOUT']
    });
    this.load();
  }

  load(): void {
    this.utilisateurApi.getAll().subscribe({
      next: (data) => {
        this.utilisateurs = data;
        this.applyFilters();
      }
    });
    this.siteApi.getAll().subscribe({ next: (data) => (this.sites = data) });
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    const raw = this.form.getRawValue();
    const payload = {
      ...raw,
      siteId: raw.siteId ? Number(raw.siteId) : null,
      password: raw.password && raw.password.trim().length > 0 ? raw.password : null
    };

    const request$ = this.editingId
      ? this.utilisateurApi.update(this.editingId, payload)
      : this.utilisateurApi.create(payload);

    request$.subscribe({
      next: () => {
        this.resetForm();
        this.load();
      }
    });
  }

  startEdit(user: UtilisateurDto): void {
    this.editingId = user.id;
    this.form.patchValue({
      username: user.username,
      password: '',
      email: user.email ?? '',
      telephone: user.telephone ?? '',
      poste: user.poste ?? '',
      role: user.role,
      actif: user.actif,
      siteId: user.site?.id ?? null
    });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  remove(id: number): void {
    this.utilisateurApi.delete(id).subscribe({ next: () => this.load() });
  }

  applyFilters(): void {
    const { role, actif } = this.filterForm.getRawValue();
    this.filtered = this.utilisateurs.filter((user) => {
      const matchRole = role === 'TOUT' || user.role === role;
      const matchActif =
        actif === 'TOUT' || (actif === 'ACTIF' ? user.actif : !user.actif);
      return matchRole && matchActif;
    });
  }

  resetFilters(): void {
    this.filterForm.reset({ role: 'TOUT', actif: 'TOUT' });
    this.applyFilters();
  }

  private resetForm(): void {
    this.editingId = null;
    this.form.reset({
      username: '',
      password: '',
      email: '',
      telephone: '',
      poste: '',
      role: 'RESPONSABLE',
      actif: true,
      siteId: null
    });
  }
}
