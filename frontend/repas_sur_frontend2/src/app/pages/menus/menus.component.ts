import { Component } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MenuApiService } from '../../core/services/menu-api.service';
import { PlatApiService } from '../../core/services/plat-api.service';
import { MenuDto } from '../../core/models/menu.dto';
import { PlatDto } from '../../core/models/plat.dto';

@Component({
  selector: 'app-menus',
  standalone: true,
  imports: [NgFor, NgIf, ReactiveFormsModule],
  templateUrl: './menus.component.html'
})
export class MenusComponent {
  menus: MenuDto[] = [];
  plats: PlatDto[] = [];
  editingId: number | null = null;

  form!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly menuApi: MenuApiService,
    private readonly platApi: PlatApiService
  ) {
    this.form = this.fb.nonNullable.group({
      nom: ['', [Validators.required]],
      description: [''],
      platIds: [[] as number[]]
    });
    this.load();
  }

  load(): void {
    this.menuApi.getAll().subscribe({ next: (data) => (this.menus = data) });
    this.platApi.getAll().subscribe({ next: (data) => (this.plats = data) });
  }

  togglePlat(id: number, checked: boolean): void {
    const current = new Set(this.form.value.platIds || []);
    checked ? current.add(id) : current.delete(id);
    this.form.patchValue({ platIds: Array.from(current) });
  }

  isPlatSelected(id: number): boolean {
    return (this.form.value.platIds || []).includes(id);
  }

  submit(): void {
    if (this.form.invalid) {
      return;
    }
    const payload = this.form.getRawValue();
    const request$ = this.editingId
      ? this.menuApi.update(this.editingId, payload)
      : this.menuApi.create(payload);

    request$.subscribe({
      next: () => {
        this.resetForm();
        this.load();
      }
    });
  }

  startEdit(item: MenuDto): void {
    this.editingId = item.id;
    this.form.patchValue({
      nom: item.nom,
      description: item.description ?? '',
      platIds: item.plats.map((plat) => plat.id)
    });
  }

  cancelEdit(): void {
    this.resetForm();
  }

  remove(id: number): void {
    this.menuApi.delete(id).subscribe({ next: () => this.load() });
  }

  formatPlats(item: MenuDto): string {
    return item.plats.map((p) => p.nom).filter(Boolean).join(', ') || '-';
  }

  private resetForm(): void {
    this.editingId = null;
    this.form.reset({ nom: '', description: '', platIds: [] });
  }
}
