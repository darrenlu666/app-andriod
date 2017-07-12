package com.codyy.erpsportal.commons.utils;

import java.security.Provider;

/**
 * To fix problem java.security.NoSuchProviderException: no such provider: Crypto int Android N
 * https://android-developers.googleblog.com/2016/06/security-crypto-provider-deprecated-in.html
 * Created by lijian on 2017/7/5.
 */

public class CryptoProvider extends Provider {
    public CryptoProvider() {
        super("Crypto", 1.0, "HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
        put("SecureRandom.SHA1PRNG", "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
        put("SecureRandom.SHA1PRNG ImplementedIn", "Software");
    }
}
