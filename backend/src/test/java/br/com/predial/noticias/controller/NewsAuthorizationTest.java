package br.com.predial.noticias.controller;

import br.com.predial.noticias.config.SecurityConfig;
import br.com.predial.noticias.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({PublicNewsController.class, AdminNewsController.class})
@Import({SecurityConfig.class, NewsAuthorizationTest.TestUsers.class})
class NewsAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NewsService newsService;

    @Test
    void visitorCanReadPublicNews() throws Exception {
        given(newsService.listPublished()).willReturn(java.util.List.of());

        mockMvc.perform(get("/api/news"))
                .andExpect(status().isOk());
    }

    @Test
    void visitorCannotReadAdministrativeNews() throws Exception {
        mockMvc.perform(get("/api/admin/news"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "EDITOR")
    void editorCanReadAdministrativeNews() throws Exception {
        given(newsService.listAll()).willReturn(java.util.List.of());

        mockMvc.perform(get("/api/admin/news"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EDITOR")
    void editorCannotDeleteNews() throws Exception {
        mockMvc.perform(delete("/api/admin/news/1").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EDITOR")
    void editorCannotUpdateNews() throws Exception {
        mockMvc.perform(put("/api/admin/news/1")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {"title":"Título","category":"Segurança","kind":"Guia informativo","summary":"Resumo","body":"Texto","date":"2026-11-13","overlay":"","imageId":"","featured":false,"popular":false}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EDITOR")
    void editorCannotPatchNews() throws Exception {
        mockMvc.perform(patch("/api/admin/news/1")
                        .with(csrf())
                        .contentType("application/json")
                .content("{\"featured\":true}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EDITOR")
    void editorCannotPublishExistingNews() throws Exception {
        mockMvc.perform(post("/api/admin/news/1/publish").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminCanUpdateNews() throws Exception {
        mockMvc.perform(put("/api/admin/news/1")
                        .with(csrf())
                        .contentType("application/json")
                        .content("""
                                {"title":"Título","category":"Segurança","kind":"Guia informativo","summary":"Resumo","body":"Texto","date":"2026-11-13","overlay":"","imageId":"","featured":false,"popular":false}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void visitorRoleCannotReadAdministrativeNews() throws Exception {
        mockMvc.perform(get("/api/admin/news"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminMustSendCsrfTokenForNewsMutation() throws Exception {
        mockMvc.perform(post("/api/admin/news")
                        .contentType("application/json")
                        .content("""
                                {"title":"Título","category":"Segurança","kind":"Guia informativo","summary":"Resumo","body":"Texto","date":"2026-11-13","overlay":"","imageId":"","featured":false,"popular":false}
                                """))
                .andExpect(status().isForbidden());
    }

    @TestConfiguration
    static class TestUsers {

        @Bean
        UserDetailsService userDetailsService() {
            return username -> User.withUsername(username)
                    .password("{noop}test-password")
                    .roles("ADMIN")
                    .build();
        }
    }
}
