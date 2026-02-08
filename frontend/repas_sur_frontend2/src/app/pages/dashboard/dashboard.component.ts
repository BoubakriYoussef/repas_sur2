import { Component } from '@angular/core';
import { DatePipe, NgFor, NgIf } from '@angular/common';
import { forkJoin } from 'rxjs';
import { ConviveApiService } from '../../core/services/convive-api.service';
import { ServiceRepasApiService } from '../../core/services/service-repas-api.service';
import { AlerteApiService } from '../../core/services/alerte-api.service';
import { MenuApiService } from '../../core/services/menu-api.service';
import { ServiceRepasDto } from '../../core/models/service-repas.dto';
import { AlerteRisqueDto } from '../../core/models/alerte.dto';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgFor, NgIf, DatePipe],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent {
  stats: Array<{ label: string; value: string; trend: string }> = [];
  nextServices: ServiceRepasDto[] = [];
  criticalAlerts: AlerteRisqueDto[] = [];

  constructor(
    private readonly conviveApi: ConviveApiService,
    private readonly serviceApi: ServiceRepasApiService,
    private readonly alerteApi: AlerteApiService,
    private readonly menuApi: MenuApiService
  ) {
    this.load();
  }

  private load(): void {
    forkJoin({
      convives: this.conviveApi.getAll(),
      services: this.serviceApi.getAll(),
      alertes: this.alerteApi.getAll(),
      menus: this.menuApi.getAll()
    }).subscribe({
      next: ({ convives, services, alertes, menus }) => {
        const alertesOuvertes = alertes.filter((a) => a.etat !== 'RESOLUE').length;
        this.stats = [
          { label: 'Convives actifs', value: String(convives.length), trend: 'Total' },
          { label: 'Services planifies', value: String(services.length), trend: 'Total' },
          { label: 'Alertes ouvertes', value: String(alertesOuvertes), trend: 'En cours' },
          { label: 'Menus en base', value: String(menus.length), trend: 'Total' }
        ];

        this.nextServices = services
          .filter((service) => !!service.dateService)
          .sort((a, b) => new Date(a.dateService).getTime() - new Date(b.dateService).getTime())
          .slice(0, 4);

        this.criticalAlerts = alertes
          .sort((a, b) => this.niveauScore(b.niveau) - this.niveauScore(a.niveau))
          .slice(0, 3);
      }
    });
  }

  private niveauScore(value: string): number {
    switch (value) {
      case 'FORT':
        return 3;
      case 'MOYEN':
        return 2;
      case 'FAIBLE':
        return 1;
      default:
        return 0;
    }
  }
}
