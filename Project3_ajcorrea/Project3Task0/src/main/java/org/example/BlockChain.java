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
import java.util.Scanner;

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
            byte[] encodedHash = md.digest("00000000".getBytes(StandardCharsets.UTF_8));
            i += 1;
        }
        Timestamp endTime = getTime();
        this.hashesPerSecond = 2000000 / (int) (endTime.getTime() - startTime.getTime());
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
     * Main method to interact with the blockchain.
     *
     *For difficulty = 2
     * Add block 1: 3 ms
     * Add block 2: 4 ms
     * Add block 3: 3 ms
     * Verify: 2 ms
     * Repair: 17 ms
     *
     * For difficulty = 3
     * Add block 1: 32 ms
     * Add block 2: 33 ms
     * Add block 3: 3 ms
     * Verify: 1 ms
     * Repair: 13 ms
     *
     * For difficulty = 4
     * Add block 1: 156 ms
     * Add block 2: 86 ms
     * Add block 3: 50 ms
     * Verify: 1 ms
     * Repair: 400 ms
     *
     * For difficulty = 5
     * Add block 1: 808 ms
     * Add block 2: 2894 ms
     * Add block 3: 1274 ms
     * Verify: 2 ms
     * Repair: 3507 ms
     *
     * As the difficulty level increases, the time required to add new blocks grows substantially.
     * This is expected because finding a valid nonce becomes more computationally intensive.
     *
     * Verifying the chain remains relatively fast and consistent, regardless of the difficulty level.
     * Verifying the proof of work in existing blocks doesn't become significantly harder as the difficulty increases.
     *
     * Repairing the chain becomes increasingly time-consuming with higher difficulty levels.
     * Recomputing the proof of work for each block, especially for a longer chain, can be a resource-intensive task.
     *
     * The repair operation time is directly affected by the chain length.
     * As the chain grows, the time to repair it also increases.
     *
     * These results illustrate the trade-off between security and performance in a blockchain system.
     * Higher difficulty levels improve security by making it more difficult for malicious actors to alter the chain.
     * However, this increased security comes at the cost of longer block creation and chain repair times.
     *
     * @param args Command-line arguments (not used).
     * @throws NoSuchAlgorithmException If SHA-256 algorithm is not available.
     */
    public static void main(String args[]) throws NoSuchAlgorithmException {
        // Create a new blockchain instance
        BlockChain chain = new BlockChain();

        // Adding genesis block to the chain
        Block genesis = new Block(0, chain.getTime(), "Genesis", 2);
        genesis.setPreviousHash("");
        genesis.proofOfWork();
        chain.computeHashesPerSecond();
        chain.addBlock(genesis);

        // Create a scanner for user input
        Scanner sc = new Scanner(System.in);
        int option = 0;

        while (true) {
            System.out.println("0. View basic blockchain status.\n" +
                    "1. Add a transaction to the blockchain.\n" +
                    "2. Verify the blockchain.\n" +
                    "3. View the blockchain.\n" +
                    "4. Corrupt the chain.\n" +
                    "5. Hide the corruption by repairing the chain.\n" +
                    "6. Exit");
            option = sc.nextInt();
            switch (option) {
                case 0: {
                    // Status of blockchain
                    System.out.println("Current size of chain:  " + chain.getChainSize());
                    System.out.println("Difficulty of most recent block: " + chain.getLatestBlock().getDifficulty());
                    System.out.println("Total difficulty for all blocks:  " + chain.getTotalDifficulty());
                    System.out.println("Approximate hashes per second " +
                            "on this machine: " + chain.getHashesPerSecond());
                    System.out.println("Expected total hashes required for " +
                            "the whole chain: " + chain.getTotalExpectedHashes());
                    System.out.println("Nonce for the most recent block: " + chain.getLatestBlock().getNonce());
                    System.out.println("Chain hash:  " + chain.getChainHash() + "\n");
                    break;
                }

                // Adding block to blockchain
                case 1: {
                    System.out.println("Enter difficulty > 0 \n");
                    int difficulty = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter transaction:  \n");
                    String transaction = sc.nextLine();
                    Timestamp start = chain.getTime();
                    // Add new block to the chain
                    Block newBlock = new Block(chain.getChainSize(), chain.getTime(), transaction, difficulty);
                    newBlock.setPreviousHash(chain.getChainHash());
                    newBlock.proofOfWork();
                    chain.addBlock(newBlock);
                    Timestamp end = chain.getTime();
                    System.out.println("Total execution time to add this block was  " +
                            (end.getTime() - start.getTime()) + " milliseconds");
                    break;
                }

                // Verify blockchain
                case 2: {
                    Timestamp start = chain.getTime();
                    System.out.println("Chain verification: " + chain.isChainValid());
                    Timestamp end = chain.getTime();
                    System.out.println("Total execution time to verify the chain was  " +
                            (end.getTime() - start.getTime()) + " milliseconds");
                    break;
                }

                // View blockchain
                case 3: {
                    System.out.println("View the Blockchain");
                    System.out.println(chain.toString());
                    break;
                }

                // Corrupt blockchain
                case 4: {
                    System.out.println("corrupt the Blockchain");
                    System.out.println("Enter block ID of block to corrupt");
                    int index = sc.nextInt();
                    sc.nextLine();
                    System.out.println("Enter new data for block " + index);
                    String transaction = sc.nextLine();
                    // Setting data to the selected block in the chain
                    chain.getBlock(index).setData(transaction);
                    System.out.println("Block " + index + " now holds " + transaction);
                    break;
                }

                //  Repair blockchain
                case 5: {
                    Timestamp start = chain.getTime();
                    chain.repairChain();
                    Timestamp end = chain.getTime();
                    System.out.println("Total execution time required to repair the chain was " +
                            (end.getTime() - start.getTime()) + " milliseconds");
                    break;
                }

                // Exit the program
                case 6:
                    System.exit(0);
            }
        }
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
            totalExpectedHashes += Math.pow(16, block.getDifficulty());  // 16 (16 hex characters) ^ difficulty of block
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

        // Chain contains more than 1 block
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

        // A chain hash, check the last element added to to the blocklist
        if (!chainHash.equals(blockList.get(blockList.size() - 1).calculateHash())) {
            return "Chain hash error";
        }

        return "TRUE";
    }

    public void repairChain() throws NoSuchAlgorithmException {
        // Genesis block
        if (blockList.size() == 1) {
            // Reset previous hash and recompute proof of work
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
