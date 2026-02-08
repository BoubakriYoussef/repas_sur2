import { Component } from '@angular/core';
import { NgFor, NgIf, DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ServiceRepasApiService } from '../../core/services/service-repas-api.service';
import { SiteApiService } from '../../core/services/site-api.service';
import { MenuApiService } from '../../core/services/menu-api.service';
import { ServiceRepasDto } from '../../core/models/service-repas.dto';
import { SiteRestaurationDto } from '../../core/models/site.dto';
import { MenuDto } from '../../core/models/menu.dto';

@Component({
  selector: 'app-services',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe, ReactiveFormsModule],
  templateUrl: './services.component.html'
})
export class ServicesComponent {
  services: ServiceRepasDto[] = [];
  sites: SiteRestaurationDto[] = [];
  menus: MenuDto[] = [];
  editingId: number | null = null;

  form!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly serviceApi: ServiceRepasApiService,
    private readonly siteApi: SiteApiService,
    private readonly menuApi: MenuApiService
  ) {
    this.form = this.fb.nonNullable.group({
      dateService: ['', [Validators.required]],
      typeRepas: ['DEJEUNER', [Validators.required]],
      statut: ['PREVU', [Validators.required]],
      siteId: [0, [Validators.required]],
      menuId: [null as number | null]
    });
    this.load();
  }

  load(): void {
    this.serviceApi.getAll().subscribe({ next: (data) => (this.services = data) });
    this.siteApi.getAll().subscribe({ next: (data) => (this.sites = data) });
    this.menuApi.getAll().subscribe({ next: (data) => (this.menus = data) });
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    const raw = this.form.getRawValue();
    const menuIdValue = raw.menuId ? Number(raw.menuId) : null;
    const payload = {
      ...raw,
      dateService: new Date(raw.dateService).toISOString(),
      siteId: Number(raw.siteId),
      menuId: menuIdValue
    };
    const request$ = this.editingId
      ? this.serviceApi.update(this.editingId, payload)
      : this.serviceApi.create(payload);

    request$.subscribe({
      next: () => {
        this.resetForm();
        this.load();
      }
    });
  }

  startEdit(item: ServiceRepasDto): void {
    this.editingId = item.id;
    this.form.patchValue({
      dateService: this.toInputDate(item.dateService),
      typeRepas: item.typeRepas,
      statut: item.statut,
      siteId: item.site?.id ?? 0,
      menuId: item.menu?.id ?? null
    });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  remove(id: number): void {
    this.serviceApi.delete(id).subscribe({ next: () => this.load() });
  }

  private resetForm(): void {
    this.editingId = null;
    this.form.reset({
      dateService: '',
      typeRepas: 'DEJEUNER',
      statut: 'PREVU',
      siteId: this.sites[0]?.id ?? 0,
      menuId: null
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
