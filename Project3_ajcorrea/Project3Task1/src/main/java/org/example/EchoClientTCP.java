// Ariane Correa
// ajcorrea

package org.example;

// Code Reference: EchoServerTCP.java from Coulouris text

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class EchoClientTCP {
    static Socket clientSocket = null;

    public static void main(String args[]) {
        // Arguments supply hostname
        Scanner sc = new Scanner(System.in);
        try {
            System.out.println("The TCP client is running.");

            clientSocket = new Socket("localhost", 7777);

            // To read from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // To write to the server
            PrintWriter out = new PrintWriter(new BufferedWriter
                    (new OutputStreamWriter(clientSocket.getOutputStream())));

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
                    // Status of blockchain
                    case 0: {
                        RequestMessage requestMessage = new RequestMessage(option);
                        out.println(requestMessage.getRequestJson());
                        out.flush();
                        String reply = in.readLine();
                        // Parsing response object from server as json object
                        JsonObject jsonReply = new JsonParser().parse(reply).getAsJsonObject();
                        System.out.println("Current size of chain:  " + jsonReply.get("size"));
                        System.out.println("Difficulty of most recent block: " + jsonReply.get("diff"));
                        System.out.println("Total difficulty for all blocks:  " + jsonReply.get("totalDiff"));
                        System.out.println("Approximate hashes per second on this machine : " + jsonReply.get("hps"));
                        System.out.println("Expected total hashes required for the whole chain: " +
                                jsonReply.get("totalHashes"));
                        System.out.println("Nonce for most recent block:  " + jsonReply.get("recentNonce"));
                        System.out.println("Chain hash:  " + jsonReply.get("chainHash") + "\n");
                        break;
                    }

                    // Adding block to blockchain
                    case 1: {
                        System.out.println("Enter difficulty > 0 \n");
                        int difficulty = sc.nextInt();
                        sc.nextLine();
                        System.out.println("Enter transaction:  \n");
                        String transaction = sc.nextLine();
                        RequestMessage requestMessage = new RequestMessage(option, difficulty, transaction);
                        out.println(requestMessage.getRequestJson());
                        out.flush();
                        String reply = in.readLine();
                        // Parsing response object from server as json object
                        JsonObject jsonReply = new JsonParser().parse(reply).getAsJsonObject();
                        System.out.println(jsonReply.get("response"));
                        break;
                    }


                    // Verify, View, and Repair blockchain
                    case 2, 3, 5: {

                        RequestMessage requestMessage = new RequestMessage(option);
                        out.println(requestMessage.getRequestJson());
                        out.flush();
                        String reply = in.readLine();
                        // Parsing response object from server as json object
                        JsonObject jsonReply = new JsonParser().parse(reply).getAsJsonObject();
                        System.out.println(jsonReply.get("response"));
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
                        RequestMessage requestMessage = new RequestMessage(option, index, transaction);
                        out.println(requestMessage.getRequestJson());
                        out.flush();
                        String reply = in.readLine();
                        //Parsing response object from server as json object
                        JsonObject jsonReply = new JsonParser().parse(reply).getAsJsonObject();
                        System.out.println(jsonReply.get("response"));
                        break;
                    }

                    // Exit the program
                    case 6:
                        RequestMessage requestMessage = new RequestMessage(option);
                        out.println(requestMessage.getRequestJson());
                        out.flush();
                        System.exit(0);


                }
            }
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
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

