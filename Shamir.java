import org.json.JSONObject;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Scanner;

public class Shamir {

    public static void main(String[] args) throws FileNotFoundException, JSONException {
        String jsonInput = readFile("input.json");
        JSONObject jsonObject = new JSONObject(jsonInput);
        JSONObject keys = jsonObject.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        BigInteger[][] points = new BigInteger[n][2];
        int pointIndex = 0;

        // Iterate over all keys in the JSON object
        for (String key : jsonObject.keySet()) {
            // Skip the "keys" key
            if (key.equals("keys")) {
                continue;
            }

            JSONObject point = jsonObject.getJSONObject(key);
            points[pointIndex][0] = BigInteger.valueOf(Integer.parseInt(key)); // Use the key as the x-coordinate
            points[pointIndex][1] = new BigInteger(point.getString("value"), Integer.parseInt(point.getString("base")));
            pointIndex++;
        }

        BigInteger c = findSecret(points, k);

        System.out.println("The secret c is: " + c);
    }

    private static String readFile(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
        String jsonInput = scanner.useDelimiter("\\Z").next();
        scanner.close();
        return jsonInput;
    }

    private static BigInteger findSecret(BigInteger[][] points, int k) {
        BigInteger c = BigInteger.ZERO;
        for (int i = 0; i < k; i++) {
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(points[j][0].negate());
                    denominator = denominator.multiply(points[i][0].subtract(points[j][0]));
                }
            }
            BigInteger term = points[i][1].multiply(numerator).divide(denominator);
            c = c.add(term);
        }
        return c;
    }
}