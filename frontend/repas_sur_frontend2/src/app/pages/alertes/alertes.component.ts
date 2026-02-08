import { Component } from '@angular/core';
import { NgFor, DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AlerteApiService } from '../../core/services/alerte-api.service';
import { ServiceRepasApiService } from '../../core/services/service-repas-api.service';
import { AlerteRisqueDto } from '../../core/models/alerte.dto';
import { ServiceRepasDto } from '../../core/models/service-repas.dto';
import { Router } from '@angular/router';

@Component({
  selector: 'app-alertes',
  standalone: true,
  imports: [NgFor, DatePipe, ReactiveFormsModule],
  templateUrl: './alertes.component.html'
})
export class AlertesComponent {
  alertes: AlerteRisqueDto[] = [];
  filtered: AlerteRisqueDto[] = [];
  services: ServiceRepasDto[] = [];

  form!: FormGroup;
  filterForm!: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly alerteApi: AlerteApiService,
    private readonly serviceApi: ServiceRepasApiService,
    private readonly router: Router
  ) {
    this.form = this.fb.nonNullable.group({
      serviceId: [0, [Validators.required]]
    });

    this.filterForm = this.fb.nonNullable.group({
      etat: ['TOUT'],
      niveau: ['TOUT'],
      dateFrom: [''],
      dateTo: ['']
    });
    this.load();
  }

  load(): void {
    this.alerteApi.getAll().subscribe({
      next: (data) => {
        this.alertes = data;
        this.applyFilters();
      }
    });
    this.serviceApi.getAll().subscribe({ next: (data) => (this.services = data) });
  }

  generer(): void {
    if (this.form.invalid) {
      return;
    }
    const serviceId = Number(this.form.getRawValue().serviceId);
    this.alerteApi.generer(serviceId).subscribe({ next: () => this.load() });
  }

  updateEtat(id: number, etat: string): void {
    this.alerteApi.updateEtat(id, { etat }).subscribe({ next: () => this.load() });
  }

  goToAction(alerteId: number): void {
    this.router.navigate(['/actions-correctives'], { queryParams: { alerteId } });
  }

  applyFilters(): void {
    const { etat, niveau, dateFrom, dateTo } = this.filterForm.getRawValue();
    const from = dateFrom ? new Date(dateFrom) : null;
    const to = dateTo ? new Date(dateTo) : null;
    this.filtered = this.alertes.filter((item) => {
      const matchEtat = etat === 'TOUT' || item.etat === etat;
      const matchNiveau = niveau === 'TOUT' || item.niveau === niveau;
      const itemDate = item.dateCreation ? new Date(item.dateCreation) : null;
      const matchFrom = !from || (itemDate && itemDate >= from);
      const matchTo = !to || (itemDate && itemDate <= to);
      return matchEtat && matchNiveau && matchFrom && matchTo;
    });
  }

  resetFilters(): void {
    this.filterForm.reset({ etat: 'TOUT', niveau: 'TOUT', dateFrom: '', dateTo: '' });
    this.applyFilters();
  }
}
