/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dot.gcpbasedot.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author lacastrillov
 */
public class AESEncrypt {

    /**
     * The Builder used to create the Encryption instance and that contains the
     * information about encryption specifications, this instance need to be
     * private and careful managed
     */
    private final Builder mBuilder;

    /**
     * The private and unique constructor, you should use the Encryption.Builder
     * to build your own instance or get the default proving just the sensible
     * information about encryption
     */
    private AESEncrypt(Builder builder) {
        mBuilder = builder;
    }

    /**
     * @param salt
     * @return an default encryption instance or {@code null} if occur some
     * Exception, you can create yur own Encryption instance using the
     * Encryption.Builder
     */
    public static AESEncrypt getDefault(String salt) {
        try {
            return Builder.getDefaultBuilder(salt).build();
        } catch (NoSuchAlgorithmException e) {
            System.out.print(e);
            return null;
        }
    }

    /**
     * Encrypt a String
     *
     * @param data the String to be encrypted
     * @param key
     *
     * @return the encrypted String or {@code null} if you send the data as
     * {@code null}
     * @throws NullPointerException if the Builder digest algorithm is
     * {@code null} or if the specified Builder secret key type is {@code null}
     * @throws IllegalStateException if the cipher instance is not initialized
     * for encryption or decryption
     */
    public String encrypt(String data, String key) {
        try {
            if (data == null) {
                return null;
            }
            SecretKey secretKey = getSecretKey(hashTheKey(key));
            byte[] dataBytes = data.getBytes(mBuilder.getCharsetName());
            Cipher cipher = Cipher.getInstance(mBuilder.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, mBuilder.getSecureRandom());
            return Base64.encodeBase64String(cipher.doFinal(dataBytes));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }

    /**
     * Decrypt a String
     *
     * @param data the String to be decrypted
     * @param key
     *
     * @return the decrypted String or {@code null} if you send the data as
     * {@code null}
     * @throws NullPointerException if the Builder digest algorithm is
     * {@code null} or if the specified Builder secret key type is {@code null}
     * @throws IllegalStateException if the cipher instance is not initialized
     * for encryption or decryption
     */
    public String decrypt(String data, String key) {
        if (data == null) {
            return null;
        }
        byte[] dataBytes = Base64.decodeBase64(data);
        SecretKey secretKey;
        try {
            secretKey = getSecretKey(hashTheKey(key));
            Cipher cipher = Cipher.getInstance(mBuilder.getAlgorithm());
            //cipher.init(Cipher.DECRYPT_MODE, secretKey, mBuilder.getIvParameterSpec(), mBuilder.getSecureRandom());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, mBuilder.getSecureRandom());
            byte[] dataBytesDecrypted = (cipher.doFinal(dataBytes));
            
            return new String(dataBytesDecrypted);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(AESEncrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
            
        return data;
    }

    /**
     * creates a 128bit salted aes key
     *
     * @param key encoded input key
     *
     * @return aes 128 bit salted key
     *
     * @throws NoSuchAlgorithmException if no installed provider that can
     * provide the requested by the Builder secret key type
     * @throws UnsupportedEncodingException if the Builder charset name is not
     * supported
     * @throws InvalidKeySpecException if the specified key specification cannot
     * be used to generate a secret key
     * @throws NullPointerException if the specified Builder secret key type is
     * {@code null}
     */
    private SecretKey getSecretKey(char[] key) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance(mBuilder.getSecretKeyType());
        KeySpec spec = new PBEKeySpec(key, mBuilder.getSalt().getBytes(mBuilder.getCharsetName()), mBuilder.getIterationCount(), mBuilder.getKeyLength());
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), mBuilder.getAlgorithm());
    }

    /**
     * takes in a simple string and performs an sha1 hash that is 128 bits
     * long...we then base64 encode it and return the char array
     *
     * @param key simple inputted string
     *
     * @return sha1 base64 encoded representation
     *
     * @throws UnsupportedEncodingException if the Builder charset name is not
     * supported
     * @throws NoSuchAlgorithmException if the Builder digest algorithm is not
     * available
     * @throws NullPointerException if the Builder digest algorithm is
     * {@code null}
     */
    private char[] hashTheKey(String key) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(mBuilder.getDigestAlgorithm());
        messageDigest.update(key.getBytes(mBuilder.getCharsetName()));
        return Base64.encodeBase64String(messageDigest.digest()).toCharArray();
    }

    /**
     * When you encrypt or decrypt in callback mode you get noticed of result
     * using this interface
     */
    public static interface Callback {

        /**
         * Called when encrypt or decrypt job ends and the process was a success
         *
         * @param result the encrypted or decrypted String
         */
        public void onSuccess(String result);

        /**
         * Called when encrypt or decrypt job ends and has occurred an error in
         * the process
         *
         * @param exception the Exception related to the error
         */
        public void onError(Exception exception);

    }

    /**
     * This class is used to create an Encryption instance, you should provide
     * ALL data or start with the Default Builder provided by the
     * getDefaultBuilder method
     */
    public static class Builder {

        private int mKeyLength;
        private int mIterationCount;
        private String mSalt;
        private String mAlgorithm;
        private String mCharsetName;
        private String mSecretKeyType;
        private String mDigestAlgorithm;
        private String mSecureRandomAlgorithm;
        private SecureRandom mSecureRandom;

        /**
         * @param salt
         * @return an default builder with the follow defaults: the default char
         * set is UTF-8 the default base mode is Base64 the Secret Key Type is
         * the PBKDF2WithHmacSHA1 the default salt is "some_salt" but can be
         * anything the default length of key is 128 the default iteration count
         * is 65536 the default algorithm is AES in CBC mode and PKCS 5 Padding
         * the default secure random algorithm is SHA1PRNG the default message
         * digest algorithm SHA1
         */
        public static Builder getDefaultBuilder(String salt) {
            return new Builder()
                    .setSalt(salt)
                    .setKeyLength(128)
                    .setCharsetName(Charset.defaultCharset().toString())
                    .setIterationCount(65536)
                    .setDigestAlgorithm("SHA1")
                    .setAlgorithm("AES")//AES/CBC/PKCS5Padding
                    .setSecureRandomAlgorithm("SHA1PRNG")
                    .setSecretKeyType("PBKDF2WithHmacSHA1");
        }

        /**
         * Build the Encryption with the provided information
         *
         * @return a new Encryption instance with provided information
         *
         * @throws NoSuchAlgorithmException if the specified
         * SecureRandomAlgorithm is not available
         * @throws NullPointerException if the SecureRandomAlgorithm is
         * {@code null} or if the IV byte array is null
         */
        public AESEncrypt build() throws NoSuchAlgorithmException {
            setSecureRandom(SecureRandom.getInstance(getSecureRandomAlgorithm()));
            return new AESEncrypt(this);
        }

        //region getters and setters
        /**
         * @return the charset name
         */
        private String getCharsetName() {
            return mCharsetName;
        }

        /**
         * @param charsetName the new charset name
         *
         * @return this instance to follow the Builder patter
         */
        public Builder setCharsetName(String charsetName) {
            mCharsetName = charsetName;
            return this;
        }

        /**
         * @return the algorithm
         */
        private String getAlgorithm() {
            return mAlgorithm;
        }

        /**
         * @param algorithm the algorithm to be used
         *
         * @return this instance to follow the Builder patter
         */
        public Builder setAlgorithm(String algorithm) {
            mAlgorithm = algorithm;
            return this;
        }

        /**
         * @return the type of aes key that will be created, on KITKAT+ the API
         * has changed, if you are getting problems please @see <a
         * href="http://android-developers.blogspot.com.br/2013/12/changes-to-secretkeyfactory-api-in.html">http://android-developers.blogspot.com.br/2013/12/changes-to-secretkeyfactory-api-in.html</a>
         */
        private String getSecretKeyType() {
            return mSecretKeyType;
        }

        /**
         * @param secretKeyType the type of AES key that will be created, on
         * KITKAT+ the API has changed, if you are getting problems please @see <a
         * href="http://android-developers.blogspot.com.br/2013/12/changes-to-secretkeyfactory-api-in.html">http://android-developers.blogspot.com.br/2013/12/changes-to-secretkeyfactory-api-in.html</a>
         *
         * @return this instance to follow the Builder patter
         */
        public Builder setSecretKeyType(String secretKeyType) {
            mSecretKeyType = secretKeyType;
            return this;
        }

        /**
         * @return the value used for salting
         */
        private String getSalt() {
            return mSalt;
        }

        /**
         * @param salt the value used for salting
         *
         * @return this instance to follow the Builder patter
         */
        public Builder setSalt(String salt) {
            mSalt = salt;
            return this;
        }

        /**
         * @return the length of key
         */
        private int getKeyLength() {
            return mKeyLength;
        }

        /**
         * @param keyLength the length of key
         *
         * @return this instance to follow the Builder patter
         */
        public Builder setKeyLength(int keyLength) {
            mKeyLength = keyLength;
            return this;
        }

        /**
         * @return the number of times the password is hashed
         */
        private int getIterationCount() {
            return mIterationCount;
        }

        /**
         * @param iterationCount the number of times the password is hashed
         *
         * @return this instance to follow the Builder patter
         */
        public Builder setIterationCount(int iterationCount) {
            mIterationCount = iterationCount;
            return this;
        }

        /**
         * @return the algorithm used to generate the secure random
         */
        private String getSecureRandomAlgorithm() {
            return mSecureRandomAlgorithm;
        }

        /**
         * @param secureRandomAlgorithm the algorithm to generate the secure
         * random
         *
         * @return this instance to follow the Builder patter
         */
        public Builder setSecureRandomAlgorithm(String secureRandomAlgorithm) {
            mSecureRandomAlgorithm = secureRandomAlgorithm;
            return this;
        }

        /**
         * @return the SecureRandom
         */
        private SecureRandom getSecureRandom() {
            return mSecureRandom;
        }

        /**
         * @param secureRandom the Secure Random
         *
         * @return this instance to follow the Builder patter
         */
        public Builder setSecureRandom(SecureRandom secureRandom) {
            mSecureRandom = secureRandom;
            return this;
        }

        /**
         * @return the message digest algorithm
         */
        private String getDigestAlgorithm() {
            return mDigestAlgorithm;
        }

        /**
         * @param digestAlgorithm the algorithm to be used to get message digest
         * instance
         *
         * @return this instance to follow the Builder patter
         */
        public Builder setDigestAlgorithm(String digestAlgorithm) {
            mDigestAlgorithm = digestAlgorithm;
            return this;
        }

        //endregion
    }

}
