import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { authGuard } from './core/guards/auth.guard';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { ConvivesComponent } from './pages/convives/convives.component';
import { AllergenesComponent } from './pages/allergenes/allergenes.component';
import { PlatsComponent } from './pages/plats/plats.component';
import { MenusComponent } from './pages/menus/menus.component';
import { ServicesComponent } from './pages/services/services.component';
import { AlertesComponent } from './pages/alertes/alertes.component';
import { ParametresComponent } from './pages/parametres/parametres.component';
import { UtilisateursComponent } from './pages/utilisateurs/utilisateurs.component';
import { ActionsCorrectivesComponent } from './pages/actions-correctives/actions-correctives.component';
import { LoginComponent } from './pages/auth/login.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'login', component: LoginComponent },
      { path: 'convives', component: ConvivesComponent },
      { path: 'allergenes', component: AllergenesComponent },
      { path: 'plats', component: PlatsComponent },
      { path: 'menus', component: MenusComponent },
      { path: 'services', component: ServicesComponent },
      { path: 'alertes', component: AlertesComponent },
      { path: 'actions-correctives', component: ActionsCorrectivesComponent },
      { path: 'utilisateurs', component: UtilisateursComponent },
      { path: 'parametres', component: ParametresComponent }
    ]
  },
  { path: '**', redirectTo: 'dashboard' }
];
