// Ariane Correa
// ajcorrea

package org.example;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class BlockChain {

    // Private instance variables
    private List<Block> blockList;
    private String chainHash;
    private int hashesPerSecond;
    Gson gson = new Gson();

    /**
     * Constructor to initialize a new blockchain.
     */
    public BlockChain() {
        this.blockList = new ArrayList<>();
        this.chainHash = "";
        this.hashesPerSecond = 0;
    }

    /**
     * Get the current blockchain's hash.
     *
     * @return The hash of the entire blockchain.
     */
    public String getChainHash() {
        return chainHash;
    }

    /**
     * Get the current system timestamp.
     *
     * @return A Timestamp object representing the current time.
     */
    public Timestamp getTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * Get the most recent block in the blockchain.
     *
     * @return The latest Block object in the chain.
     */
    public Block getLatestBlock() {
        return blockList.get(blockList.size() - 1);
    }

    /**
     * Get the size (number of blocks) of the blockchain.
     *
     * @return The number of blocks in the blockchain.
     */
    public int getChainSize() {
        return blockList.size();
    }

    /**
     * Compute the number of hash calculations per second.
     *
     * @throws NoSuchAlgorithmException If SHA-256 algorithm is not available.
     */
    public void computeHashesPerSecond() throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        int i = 0;
        Timestamp startTime = getTime();
        while (i < 2000000) {
            // Calculate a hash 2,000,000 times with a fixed input
            byte[] encodedHash = md.digest(
                    "00000000".getBytes(StandardCharsets.UTF_8));
            i += 1;
        }
        Timestamp endTime = getTime();
        this.hashesPerSecond = 2000000 / (endTime.compareTo(startTime));
    }

    /**
     * Get the number of hash calculations per second.
     *
     * @return The number of hash calculations per second.
     */
    public int getHashesPerSecond() {
        return this.hashesPerSecond;
    }

    /**
     * Add a new block to the blockchain.
     *
     * @param newBlock The Block object to be added.
     * @throws NoSuchAlgorithmException If SHA-256 algorithm is not available.
     */
    public void addBlock(Block newBlock) throws NoSuchAlgorithmException {
        if (this.blockList.size() > 0)
            newBlock.setPreviousHash(getChainHash());
        else
            newBlock.setPreviousHash("");
        blockList.add(newBlock);
        this.chainHash = newBlock.proofOfWork();
    }

    /**
     * Converts the blockchain to a JSON representation.
     *
     * @return JSON string representing the blockchain.
     */
    @Override
    public String toString() {

        JsonArray jsonArray = new JsonArray();

        for (Block b : blockList) {
            jsonArray.add(gson.fromJson(b.toString(), JsonElement.class));
        }

        return jsonArray.toString();

    }

    /**
     * Get a specific block from the blockchain based on its index.
     *
     * @param i The index of the block to retrieve.
     * @return The block at the specified index.
     */
    public Block getBlock(int i) {
        return blockList.get(i);
    }


    /**
     * Calculate the total difficulty of all blocks in the blockchain.
     *
     * @return The total difficulty of the blockchain.
     */
    public int getTotalDifficulty() {
        int totalDifficulty = 0;
        for (Block block : blockList) {
            totalDifficulty += block.getDifficulty();
        }
        return totalDifficulty;
    }

    /**
     * Calculate the total expected hash calculations required for the entire blockchain.
     *
     * @return The total expected hash calculations needed for the blockchain.
     */
    public double getTotalExpectedHashes() {
        double totalExpectedHashes = 0;
        for (Block block : blockList)
            totalExpectedHashes += Math.pow(16, block.getDifficulty()); // 16 (16 hex characters) ^ difficulty of block
        return totalExpectedHashes;
    }

    /**
     * Check the validity of the blockchain.
     *
     * @return "TRUE" if the blockchain is valid; "FALSE" with an explanation otherwise.
     */
    public String isChainValid() throws NoSuchAlgorithmException {
        // Chain contains only 1 block , i.e. genesis
        if (blockList.size() == 1) {
            Block genesisBlock = this.blockList.get(0);
            String hash = genesisBlock.calculateHash();
            // Calculate prefix based on difficulty, number of leading zeroes based on the difficulty value
            String prefix = new String(new char[genesisBlock.getDifficulty()]).replace("\0", "0");
            if (!hash.substring(0, genesisBlock.getDifficulty()).equals(prefix)) {
                return "FALSE \n Improper hash on genesis node";
            } else if (!chainHash.equals(hash)) {
                return "FALSE \n Chain hash and computed hash do not match";
            } else {
                return "TRUE";
            }
        }

        // More than 1 block
        if (blockList.size() > 1) {
            for (int i = 1; i < blockList.size(); i++) {
                Block currentBlock = this.blockList.get(i);
                Block previousBlock = this.blockList.get(i - 1);

                String hash = currentBlock.calculateHash();
                String hashPointer = currentBlock.getPreviousHash();
                // Calculate prefix based on difficulty, number of leading zeroes based on the difficulty value
                String prefix = new String(new char[currentBlock.getDifficulty()]).replace("\0", "0");

                if (!hash.substring(0, currentBlock.getDifficulty()).equals(prefix))
                    return "FALSE \n Improper hash on node " + i + " Does not begin with " + prefix;
                    // Check proof of work / leading zeros
                else if (!hashPointer.equals(previousBlock.calculateHash()))
                    return "FALSE \n Improper previous hash";
            }
        }

        // Chain hash , check the last element added to to the blocklist
        if (!chainHash.equals(blockList.get(blockList.size() - 1).calculateHash())) {
            return "Chain hash error";
        }

        return "TRUE";
    }

    /**
     * This routine repairs the chain. It checks the hashes of each block and ensures
     * that any illegal hashes are recomputed.
     * After this routine is run, the chain will be valid. The routine does not modify any difficulty values.
     * It computes new proof of work based on the difficulty specified in the Block.
     *
     * @throws NoSuchAlgorithmException
     */
    public void repairChain() throws NoSuchAlgorithmException {

        // Genesis block
        if (blockList.size() == 1) {
            //Reset previous hash and recompute proof of work
            blockList.get(0).setPreviousHash("");
            blockList.get(0).proofOfWork();
        }

        if (blockList.size() > 1) {
            for (int i = 1; i < blockList.size(); i++) {
                // Reset previous hash and recompute proof of work
                blockList.get(i).setPreviousHash(blockList.get(i - 1).calculateHash());
                blockList.get(i).proofOfWork();
            }

            // Reset chain hash
            this.chainHash = blockList.get(blockList.size() - 1).calculateHash();
        }
    }
}


