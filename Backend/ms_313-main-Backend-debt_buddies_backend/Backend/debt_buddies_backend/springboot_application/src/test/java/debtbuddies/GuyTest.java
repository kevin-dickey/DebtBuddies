package debtbuddies;


import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.JsonObject;
import debtbuddies.Guys.GuyRepository;
import debtbuddies.Guys.GuyRepository;
import debtbuddies.Users.UserRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
//import org.springframework.boot.test.web.server.LocalServerPort;
// SBv3

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

@RunWith(SpringRunner.class)
public class GuyTest {

    String userName = "Owen";

    @Autowired
    private GuyRepository myRepo;

    @LocalServerPort
    int port;

    @Before
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }


    public void GetguysTest(){
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body("").
                when().
                get("/guys");

        int statusCode = response.getStatusCode();
        System.out.println(response.getBody().asString());

        assertEquals(200, statusCode);
    }

    @Test
    public void GetguyTest(){
        String userString = "[\n" +
                "    {\n" +
                "        \"id\": 1,\n" +
                "        \"guyName\": \"Owen\",\n" +
                "        \"guysFriend\": \"Kevin\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"id\": 2,\n" +
                "        \"guyName\": \"Owen\",\n" +
                "        \"guysFriend\": \"Bob\"\n" +
                "    }\n" +
                "]";
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body("").
                when().
                get("/guy/Owen");

        int statusCode = response.getStatusCode();
        System.out.println(response.getBody().asString());

        assertEquals(200, statusCode);
    }

 /*   @Test
    public void CreateUserTest() throws UnsupportedEncodingException {
        JsonObject reqparam=new JsonObject();
        reqparam.addProperty("id", userName);
        reqparam.addProperty("userName", "beans");
        reqparam.addProperty("isOnline", false);
        reqparam.addProperty("email", "123@grimace.org");
        reqparam.addProperty("password", "ronald123");
        reqparam.addProperty("coins", 8);
        reqparam.addProperty("icon", "icon3");

        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset", "utf-8").
                body(reqparam.toString().getBytes(StandardCharsets.UTF_8)).
                when().
                post("/users");

        int statusCode = response.getStatusCode();
        System.out.println(response.getBody().asString());

        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("success", returnObj.get("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    @Test
    public void CreateguyFailTest() throws UnsupportedEncodingException {

        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset", "utf-8").
                body("{}").
                when().
                post("/guys");

        int statusCode = response.getStatusCode();

        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("failure", returnObj.get("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void UpdateguyTest() throws InterruptedException {
        JsonObject reqparam=new JsonObject();
        reqparam.addProperty("id", 1);
        reqparam.addProperty("guyName", "Owen");
        reqparam.addProperty("guysFriend", "Colin");

        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset", "utf-8").
                body(reqparam.toString().getBytes(StandardCharsets.UTF_8)).
                when().
                put("/guys/1");

        int statusCode = response.getStatusCode();

        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals("Colin", returnObj.get("guysFriend"));
        } catch (JSONException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void UpdateguyFailTest(){
        JsonObject reqparam=new JsonObject();
        reqparam.addProperty("id", 1);
        reqparam.addProperty("guyName", "Owen");
        reqparam.addProperty("guysFriend", "Colin");

        Response response = RestAssured.given().
                header("Content-Type", "application/json").
                header("charset", "utf-8").
                body(reqparam.toString().getBytes(StandardCharsets.UTF_8)).
                when().
                put("/guys/1");

        int statusCode = response.getStatusCode();

        assertEquals(200, statusCode);

        String returnString = response.getBody().asString();

        assertEquals("", returnString);
        /*
        try {
            JSONObject returnObj = new JSONObject(returnString);
            assertEquals(userName, returnObj.get("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

         */
    }

    @Test
    public void DeleteguyTest(){

        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                body("").
                when().
                delete("/guys/1");

        int statusCode = response.getStatusCode();

        assertEquals(200, statusCode);

    }

    /*
    @Test
    public void reverseTest() {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body("hello").
                when().
                post("/reverse");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body for correct response
        String returnString = response.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject returnObj = returnArr.getJSONObject(returnArr.length()-1);
            assertEquals("olleh", returnObj.get("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void capitalizeTest() {
        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body("hello").
                when().
                post("/capitalize");


        // Check status code
        int statusCode = response.getStatusCode();
        assertEquals(200, statusCode);

        // Check response body for correct response
        String returnString = response.getBody().asString();
        try {
            JSONArray returnArr = new JSONArray(returnString);
            JSONObject returnObj = returnArr.getJSONObject(returnArr.length()-1);
            assertEquals("HELLO", returnObj.get("data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

     */
}