package com.example.springtutorial;

import com.example.springtutorial.userClasses.Person;
import com.example.springtutorial.userClasses.PersonRepository;
import com.example.springtutorial.userClasses.Wish;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.google.gson.Gson;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private ObjectMapper objectMapper;

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
        Person mockPerson = createTestPerson();

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
                .isNotNull();

        softAssertions.assertAll();
    }


    @Test
    @DisplayName("Get list of all persons")
    void getAllPersons() throws Exception {
        Person mockPerson = createTestPerson();

        this.mockMvc.perform(get("/person"))
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
        Person mockPerson = createTestPerson();
        String wishDescription = "test_wish" + System.currentTimeMillis();
        MvcResult result = this.mockMvc.perform(post("/person/" + mockPerson.getId() + "/wishes")
                .contentType(MediaType.APPLICATION_JSON).content(wishDescription))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        Wish createdWish = objectMapper.readValue(contentAsString, Wish.class);
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(createdWish.getDescription())
                .as("Received wish description")
                .isEqualTo(wishDescription);

        softAssertions.assertThat(createdWish.getId())
                .as("Received wish id")
                .isNotEqualTo("0");
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("Add wish to a person with null wishlist")
    void addWishToNullWishlist() throws Exception {
        Person mockPerson = createTestPerson();
        mockPerson.setWishList(null);

        String wishDescription = "test_wish" + System.currentTimeMillis();
        MvcResult result = this.mockMvc.perform(post("/person/" + mockPerson.getId() + "/wishes")
                .contentType(MediaType.APPLICATION_JSON).content(wishDescription))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        Wish createdWish = objectMapper.readValue(contentAsString, Wish.class);

        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(createdWish.getDescription())
                .as("Received wish description")
                .isEqualTo(wishDescription);

        softAssertions.assertThat(createdWish.getId())
                .as("Received wish id")
                .isNotEqualTo("0");
        softAssertions.assertAll();
    }

    @Test
    @DisplayName("Get person wishlist")
    void getPersonWishList() throws Exception {
        Person mockPerson = createTestPerson();
        List<Wish> wishlist = mockPerson.getWishList();

        MvcResult result = this.mockMvc.perform(get("/person/" + mockPerson.getId() + "/wishes"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(greaterThan(0))))
                .andReturn();

        CollectionType javaType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Wish.class);

        String contentAsString = result.getResponse().getContentAsString();
        List<Wish> responseWishlist = objectMapper.readValue(contentAsString, javaType);

        assertIterableEquals(wishlist, responseWishlist);
    }


    @Test
    @DisplayName("Edit person's wish")
    void editPersonWish() throws Exception {
        Person mockPerson = createTestPerson();

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

        assertEquals(updatedWish, wishToUpdate);
    }

    @Test
    @DisplayName("Edit person's non-existing wish")
    void editPersonFakeWish() throws Exception {
        Person mockPerson = createTestPerson();

        List<Wish> wishListToUpdate = mockPerson.getWishList();
        Wish wishToUpdate = wishListToUpdate.get(0);
        wishToUpdate.setDescription("upd_" + System.currentTimeMillis() + wishToUpdate.getDescription());

        String json = gson.toJson(wishToUpdate);

        this.mockMvc.perform(post("/person/" + mockPerson.getId() + "/wishes/" + System.currentTimeMillis())
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Take person's wish")
    void takePersonWish() throws Exception {
        Person mockPerson = createTestPerson();

        List<Wish> wishListToUpdate = mockPerson.getWishList();
        Wish wishToUpdate = wishListToUpdate.get(0);

        this.mockMvc.perform(post("/person/" + mockPerson.getId() + "/wishes/" + wishToUpdate.getId() + "/take"))
                .andDo(print())
                .andExpect(status().isOk());

        Person mockPersonWithTakenWish = personRepository.findById(mockPerson.getId());
        Assertions.assertTrue(mockPersonWithTakenWish.getWishList().stream().filter(wish -> wish == wishToUpdate).anyMatch(Wish::isTaken));
    }

    @Test
    @DisplayName("Delete person's wish")
    void deletePersonWish() throws Exception {
        Person mockPerson = createTestPerson();

        List<Wish> wishListToUpdate = mockPerson.getWishList();
        Wish wishToDelete = wishListToUpdate.get(0);

        this.mockMvc.perform(delete("/person/" + mockPerson.getId() + "/wishes/" + wishToDelete.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        Person mockPersonWithDeletedWish = personRepository.findById(mockPerson.getId());
        Assertions.assertTrue(mockPersonWithDeletedWish.getWishList().stream().noneMatch(wish -> wish == wishToDelete));
    }

    @Test
    @DisplayName("Delete person's non-existing wish")
    void deletePersonFakeWish() throws Exception {
        Person mockPerson = createTestPerson();

        this.mockMvc.perform(delete("/person/" + mockPerson.getId() + "/wishes/" + System.currentTimeMillis()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    /**
     * Create mock person with wishlist
     *
     * @return mock person object
     */
    private Person createTestPerson() {

        Person mockPerson = new Person("Adding", "Person" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        ArrayList<Wish> listOfWishes = Stream.of(
                new Wish("test_wish_1"),
                new Wish("test_wish_2"),
                new Wish("test_wish_3")).collect(Collectors.toCollection(ArrayList::new));
        mockPerson.setWishList(listOfWishes);

        personRepository.save(mockPerson);
        return mockPerson;
    }
}
