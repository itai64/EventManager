package EventManager.EventManager;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.aspectj.lang.annotation.After;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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

    @Test
    public void RateLimitTest() throws Exception {

        for (int i=0;i<40;i++){
            createUserTest();
        }
        JSONObject body = new JSONObject();
        body.put("name","itai");
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/createUser").content(String.valueOf(body)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests());

    }

    @Test
    public void MultiplyEventsTest() throws Exception{
        JSONObject body = new JSONObject();
        body.put("name","Shoval");
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/createUser").content(String.valueOf(body)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(content().string(containsString("Shoval")));

        JSONArray createMultiplyEventsBody = new JSONArray();
        createMultiplyEventsBody.put(new JSONObject().put("description","test event 1").put("popularity",5).put("location","TLV").put("eventDate","2020-12-20T20:45:30"));
        createMultiplyEventsBody.put(new JSONObject().put("description","test event 2").put("popularity",6).put("location","Ramat-Gan").put("eventDate","2024-12-20T20:45:30"));


        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/1/createMultiplyEvent").content(String.valueOf(createMultiplyEventsBody)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].description").value("test event 1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].popularity").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].location").value("TLV"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].eventDate").value("2020-12-20T20:45:30"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[1].description").value("test event 2"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[1].popularity").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[1].location").value("Ramat-Gan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[1].eventDate").value("2024-12-20T20:45:30"));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/event/getAllEvents")).andExpect(MockMvcResultMatchers.jsonPath("$",hasSize(2)));


        JSONArray updateMultiplyEventsBody = new JSONArray();
        updateMultiplyEventsBody.put(new JSONObject().put("id",1).put("description","test event 3").put("popularity",5).put("location","TLV").put("eventDate","2020-12-20T20:45:30"));
        updateMultiplyEventsBody.put(new JSONObject().put("id",2).put("description","test event 4").put("popularity",6).put("location","Ramat-Gan").put("eventDate","2024-12-20T20:45:30"));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/1/updateMultiplyEvents").content(String.valueOf(updateMultiplyEventsBody)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].description").value("test event 3"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].popularity").value(5))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].location").value("TLV"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[0].eventDate").value("2020-12-20T20:45:30"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[1].description").value("test event 4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[1].popularity").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[1].location").value("Ramat-Gan"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.events[1].eventDate").value("2024-12-20T20:45:30"));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/event/getAllEvents")).andExpect(MockMvcResultMatchers.jsonPath("$",hasSize(2)));

        JSONArray deleteMultiplyEventsBody = new JSONArray();
        deleteMultiplyEventsBody.put(1);
        deleteMultiplyEventsBody.put(2);
        this.mockMvc.perform(MockMvcRequestBuilders.delete("/users/1/deleteMultiplyEvents").content(String.valueOf(deleteMultiplyEventsBody)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void subscribeTest() throws Exception{
        // I read in the internet that the eventBus open new thread, so the logs of the event not printed.
        createEventsForUserTest();
        JSONObject subscriber = new JSONObject();
        subscriber.put("name","Shoval");
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/createUser").content(String.valueOf(subscriber)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/2/subscribeEvent").content(String.valueOf(1)).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        deleteEventTest();

    }

}
