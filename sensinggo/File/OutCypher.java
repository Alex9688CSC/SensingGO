package edu.nctu.wirelab.sensinggo.File;

import android.util.Base64;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;

/**
 * Set private key, and used to encrypt
 */
public class OutCypher implements Serializable {
    private String privateKeyString;
    private PrivateKey privateKey;

    public OutCypher(){
        // Version = 5
//        the private key that server provide in pkcs8 format, such as
        privateKeyString = "MIIJQQIBADANBgkqhkiG9w0BAQEFAASCCSswggknAgEAAoICAQCod1qGGKAAWf14\r" +
                "0Q8h4indpdBYxwYzoa20bGGUPEowafELXcBrgk87e4jaYMO5EwDHULJeb1UoSgM3\r" +
                "uV/etdahHP4TPsMHDSW7PHZY7AoKzE/7HrEzGaYcHrXUv+muvcvYVbm1rigE4klD\r" +
                "PovEaxCaKhtKaPRRg0U4g65UKQhAjpMKWDU9eqTkfsnoDy3BCD8Y3OCtSW/nncGX\r" +
                "V8pU1Myrpx3ghhT2A4iSDCBGzpLBk3IRHDMd79kEOYF8WCisxRsN1J0wXwif8X+W\r" +
                "MYuqAMWXZVBfMqeLvIISl6wUF4vHddZxWYK8P6Z5Xhw385vY9RPeViUSoSljsz5Q\r" +
                "YFbnmtdIDWLZBoDUlf5HCFcHFIlwbLKFt445Q2oU1eGf14eQKDai9pHlsjiy0Sl4\r" +
                "y+rTZU6F/zOlinWYOKNJonlqqPw2zIbrB1R9U+nT6cpqCqn9aPiyA8cDFWGiA+/k\r" +
                "PVbNS89hWWnBb/8ETNJEEJJp+9IN6DYmdOF+t8h/VL0sq/DgYxMfOteGZhjtZ4Jc\r" +
                "qlm8iVIPmNbyqe0VDwD2eFw+AlSwCT1tEO4K6JvCWxL4y3ShaRjLslgPvhULrlIX\r" +
                "e3ho1J4gkF/1dMrqbQPB8KeW4rurYgPqKm4NgSMMf/lqoGHt4a9jRBIXZgWgGfj2\r" +
                "txY9lRR0dP07Ql6gC+cMoc3wWkN9RQIDAQABAoICAGeoUN3z5vdwZ8NgN4Rsyb/l\r" +
                "GAaYJ5u7XsuGaKAyXJ/ff/6zaAN+wcvi3jkokyWcqLz7TVVIAcjxiXtPJ8s8bdzi\r" +
                "nv8ufggnsdC8ikF6s08jmg71OqkrngbtaoqGr33isLvjO4qrp1oh9lxW7t8j1apW\r" +
                "QEaPv80sDXqRo8GLIdW/JICX9+JlRXKHBSsxs8Bzl4dv2Rsm9aE5c3XxAcEi0vDH\r" +
                "ul19gwZA9FaQ+Yc38hsD44rdpCJGg2zTCXZ+NEDiYxwmd7Xcs8zeZtf+kmY/ZVUb\r" +
                "dNGutGMJ0aspAiu2tT48pn9GSWgVbhR9sLVIwttDKT+EF/mXioUbNDt2m+KUZuOh\r" +
                "q0fk6wqFjb4iOcIe8Jjwk7lgr69TBnr89ByirmbHL8/Wzege3h+bD4gu5jz219vS\r" +
                "ZgrtGxduy9KwJksgSxF/mEhaMBRz0rsP2ubPPJ08meHxj/IW+u9ugcV2ZxSd2NBj\r" +
                "u9f5vZ03NQbaV0MO6EgjeXjnaYXmh3gXi1WKiEBVxj3IC8YDTCY1VZx4u0tZrU8H\r" +
                "8d6dvd5p1JNKd0RimysTZMK861z62HRiNtKAoflte6vjDlDM/rCufaqh4TC/NkGL\r" +
                "lIJu7iCeg2cEgF6ttz1jdMvgUqCDOHdVwrhUyeKAuteNDsRYVV+VL8KfYr+bI+fj\r" +
                "6cYCcHarrBVxLl0scz69AoIBAQDdWPIVloMaLTTmH7dspIm7LCTQZOSOVA/i+QGN\r" +
                "zkxno51vK8ygKltJMw+gzBpvE8KJ/fod29GJY+Wsib+/ELBVfaKsz6wRouRzuZPV\r" +
                "Lq6ZQEHWmE/QYGzfQM16jU6/cLmdXKv5Btjb9gPxocpPC60t6GfmonQ+ZwB98kCK\r" +
                "GQP6kOaUw7LoP2cYAIUFoCOm88AhVb7JWmK17J5Ofp1kkv2Gkt3f0asH+7GZhMnh\r" +
                "odD1/SCJNwannUs3roBW3vasumxDIAm1HKDri/lYYpslt2NGXAaxej5rIE4ERYvC\r" +
                "lMlGKraSfxd90tzN51zL+JUJMVjIZ88YfFhz9fehyQaKXVS7AoIBAQDC1w92NqYd\r" +
                "9CD4WVLjm/IlF/rUmsiOHuNTK0rBvYWDBCtXr4sTrf7MTwofns8fp3NYc8FOaFGJ\r" +
                "s5HfZKEQSeO006oKQmVs3hKsEm6tknBA5jnTKZxtrCr7px8ww361HhV9KYUv1OXt\r" +
                "Qy7wkGKw37OOn2hz6QlyA6sRgak9YKuVFwfyvMhX5DfxT3zo4HLyP3peKZWlNlUh\r" +
                "M2TL7/TMDVjavsjZp4USTiM9CYWrALtjl4e+m4327PvYa+ha1cb+WGvpagLFjK50\r" +
                "SDfE2Hmbn+MZqubmXTtYtrSdx9RUxpOuj7Mzj/tI5JHnI7Bo8g0X6TVv65pQtTSH\r" +
                "QnfRYOsw3lX/AoIBACtNXEa25Dz5vRCMAeFCKyOv1h/irQffECCSICiyxCW+T21R\r" +
                "Pz3SK3udfqAxbbZvQi6mvgibWVmsbmEbraReSnDfHDT8h6gHmvO8yVv6PvQCKx8l\r" +
                "OvAD/SiLz+b6v/4ZumY7m/UizsfxUAI0aZSfLyj8/16SR3Vxxxnci524mRuqv0E5\r" +
                "K8XdqXh7mYppMrBVnS+oS6OuiAMZIDsw7++eBfUOxFMl/HSaW7sxzCDU/EEzXOsb\r" +
                "FbUyw2VIQRk9kzplWgqjuOJykFSkVIpi+AEf6E76b+DGjnF8p2aUiGvvmZSdhIwS\r" +
                "SvE1kDbD3VaV8eoTek/yZwReRxugcep3nkzxR0MCggEAQ8Tf2OM7NlHyoc8X2Jkm\r" +
                "SSalFQvdVCmEMiptryOHa8GofbBONIMhi9i2EbeAHWd15D8m7mm0aqvtwgDTUMIS\r" +
                "lJPGohjkBFDdt8IVgwPbTYeGd18SzMbEkJFZdPGagc679LPbYn6vlq32hrAB07mL\r" +
                "T8E8aIyWv3RtLQKnZfTY2VuQZSqqmH+FwcY/ERT4hi/0FQprxXI3e/vPRGvq/opp\r" +
                "7HPNkn8E9w/8i79jIc54XzSWf4LFhyx4kJc/01BmDZOdvSslWacMCuAPncUZzzp2\r" +
                "mDfsZhXpjXrHKdYWkYIcXewXjYi4A2uVIAs20dnXGgkAkDQnkYjlg2JoIIiQZYf/\r" +
                "6wKCAQBQAz+qIsDCtd3Jb+k0oub3CPwToov4mqvVRBX5+JwWC/SWvldRM2UVXlz8\r" +
                "rlb1yspoeXvByW7SPVnAjp6dsNSIflC78+1yZVz6ZcBMGpxFxuhJssX3jlxIqX6L\r" +
                "ewX7riBeyJCJudIFJH0VxEfKV9RxuXHrfNE3nfzevxH7UH1Xs9l/viMuUeJOTgHE\r" +
                "qO4wWZe+3a+qKd7yhGhSPAZnIuIS86lgyb/1NgVC08Dimq/nqCHLvvDiFgePqCrH\r" +
                "Eu5ha7U7qe72ptaEzSrAXnkFyl7AIkTIPt5G6N7uA9Ia9Y2CONW+aJlsNFBuzJUM\r" +
                "XrlOZGKTmOXNiIcG91pHXatm9hbW\r";
        privateKey = loadPrivateKey(privateKeyString);
    }

    public byte[] signString(String plaintext){
        byte[] signature = null;
        Signature instance = null;
        try {
            instance = Signature.getInstance("SHA1withRSA");
            instance.initSign(privateKey);
            instance.update((plaintext).getBytes());
            signature = instance.sign();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return signature;
    }

    private PrivateKey loadPrivateKey(String key64) {
        byte[] clear = Base64.decode(key64, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(clear);
        KeyFactory fact;
        try {
            fact = KeyFactory.getInstance("RSA");
            PrivateKey priv = fact.generatePrivate(keySpec);
            Arrays.fill(clear, (byte) 0);
            return priv;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void openAndWriteFileInBytes(byte[] bytes, String FilePath){
        File file = new File(FilePath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(bytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
