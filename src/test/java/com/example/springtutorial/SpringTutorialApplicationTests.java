package com.example.springtutorial;

import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
//@Transactional
@AutoConfigureMockMvc
class SpringTutorialApplicationTests {

    private Person testPerson = new Person("Beep", "Boop");

    @Autowired
    private MainController mainController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(mainController);
    }

    @Test
    void addPerson() {
        long id = mainController.addPerson(testPerson);
        Person newPerson = mainController.getPerson(id);
        Assertions.assertNotNull(newPerson);
    }

    @Test
    void ehTest() throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(testPerson);

        this.mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }
}
