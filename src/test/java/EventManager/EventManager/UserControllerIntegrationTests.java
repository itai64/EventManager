package EventManager.EventManager;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTests {
    @Autowired
    private MockMvc mockMvc;


    @Test
    void createUserTest() throws Exception {
        JSONObject body = new JSONObject();
        body.put("name","itai");
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/createUser").content(String.valueOf(body)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(containsString("itai")));
    }

    @Test
    void createSingleEventForUserTest()throws Exception{
        createUserTest();

        JSONObject firstEventBody = new JSONObject().put("description","test event 1").put("popularity",5).put("location","TLV").put("eventDate","2020-12-20T20:45:30");
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/1/createEvent").content(String.valueOf(firstEventBody)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("test event 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.popularity").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value("TLV"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.eventDate").value("2020-12-20T20:45:30"));
    }

    @Test
    void createEventsForUserTest() throws Exception {
        createUserTest();

        JSONObject firstEventBody = new JSONObject().put("description","test event 1").put("popularity",5).put("location","TLV").put("eventDate","2020-12-20T20:45:30");
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/1/createEvent").content(String.valueOf(firstEventBody)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("test event 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.popularity").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value("TLV"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.eventDate").value("2020-12-20T20:45:30"));

        JSONObject secondEventBody = new JSONObject().put("description","test event 2").put("popularity",6).put("location","Ramat-Gan").put("eventDate","2024-12-20T20:45:30");
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/1/createEvent").content(String.valueOf(secondEventBody)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("test event 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.popularity").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value("Ramat-Gan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.eventDate").value("2024-12-20T20:45:30"));
    }

    @Test
    void deleteEventTest() throws Exception {
        createEventsForUserTest();

        JSONObject eventBody = new JSONObject().put("id",1).put("description","test event 1").put("popularity",5).put("location","TLV").put("eventDate","2020-12-20T20:45:30");
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/deleteEvent").content(String.valueOf(eventBody)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    void updateEventTest() throws Exception {
        createSingleEventForUserTest();

        JSONObject eventBody = new JSONObject().put("id",2).put("description","test event 2").put("popularity",6).put("location","Ramat-Aviv").put("eventDate","2024-12-20T20:45:30");
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/2/updateEvent").content(String.valueOf(eventBody)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value("Ramat-Aviv"));

    }

    @Test
    public void getEventByIdTest() throws Exception{
        createEventsForUserTest();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1/getEventById/2"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(2));

    }

    @Test
    public void getEventByLocationTest() throws Exception{

        createEventsForUserTest();

        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1/events/locationFilter/TLV"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].location").value("TLV"));
    }

    @Test
    public void getEventOrderByCreationTest() throws Exception{
        createEventsForUserTest();
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1/events/sortByCreationTime"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].id").value(1));
    }

    @Test
    public void getEventOrderByEventDateTest() throws Exception{
        createEventsForUserTest();
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1/events/sortByDate"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].id").value(1));
    }

    @Test
    public void getEventOrderByPopularity() throws Exception{
        createEventsForUserTest();
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/1/events/sortByPopularity"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].popularity").value(5));
    }


}
