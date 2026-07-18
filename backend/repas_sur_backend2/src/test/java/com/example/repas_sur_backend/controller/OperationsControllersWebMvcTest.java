package com.example.repas_sur_backend.controller;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.repas_sur_backend.dto.ActionCorrectiveDto;
import com.example.repas_sur_backend.dto.AlerteRisqueDto;
import com.example.repas_sur_backend.dto.ConviveDto;
import com.example.repas_sur_backend.dto.IdCodeDto;
import com.example.repas_sur_backend.dto.IdNomDto;
import com.example.repas_sur_backend.dto.ServiceRepasDto;
import com.example.repas_sur_backend.dto.SiteRestaurationDto;
import com.example.repas_sur_backend.dto.UtilisateurDto;
import com.example.repas_sur_backend.exception.GlobalExceptionHandler;
import com.example.repas_sur_backend.model.enums.EtatAlerte;
import com.example.repas_sur_backend.model.enums.NiveauAlerte;
import com.example.repas_sur_backend.model.enums.RoleUtilisateur;
import com.example.repas_sur_backend.model.enums.StatutService;
import com.example.repas_sur_backend.model.enums.TypeConvive;
import com.example.repas_sur_backend.model.enums.TypeRepas;
import com.example.repas_sur_backend.model.enums.TypeSite;
import com.example.repas_sur_backend.security.JwtAuthenticationFilter;
import com.example.repas_sur_backend.security.SecurityConfig;
import com.example.repas_sur_backend.service.ActionCorrectiveService;
import com.example.repas_sur_backend.service.AlerteRisqueService;
import com.example.repas_sur_backend.service.ConviveService;
import com.example.repas_sur_backend.service.ServiceRepasService;
import com.example.repas_sur_backend.service.UtilisateurService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({
    ConviveController.class,
    UtilisateurController.class,
    ServiceRepasController.class,
    ActionCorrectiveController.class,
    AlerteRisqueController.class,
    GlobalExceptionHandler.class,
})
@Import(SecurityConfig.class)
class OperationsControllersWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConviveService conviveService;

    @MockitoBean
    private UtilisateurService utilisateurService;

    @MockitoBean
    private ServiceRepasService serviceRepasService;

    @MockitoBean
    private ActionCorrectiveService actionCorrectiveService;

    @MockitoBean
    private AlerteRisqueService alerteRisqueService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private com.example.repas_sur_backend.security.CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void passThroughJwtFilter() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(
            org.mockito.ArgumentMatchers.any(),
            org.mockito.ArgumentMatchers.any(),
            org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void conviveEndpoints_coverCrudAndValidation() throws Exception {
        SiteRestaurationDto site = new SiteRestaurationDto(1L, "Site", TypeSite.SCOLAIRE, "Rue");
        ConviveDto dto = new ConviveDto(1L, "Doe", "Jane", TypeConvive.ENFANT_SCOLAIRE, site, Set.of(), Set.of());
        when(conviveService.findAll()).thenReturn(List.of(dto));
        when(conviveService.getById(1L)).thenReturn(dto);
        when(conviveService.save(org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(conviveService.update(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any())).thenReturn(dto);

        mockMvc.perform(get("/api/convives").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].prenom").value("Jane"));

        mockMvc.perform(get("/api/convives/1").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk());

        mockMvc.perform(
            post("/api/convives")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Doe\",\"prenom\":\"Jane\",\"typeConvive\":\"ENFANT_SCOLAIRE\",\"siteId\":1}")
        )
            .andExpect(status().isCreated());

        mockMvc.perform(
            post("/api/convives")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Doe\"}")
        )
            .andExpect(status().isBadRequest());

        mockMvc.perform(
            put("/api/convives/1")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Doe\",\"prenom\":\"Jane\",\"typeConvive\":\"ENFANT_SCOLAIRE\",\"siteId\":1}")
        )
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/convives/1").with(user("admin").roles("ADMIN")))
            .andExpect(status().isNoContent());
    }

    @Test
    void utilisateurEndpoints_coverAdminAccessAndValidation() throws Exception {
        SiteRestaurationDto site = new SiteRestaurationDto(1L, "Site", TypeSite.SCOLAIRE, "Rue");
        UtilisateurDto dto = new UtilisateurDto(2L, "admin", "admin@test.local", "0102", "Chef", RoleUtilisateur.ADMIN, true, site);
        when(utilisateurService.findAll()).thenReturn(List.of(dto));
        when(utilisateurService.getById(2L)).thenReturn(dto);
        when(utilisateurService.save(org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(utilisateurService.update(org.mockito.ArgumentMatchers.eq(2L), org.mockito.ArgumentMatchers.any())).thenReturn(dto);

        mockMvc.perform(get("/api/utilisateurs").with(user("manager").roles("RESPONSABLE")))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/utilisateurs").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("admin"));

        mockMvc.perform(get("/api/utilisateurs/2").with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("ADMIN"));

        mockMvc.perform(
            post("/api/utilisateurs")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"secret\",\"role\":\"ADMIN\",\"actif\":true,\"siteId\":1}")
        )
            .andExpect(status().isCreated());

        mockMvc.perform(
            post("/api/utilisateurs")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"\",\"actif\":true}")
        )
            .andExpect(status().isBadRequest());

        mockMvc.perform(
            put("/api/utilisateurs/2")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"role\":\"ADMIN\",\"actif\":true,\"siteId\":1}")
        )
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/utilisateurs/2").with(user("admin").roles("ADMIN")))
            .andExpect(status().isNoContent());
    }

    @Test
    void serviceRepasEndpoints_coverCrud() throws Exception {
        SiteRestaurationDto site = new SiteRestaurationDto(1L, "Site", TypeSite.SCOLAIRE, "Rue");
        ServiceRepasDto dto = new ServiceRepasDto(
            3L,
            LocalDateTime.of(2026, 7, 17, 12, 0),
            TypeRepas.DEJEUNER,
            StatutService.PREVU,
            site,
            new IdNomDto(9L, "Menu A")
        );
        when(serviceRepasService.findAll()).thenReturn(List.of(dto));
        when(serviceRepasService.getById(3L)).thenReturn(dto);
        when(serviceRepasService.save(org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(serviceRepasService.update(org.mockito.ArgumentMatchers.eq(3L), org.mockito.ArgumentMatchers.any())).thenReturn(dto);

        mockMvc.perform(get("/api/services-repas").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].typeRepas").value("DEJEUNER"));

        mockMvc.perform(get("/api/services-repas/3").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk());

        mockMvc.perform(
            post("/api/services-repas")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dateService\":\"2026-07-17T12:00:00\",\"typeRepas\":\"DEJEUNER\",\"statut\":\"PREVU\",\"siteId\":1,\"menuId\":9}")
        )
            .andExpect(status().isCreated());

        mockMvc.perform(
            put("/api/services-repas/3")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"dateService\":\"2026-07-17T12:00:00\",\"typeRepas\":\"DEJEUNER\",\"statut\":\"PREVU\",\"siteId\":1,\"menuId\":9}")
        )
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/services-repas/3").with(user("manager").roles("RESPONSABLE")))
            .andExpect(status().isNoContent());
    }

    @Test
    void actionCorrectiveEndpoints_coverCrudAndForbidden() throws Exception {
        UtilisateurDto utilisateur = new UtilisateurDto(2L, "admin", null, null, null, RoleUtilisateur.ADMIN, true, null);
        ActionCorrectiveDto dto = new ActionCorrectiveDto(
            4L,
            LocalDateTime.of(2026, 7, 17, 13, 0),
            "RETRAIT",
            "Desc",
            new IdNomDto(10L, "Alerte"),
            new IdNomDto(11L, "Convive"),
            utilisateur
        );
        when(actionCorrectiveService.findAll()).thenReturn(List.of(dto));
        when(actionCorrectiveService.getById(4L)).thenReturn(dto);
        when(actionCorrectiveService.save(org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(actionCorrectiveService.update(org.mockito.ArgumentMatchers.eq(4L), org.mockito.ArgumentMatchers.any())).thenReturn(dto);

        mockMvc.perform(get("/api/actions-correctives").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].typeAction").value("RETRAIT"));

        mockMvc.perform(get("/api/actions-correctives/4").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk());

        mockMvc.perform(
            post("/api/actions-correctives")
                .with(user("cook").roles("CUISINE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2026-07-17T13:00:00\",\"typeAction\":\"RETRAIT\",\"description\":\"Desc\",\"alerteId\":10,\"utilisateurId\":2}")
        )
            .andExpect(status().isCreated());

        mockMvc.perform(
            put("/api/actions-correctives/4")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"date\":\"2026-07-17T13:00:00\",\"typeAction\":\"RETRAIT\",\"description\":\"Desc\",\"alerteId\":10,\"utilisateurId\":2}")
        )
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/actions-correctives/4").with(user("cook").roles("CUISINE")))
            .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/actions-correctives/4").with(user("manager").roles("RESPONSABLE")))
            .andExpect(status().isNoContent());
    }

    @Test
    void alerteEndpoints_coverCrudGenerationAndEtatUpdate() throws Exception {
        SiteRestaurationDto site = new SiteRestaurationDto(1L, "Site", TypeSite.SCOLAIRE, "Rue");
        AlerteRisqueDto dto = new AlerteRisqueDto(
            5L,
            EtatAlerte.NOUVELLE,
            NiveauAlerte.FORT,
            "Conflit detecte",
            LocalDateTime.of(2026, 7, 17, 14, 0),
            new IdNomDto(12L, "Doe Jane"),
            new ServiceRepasDto(3L, LocalDateTime.of(2026, 7, 17, 12, 0), TypeRepas.DEJEUNER, StatutService.PREVU, site, new IdNomDto(9L, "Menu A")),
            Set.of(new IdCodeDto(1L, "GLUTEN", "Gluten"))
        );
        when(alerteRisqueService.findAll()).thenReturn(List.of(dto));
        when(alerteRisqueService.getById(5L)).thenReturn(dto);
        when(alerteRisqueService.save(org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(alerteRisqueService.update(org.mockito.ArgumentMatchers.eq(5L), org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(alerteRisqueService.updateEtat(org.mockito.ArgumentMatchers.eq(5L), org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(alerteRisqueService.genererAlertesPourService(3L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/alertes").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].niveau").value("FORT"));

        mockMvc.perform(get("/api/alertes/5").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk());

        mockMvc.perform(
            post("/api/alertes")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"etat\":\"NOUVELLE\",\"niveau\":\"FORT\",\"message\":\"Conflit detecte\",\"dateCreation\":\"2026-07-17T14:00:00\",\"conviveId\":12,\"serviceId\":3,\"allergeneIds\":[1]}")
        )
            .andExpect(status().isCreated());

        mockMvc.perform(
            put("/api/alertes/5")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"etat\":\"NOUVELLE\",\"niveau\":\"FORT\",\"message\":\"Conflit detecte\",\"dateCreation\":\"2026-07-17T14:00:00\",\"conviveId\":12,\"serviceId\":3,\"allergeneIds\":[1]}")
        )
            .andExpect(status().isOk());

        mockMvc.perform(post("/api/alertes/generer/3").with(user("manager").roles("RESPONSABLE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].message").value("Conflit detecte"));

        mockMvc.perform(
            post("/api/alertes/5/etat")
                .with(user("cook").roles("CUISINE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"etat\":\"TRAITEE\"}")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.etat").value("NOUVELLE"));

        mockMvc.perform(delete("/api/alertes/5").with(user("manager").roles("RESPONSABLE")))
            .andExpect(status().isNoContent());
    }
}
