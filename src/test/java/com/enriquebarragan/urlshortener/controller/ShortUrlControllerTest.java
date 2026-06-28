package com.enriquebarragan.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.enriquebarragan.urlshortener.config.TestConfig;
import com.enriquebarragan.urlshortener.dto.ShortUrlRequest;
import com.enriquebarragan.urlshortener.model.User;
import com.enriquebarragan.urlshortener.repository.ShortUrlRepository;
import com.enriquebarragan.urlshortener.repository.UserRepository;
import com.enriquebarragan.urlshortener.security.JwtService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
class ShortUrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String token;

    @BeforeEach
    void setUp() {
        shortUrlRepository.deleteAll();
        userRepository.deleteAll();

        User user = User.builder()
                .username("enrique")
                .password(passwordEncoder.encode("123456"))
                .build();
        userRepository.save(user);

        token = jwtService.generateToken("enrique");
    }

    @Test
    void create_shouldCreateUrlAndReturn201() throws Exception {
        ShortUrlRequest request = new ShortUrlRequest("https://www.google.com", null);

        mockMvc.perform(post("/api/urls")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.originalUrl").value("https://www.google.com"))
                .andExpect(jsonPath("$.shortCode").exists())
                .andExpect(jsonPath("$.clickCount").value(0));
    }

    @Test
    void create_withoutToken_shouldReturn403() throws Exception {
        ShortUrlRequest request = new ShortUrlRequest("https://www.google.com", null);

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_withInvalidUrl_shouldReturn400() throws Exception {
        ShortUrlRequest request = new ShortUrlRequest("not-an-URL", null);

        mockMvc.perform(post("/api/urls")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void redirect_shouldRedirectTo302() throws Exception {
        ShortUrlRequest request = new ShortUrlRequest("https://www.google.com", null);

        String response = mockMvc.perform(post("/api/urls")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String shortCode = objectMapper.readTree(response).get("shortCode").asText();

        mockMvc.perform(get("/r/" + shortCode))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.google.com"));
    }
}