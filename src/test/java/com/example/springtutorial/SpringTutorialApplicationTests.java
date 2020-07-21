package com.example.springtutorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
//@DataJpaTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpringTutorialApplicationTests {

    private final SoftAssertions softAssertions = new SoftAssertions();

    Gson gson = new Gson();
    Person mockPerson;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    void setUpData() throws Exception {
        final String TEST_USER_FIRST_NAME = "SuperSecretTestUserFirstName";
        final String TEST_USER_LAST_NAME = "SuperSecretTestUserLastName";

        mockPerson = new Person(TEST_USER_FIRST_NAME, TEST_USER_LAST_NAME);
        ArrayList<Wish> listOfWishes = new ArrayList<Wish>() {
            {
                add(new Wish("test_wish_1"));
                add(new Wish("test_wish_2"));
                add(new Wish("test_wish_3"));
            }
        };
        mockPerson.setWishList(listOfWishes);

        String json = gson.toJson(mockPerson);

        MvcResult result = this.mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();
        long generatedId = Long.parseLong(result.getResponse().getContentAsString());
        mockPerson.setId(generatedId);
        mockPerson.setWishList(personRepository.findById(generatedId).getWishList());
    }


    @Test
    @DisplayName("Add new person")
    void addPerson() throws Exception {
        Person addingPerson = new Person("Adding", "Person" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        String json = gson.toJson(addingPerson);

        this.mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get person detail info")
    void getPersonInfo() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/person/" + mockPerson.getId()))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        Person person = objectMapper.readValue(contentAsString, Person.class);

        softAssertions.assertThat(person.getId())
                .as("Person id")
                .isEqualTo(mockPerson.getId());

        softAssertions.assertThat(person.getFirstName())
                .as("Person first name")
                .isEqualTo(mockPerson.getFirstName());

        softAssertions.assertThat(person.getLastName())
                .as("Person last name")
                .isEqualTo(mockPerson.getLastName());

        softAssertions.assertThat(person.getWishList())
                .as("Person wishlist")
                .isNotEmpty();

        softAssertions.assertAll();
    }


    @Test
    @DisplayName("Get list of persons")
    void getAllPersons() throws Exception {
        this.mockMvc.perform(get("/person/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[*].firstName", hasItem(mockPerson.getFirstName())))
                .andExpect(jsonPath("$[*].lastName", hasItem(mockPerson.getLastName())));
    }

    @Test
    @DisplayName("Add wish to a person")
    void addPersonWish() throws Exception {

        Wish wish = new Wish("test_wish");
        String json = gson.toJson(wish);
        MvcResult result = this.mockMvc.perform(post("/person/" + mockPerson.getId() + "/wishes")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        Wish createdWish = objectMapper.readValue(contentAsString, Wish.class);

        Assertions.assertEquals(wish, createdWish);
    }

    @Test
    @DisplayName("Edit person's wish")
    void editPersonWish() throws Exception {


        List<Wish> wishListToUpdate = mockPerson.getWishList();
        Wish wishToUpdate = wishListToUpdate.get(0);
        wishToUpdate.setDescription("upd_" + System.currentTimeMillis() + wishToUpdate.getDescription());

        String json = gson.toJson(wishToUpdate);


        MvcResult result = this.mockMvc.perform(post("/person/" + mockPerson.getId() + "/wishes/" + wishToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        Wish updatedWish = objectMapper.readValue(contentAsString, Wish.class);

        Assertions.assertEquals(updatedWish, wishToUpdate);
    }

}
