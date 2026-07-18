package com.example.repas_sur_backend.controller;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.repas_sur_backend.dto.AllergeneDto;
import com.example.repas_sur_backend.dto.MenuDto;
import com.example.repas_sur_backend.dto.PlatDto;
import com.example.repas_sur_backend.dto.RegimeDto;
import com.example.repas_sur_backend.dto.SiteRestaurationDto;
import com.example.repas_sur_backend.exception.GlobalExceptionHandler;
import com.example.repas_sur_backend.exception.NotFoundException;
import com.example.repas_sur_backend.model.enums.RegimeType;
import com.example.repas_sur_backend.model.enums.TypeSite;
import com.example.repas_sur_backend.security.JwtAuthenticationFilter;
import com.example.repas_sur_backend.security.SecurityConfig;
import com.example.repas_sur_backend.service.AllergeneService;
import com.example.repas_sur_backend.service.MenuService;
import com.example.repas_sur_backend.service.PlatService;
import com.example.repas_sur_backend.service.RegimeService;
import com.example.repas_sur_backend.service.SiteRestaurationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
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
    AllergeneController.class,
    MenuController.class,
    PlatController.class,
    RegimeController.class,
    SiteRestaurationController.class,
    GlobalExceptionHandler.class,
})
@Import(SecurityConfig.class)
class AdminCrudControllersWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AllergeneService allergeneService;

    @MockitoBean
    private MenuService menuService;

    @MockitoBean
    private PlatService platService;

    @MockitoBean
    private RegimeService regimeService;

    @MockitoBean
    private SiteRestaurationService siteRestaurationService;

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
    void allergeneEndpoints_coverSuccessValidationAndSecurity() throws Exception {
        AllergeneDto dto = new AllergeneDto(1L, "GLUTEN", "Gluten", "desc");
        when(allergeneService.findAll()).thenReturn(List.of(dto));
        when(allergeneService.getById(1L)).thenReturn(dto);
        when(allergeneService.save(org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(allergeneService.update(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.any())).thenReturn(dto);

        mockMvc.perform(get("/api/allergenes"))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/allergenes").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("GLUTEN"));

        mockMvc.perform(get("/api/allergenes/1").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.libelle").value("Gluten"));

        mockMvc.perform(
            post("/api/allergenes")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"GLUTEN\",\"libelle\":\"Gluten\",\"description\":\"desc\"}")
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(
            post("/api/allergenes")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"\",\"libelle\":\"Gluten\"}")
        )
            .andExpect(status().isBadRequest());

        mockMvc.perform(
            put("/api/allergenes/1")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"GLUTEN\",\"libelle\":\"Gluten\",\"description\":\"desc\"}")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("GLUTEN"));

        mockMvc.perform(delete("/api/allergenes/1").with(user("manager").roles("RESPONSABLE")))
            .andExpect(status().isNoContent());

        mockMvc.perform(
            post("/api/allergenes")
                .with(user("cook").roles("CUISINE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"GLUTEN\",\"libelle\":\"Gluten\",\"description\":\"desc\"}")
        )
            .andExpect(status().isForbidden());
    }

    @Test
    void menuEndpoints_coverCrudAnd404() throws Exception {
        MenuDto dto = new MenuDto(2L, "Menu A", "desc", Set.of());
        when(menuService.findAll()).thenReturn(List.of(dto));
        when(menuService.getById(2L)).thenReturn(dto);
        when(menuService.save(org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(menuService.update(org.mockito.ArgumentMatchers.eq(2L), org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(menuService.getById(99L)).thenThrow(new NotFoundException("Menu not found: 99"));

        mockMvc.perform(get("/api/menus").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nom").value("Menu A"));

        mockMvc.perform(get("/api/menus/2").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("desc"));

        mockMvc.perform(get("/api/menus/99").with(user("cook").roles("CUISINE")))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Menu not found: 99"));

        mockMvc.perform(
            post("/api/menus")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Menu A\",\"description\":\"desc\",\"platIds\":[1,2]}")
        )
            .andExpect(status().isCreated());

        mockMvc.perform(
            put("/api/menus/2")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Menu A\",\"description\":\"desc\",\"platIds\":[1,2]}")
        )
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/menus/2").with(user("admin").roles("ADMIN")))
            .andExpect(status().isNoContent());
    }

    @Test
    void platEndpoints_coverCrudValidationAndForbidden() throws Exception {
        PlatDto dto = new PlatDto(3L, "Plat A", "ENTREE", "desc", false, true, Set.of());
        when(platService.findAll()).thenReturn(List.of(dto));
        when(platService.getById(3L)).thenReturn(dto);
        when(platService.save(org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(platService.update(org.mockito.ArgumentMatchers.eq(3L), org.mockito.ArgumentMatchers.any())).thenReturn(dto);

        mockMvc.perform(get("/api/plats").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nom").value("Plat A"));

        mockMvc.perform(get("/api/plats/3").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk());

        mockMvc.perform(
            post("/api/plats")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Plat A\",\"categorie\":\"ENTREE\",\"description\":\"desc\",\"contientPorc\":false,\"estVegetarien\":true}")
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.estVegetarien").value(true));

        mockMvc.perform(
            post("/api/plats")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Plat A\"}")
        )
            .andExpect(status().isBadRequest());
        verify(platService).save(org.mockito.ArgumentMatchers.any());

        mockMvc.perform(
            put("/api/plats/3")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Plat A\",\"categorie\":\"ENTREE\",\"description\":\"desc\",\"contientPorc\":false,\"estVegetarien\":true}")
        )
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/plats/3").with(user("manager").roles("RESPONSABLE")))
            .andExpect(status().isNoContent());

        mockMvc.perform(delete("/api/plats/3").with(user("cook").roles("CUISINE")))
            .andExpect(status().isForbidden());
    }

    @Test
    void regimeEndpoints_coverCrud() throws Exception {
        RegimeDto dto = new RegimeDto(4L, "VEGETARIEN", "Vegetarien", RegimeType.ETHIQUE, "desc");
        when(regimeService.findAll()).thenReturn(List.of(dto));
        when(regimeService.getById(4L)).thenReturn(dto);
        when(regimeService.save(org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(regimeService.update(org.mockito.ArgumentMatchers.eq(4L), org.mockito.ArgumentMatchers.any())).thenReturn(dto);

        mockMvc.perform(get("/api/regimes").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("VEGETARIEN"));

        mockMvc.perform(get("/api/regimes/4").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk());

        mockMvc.perform(
            post("/api/regimes")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"VEGETARIEN\",\"libelle\":\"Vegetarien\",\"type\":\"ETHIQUE\",\"description\":\"desc\"}")
        )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.type").value("ETHIQUE"));

        mockMvc.perform(
            put("/api/regimes/4")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"code\":\"VEGETARIEN\",\"libelle\":\"Vegetarien\",\"type\":\"ETHIQUE\",\"description\":\"desc\"}")
        )
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/regimes/4").with(user("admin").roles("ADMIN")))
            .andExpect(status().isNoContent());
    }

    @Test
    void siteEndpoints_coverCrudAndValidation() throws Exception {
        SiteRestaurationDto dto = new SiteRestaurationDto(5L, "Site A", TypeSite.SCOLAIRE, "Rue 1");
        when(siteRestaurationService.findAll()).thenReturn(List.of(dto));
        when(siteRestaurationService.getById(5L)).thenReturn(dto);
        when(siteRestaurationService.save(org.mockito.ArgumentMatchers.any())).thenReturn(dto);
        when(siteRestaurationService.update(org.mockito.ArgumentMatchers.eq(5L), org.mockito.ArgumentMatchers.any())).thenReturn(dto);

        mockMvc.perform(get("/api/sites").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].type").value("SCOLAIRE"));

        mockMvc.perform(get("/api/sites/5").with(user("cook").roles("CUISINE")))
            .andExpect(status().isOk());

        mockMvc.perform(
            post("/api/sites")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Site A\",\"type\":\"SCOLAIRE\",\"adresse\":\"Rue 1\"}")
        )
            .andExpect(status().isCreated());

        mockMvc.perform(
            post("/api/sites")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"\",\"adresse\":\"Rue 1\"}")
        )
            .andExpect(status().isBadRequest());

        mockMvc.perform(
            put("/api/sites/5")
                .with(user("manager").roles("RESPONSABLE"))
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"nom\":\"Site A\",\"type\":\"SCOLAIRE\",\"adresse\":\"Rue 1\"}")
        )
            .andExpect(status().isOk());

        mockMvc.perform(delete("/api/sites/5").with(user("manager").roles("RESPONSABLE")))
            .andExpect(status().isNoContent());
    }
}
