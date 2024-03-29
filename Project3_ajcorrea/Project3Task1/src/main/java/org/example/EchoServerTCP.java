// Ariane Correa
// ajcorrea

package org.example;

// Code Reference: EchoServerTCP.java from Coulouris text

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Scanner;

public class EchoServerTCP {

    public static void main(String args[]) {

        Socket clientSocket = null;

        System.out.println("Blockchain server running");
        try {
            Scanner sc = new Scanner(System.in);
            int serverPort = 7777;  // Read user input
            ServerSocket listenSocket = new ServerSocket(serverPort);

            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            clientSocket = listenSocket.accept();
            // If we get here, then we are now connected to a client.

            // Set up "in" to read from the client socket
            Scanner in;
            in = new Scanner(clientSocket.getInputStream());

            // Set up "out" to write to the client socket
            PrintWriter out;
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));

            /*
             * Forever,
             *   read a line from the socket
             *   print it to the console
             *   echo it (i.e. write it) back to the client
             */

            BlockChain chain = new BlockChain();
            int option = 0;

            Block genesis = new Block(0, chain.getTime(), "Genesis", 2);
            genesis.setPreviousHash("");
            genesis.proofOfWork();

            chain.computeHashesPerSecond();
            chain.addBlock(genesis);

            System.out.println("We have a visitor");
            while (true) {
                JsonObject clientReply = new JsonParser().parse(in.nextLine()).getAsJsonObject();
                option = clientReply.get("op").getAsInt();
                switch (option) {
                    // Status of blockchain
                    case 0: {
                        ResponseMessage responseMessage = new ResponseMessage(option,
                                chain.getChainSize(), chain.getLatestBlock().getDifficulty(),
                                chain.getTotalDifficulty(), chain.getHashesPerSecond(), chain.getTotalExpectedHashes(),
                                chain.getLatestBlock().getNonce(), chain.getChainHash());
                        System.out.println("Response : " + responseMessage.getResponseJson());
                        out.println(responseMessage.getResponseJson());
                        out.flush();
                        break;
                    }

                    // Adding block to blockchain
                    case 1: {
                        System.out.println("Adding a block");
                        int difficulty = clientReply.get("difficulty").getAsInt();
                        String transaction = clientReply.get("transaction").getAsString();
                        Timestamp start = chain.getTime();
                        Block newBlock = new Block(chain.getChainSize(), chain.getTime(), transaction, difficulty);
                        newBlock.setPreviousHash(chain.getChainHash());
                        newBlock.proofOfWork();
                        chain.addBlock(newBlock);
                        Timestamp end = chain.getTime();
                        ResponseMessage responseMessage = new ResponseMessage(option, "Total execution " +
                                "time to add this block was  " + (end.getTime() - start.getTime()) + " milliseconds");
                        System.out.println("Setting response to : " + responseMessage.getResponseJson());
                        out.println(responseMessage.getResponseJson());
                        out.flush();
                        break;
                    }

                    // Verify blockchain
                    case 2: {
                        System.out.println("Verifying entire chain");
                        Timestamp start = chain.getTime();
                        System.out.println("Chain verification: " + chain.isChainValid());
                        Timestamp end = chain.getTime();
                        ResponseMessage responseMessage = new ResponseMessage(option, "Total execution " +
                                "time to verify the chain was  " + (end.getTime() - start.getTime()) + " milliseconds");
                        System.out.println("Setting response to: " + responseMessage.getResponseJson());
                        out.println(responseMessage.getResponseJson());
                        out.flush();
                        break;
                    }

                    // View blockchain
                    case 3: {
                        System.out.println("View the Blockchain");
                        ResponseMessage responseMessage = new ResponseMessage(option, chain.toString());
                        System.out.println("Setting response to: " + responseMessage.getResponseJson());
                        out.println(responseMessage.getResponseJson());
                        out.flush();
                        break;

                    }

                    // Corrupt blockchain
                    case 4: {
                        System.out.println("Corrupt the Blockchain");
                        int index = clientReply.get("index").getAsInt();
                        String transaction = clientReply.get("transaction").getAsString();
                        chain.getBlock(index).setData(transaction);
                        ResponseMessage responseMessage = new ResponseMessage(option, "Block " + index +
                                " now holds " + transaction);
                        System.out.println("Setting response to: " + responseMessage.getResponseJson());
                        out.println(responseMessage.getResponseJson());
                        out.flush();
                        break;
                    }

                    //  Repair blockchain
                    case 5: {
                        System.out.println("Repairing the entire chain");
                        Timestamp start = chain.getTime();
                        chain.repairChain();
                        Timestamp end = chain.getTime();
                        ResponseMessage responseMessage = new ResponseMessage(option, "Total execution " +
                                "time required to repair the chain was " + (end.getTime() - start.getTime()) +
                                " milliseconds");
                        System.out.println("Setting response to: " + responseMessage.getResponseJson());
                        out.println(responseMessage.getResponseJson());
                        out.flush();
                        break;
                    }

                    // Exit the program
                    case 6:
                        System.exit(0);
                }
            }

            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
            // If quitting (typically by you sending quit signal) clean up sockets
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // Ignore exception on close
            }
        }
    }
}