// Ariane Correa
// ajcorrea

package org.example;

public class BlockHelper {

    /**
     * Converts a byte array into a hexadecimal string.
     *
     * @param hash The byte array to be converted.
     * @return The hexadecimal string representation of the byte array.
     */

    // Code Reference: https://www.baeldung.com/sha-256-hashing-java
    public static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
