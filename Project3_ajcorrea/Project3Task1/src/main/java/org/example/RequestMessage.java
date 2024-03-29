// Ariane Correa
// ajcorrea

package org.example;
import com.google.gson.JsonObject;

public class RequestMessage {
    private JsonObject requestJson = new JsonObject();

    // options 0, 2, 3, 4, 6
    /**
     * Constructor for single operation
     *
     * @param op
     */
    public RequestMessage(int op) {
        requestJson.addProperty("op", op);
    }

    // options 1 , 4
    /**
     * Constructor to send additional property and transaction details
     *
     * @param op
     * @param property
     * @param transaction
     */
    public RequestMessage(int op, int property, String transaction) {
        requestJson.addProperty("op", op);
        if (op == 1)
            requestJson.addProperty("difficulty", property);
        if (op == 4)
            requestJson.addProperty("index", property);
        requestJson.addProperty("transaction", transaction);
    }

    /**
     * Getter method for requestJson
     *
     * @return
     */
    public JsonObject getRequestJson() {
        return requestJson;
    }
}
