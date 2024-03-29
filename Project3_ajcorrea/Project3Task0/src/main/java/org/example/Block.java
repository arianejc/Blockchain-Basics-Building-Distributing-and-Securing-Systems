// Ariane Correa
// ajcorrea

package org.example;

import com.google.gson.JsonObject;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
public class Block {

    // Stores the position of the block on the chain
    private int index;
    // Stores the timestamp of the instant when the block was created
    private Timestamp timestamp;
    // Stores single transaction details of the block
    private String data;
    // Stores the SHA256 hash of the block's parent
    private String previousHash;
    // Value determined by POW (Proof of Work) routine
    private BigInteger nonce;
    // The minimum number of leftmost hex digits needed by a proper hash
    private int difficulty;

    /**
     * Constructor for creating a Block object.
     *
     * @param index      The position of the block on the chain.
     * @param timestamp  The timestamp of when the block was created.
     * @param data       Single transaction details of the block.
     * @param difficulty The difficulty level for proof of work.
     */
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
        this.nonce = BigInteger.ZERO;
    }

    /**
     * Calculate and return the SHA-256 hash of the block.
     *
     * @return The calculated SHA-256 hash.
     * @throws NoSuchAlgorithmException If SHA-256 hashing algorithm is not available.
     */
    public String calculateHash() throws NoSuchAlgorithmException {
        String parentString = String.valueOf(this.index) + this.timestamp + this.data +
                this.previousHash + this.nonce + this.difficulty;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = md.digest(parentString.getBytes(StandardCharsets.UTF_8));
        return BlockHelper.bytesToHex(encodedHash);
    }

    /**
     * Get the current nonce value.
     *
     * @return The nonce value.
     */
    public BigInteger getNonce() {
        return nonce;
    }

    /**
     * Get the difficulty level for proof of work.
     *
     * @return The difficulty level.
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Set the difficulty level for proof of work.
     *
     * @param difficulty The new difficulty level.
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Get the position of the block on the chain.
     *
     * @return The block's index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get the timestamp when the block was created.
     *
     * @return The timestamp of block creation.
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Get the data (transaction details) of the block.
     *
     * @return The block's data.
     */
    public String getData() {
        return data;
    }

    /**
     * Get the SHA-256 hash of the block's parent.
     *
     * @return The SHA-256 hash of the parent block.
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Set the position of the block on the chain.
     *
     * @param index The new block index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Set the timestamp when the block was created.
     *
     * @param timestamp The new block creation timestamp.
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Set the data (transaction details) of the block.
     *
     * @param data The new block data.
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Set the SHA-256 hash of the block's parent.
     *
     * @param previousHash The new parent block's hash.
     */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    /**
     * Convert the block's attributes to a JSON representation.
     *
     * @return A JSON string representing the block's attributes.
     */
    @Override
    public String toString() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("index", index);
        jsonObject.addProperty("timestamp", String.valueOf(timestamp));
        jsonObject.addProperty("tx", data);
        jsonObject.addProperty("previousHash", previousHash);
        jsonObject.addProperty("nonce", nonce);
        jsonObject.addProperty("difficulty", difficulty);
        return jsonObject.toString();
    }

    /**
     * Perform Proof of Work (POW) to find a hash that meets the required difficulty level.
     *
     * @return The valid SHA-256 hash after POW.
     * @throws NoSuchAlgorithmException If SHA-256 hashing algorithm is not available.
     */
    public String proofOfWork() throws NoSuchAlgorithmException {
        String hexHash = calculateHash();
        String matchString = "";
        for (int i = 0; i < this.difficulty; i++) {
            matchString = matchString + "0";
        }
        while (!hexHash.substring(0, this.difficulty).equalsIgnoreCase(matchString)) {
            this.nonce = this.nonce.add(BigInteger.ONE);
            hexHash = calculateHash();
        }
        return hexHash;
    }
}
