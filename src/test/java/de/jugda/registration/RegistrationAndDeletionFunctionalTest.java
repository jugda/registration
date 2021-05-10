package de.jugda.registration;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author Niko Köbler, https://www.n-k.de, @dasniko
 */
@QuarkusTest
public class RegistrationAndDeletionFunctionalTest extends FunctionalTestBase {

    @Test
    void testGetRegistrationForm() {
        given()
            .queryParam("eventId", EVENT_ID)
            .queryParam("deadline", EVENT_ID + "T23:59:59+02:00")
            .get("/registration")
            .then()
            .statusCode(200)
            .body("html.body.form.h3", equalTo("Anmeldung"));
    }

    @Test
    void testCreateRegistrationAndDeleteViaDeleteRequest() {
        Participant participant = PARTICIPANTS.get(0);
        String link = given().contentType(ContentType.URLENC)
            .formParams(
                "eventId", EVENT_ID,
                "name", participant.getName(),
                "email", participant.getEmail()
            )
            .post("/registration")
            .then()
            .statusCode(200)
            .body("html.body.h3", equalTo("Vielen Dank, " + participant.getName()))
            .extract()
            .response()
            .htmlPath()
            .getString("html.body.p[2].a");
        String registrationId = link.substring(link.indexOf("?id=") + 4);

        given()
            .queryParam("id", registrationId)
            .delete("/delete")
            .then()
            .statusCode(204);
    }

    @Test
    void testCreateRegistrationAndDeleteViaDirectLink() {
        Participant participant = PARTICIPANTS.get(1);
        String link = given().contentType(ContentType.URLENC)
            .formParams(
                "eventId", EVENT_ID,
                "name", participant.getName(),
                "email", participant.getEmail()
            )
            .post("/registration")
            .then()
            .statusCode(200)
            .body("html.body.h3", equalTo("Vielen Dank, " + participant.getName()))
            .extract()
            .response()
            .htmlPath()
            .getString("html.body.p[2].a");
        String registrationId = link.substring(link.indexOf("?id=") + 4);

        given()
            .queryParam("id", registrationId)
            .get("/delete")
            .then()
            .statusCode(200)
            .body("html.body.h3", equalTo("Vielen Dank, " + participant.getName()));
    }

    @Test
    void testCreateRegistrationAndDeleteViaFormPost() {
        Participant participant = PARTICIPANTS.get(2);
        given().contentType(ContentType.URLENC)
            .formParams(
                "eventId", EVENT_ID,
                "name", participant.getName(),
                "email", participant.getEmail()
            )
            .post("/registration")
            .then()
            .statusCode(200)
            .body("html.body.h3", equalTo("Vielen Dank, " + participant.getName()));

        // test if the deletion form returns
        given()
            .queryParam("eventId", EVENT_ID)
            .get("/delete")
            .then()
            .statusCode(200)
            .body("html.body.form.h3", equalTo("Abmeldung"));

        // test if the deletion itself works
        given().contentType(ContentType.URLENC)
            .formParams(
                "eventId", EVENT_ID,
                "email", participant.getEmail()
            )
            .post("/delete")
            .then()
            .statusCode(200)
            .body("html.body.h3", equalTo("Vielen Dank, " + participant.getName()));
    }

}
