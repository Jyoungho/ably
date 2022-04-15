package app.project.ably.core.config;

import app.project.ably.common.utill.CipherUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CipherService {

    @Value("${app.cipher.key}")
    private String key;
    @Value("${app.cipher.salt}")
    private String salt;

    public String encrypt(String text){
        String result = null;
        result = new CipherUtil(key, salt).encrypt(text);
        return result;
    }

    public String decrypt(String encryptedText) {
        String result = null;
        result = new CipherUtil(key, salt).decrypt(encryptedText);
        return result;
    }
}
