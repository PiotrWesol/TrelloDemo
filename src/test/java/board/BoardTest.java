package board;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

public class BoardTest {

    private final String api = "";
    private final String token = "";

    @Test
    public void createNewBoard() {

        Response response = given()
                .queryParam("key", api)
                .queryParam("token", token)
                .queryParam("name", "My first board")
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertEquals("My first board", json.get("name"));

        String boardId = json.get("id");

        given()
                .queryParam("key", api)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .delete("https://api.trello.com/1/boards/" + boardId)
                .then()
                .statusCode(200);

    }

    @Test
    public void createBoardWithEmptyBoardName() {

        Response response = given()
                .queryParam("key", api)
                .queryParam("token", token)
                .queryParam("name", "")
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(400)
                .extract()
                .response();
    }

    @Test
    public void createBoardWithoutDefaultLists() {
        Response response = given()
                .queryParam("key", api)
                .queryParam("token", token)
                .queryParam("name", "Board without default lists")
                .queryParam("defaultLists", false)
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertEquals("Board without default lists", json.get("name"));

        String boardId = json.get("id");

        Response responseGet = given()
                .queryParam("key", api)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .get("https://api.trello.com/1/boards" + boardId + "/lists")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath jsonGet = responseGet.jsonPath();
        List<String> idList = jsonGet.getList("id");

        Assertions.assertEquals(0, idList.size());
    }
    @Test
    public void createBoardWithDeafultLists(){
        Response response = given()
                .queryParam("key", api)
                .queryParam("token", token)
                .queryParam("name", "Board with default lists")
                .queryParam("defaultLists", true)
                .contentType(ContentType.JSON)
                .when()
                .post("https://api.trello.com/1/boards/")
                .then()
                .statusCode(200)
                .extract()
                .response();

        JsonPath json = response.jsonPath();
        Assertions.assertEquals("Board with default lists", json.get("name"));

        String boardId = json.get("id");

        Response responseGet = given()
                .queryParam("key", api)
                .queryParam("token", token)
                .contentType(ContentType.JSON)
                .when()
                .get("https://api.trello.com/1/boards" + boardId + "/lists")
                .then()
                .statusCode(200)
                .extract()
                .response();


        JsonPath jsonGet = responseGet.jsonPath();
        List<String> idList = jsonGet.getList("id");
        Assertions.assertEquals(3, idList.size());

        List<String> nameList = jsonGet.getList("name");
        Assertions.assertEquals("To Do", nameList.get(0));
        Assertions.assertEquals("Doing", nameList.get(1));
        Assertions.assertEquals("Done", nameList.get(2));


    }

}