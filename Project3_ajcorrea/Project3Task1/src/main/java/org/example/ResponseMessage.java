// Ariane Correa
// ajcorrea

package org.example;

import com.google.gson.JsonObject;
import java.math.BigInteger;

public class ResponseMessage {
    private JsonObject responseJson = new JsonObject();

    // option 0
    /**
     * Constructor for ResponseMessage option 0 with all parameters
     *
     * @param option
     * @param chainSize
     * @param difficulty
     * @param totalDifficulty
     * @param hashesPerSecond
     * @param totalExpectedHashes
     * @param nonce
     * @param chainHash
     */
    public ResponseMessage(int option, int chainSize, int difficulty, int totalDifficulty,
                           int hashesPerSecond, double totalExpectedHashes, BigInteger nonce, String chainHash) {
        responseJson.addProperty("selection", option);
        responseJson.addProperty("size", chainSize);
        responseJson.addProperty("chainHash", chainHash);
        responseJson.addProperty("totalHashes", totalExpectedHashes);
        responseJson.addProperty("totalDiff", totalDifficulty);
        responseJson.addProperty("recentNonce", nonce);
        responseJson.addProperty("diff", difficulty);
        responseJson.addProperty("hps", hashesPerSecond);
    }


    // option 1
    /**
     * Constructor for ResponseMessage with selection message and response
     *
     * @param selection
     * @param response
     */
    public ResponseMessage(int selection , String response) {
        responseJson.addProperty("selection", selection);
        responseJson.addProperty("response", response);
    }


    /**
     * getter method for ResponseJson
     *
     * @return
     */
    public JsonObject getResponseJson() {
        return responseJson;
    }
}
