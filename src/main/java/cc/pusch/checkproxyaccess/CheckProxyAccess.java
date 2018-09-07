package cc.pusch.checkproxyaccess;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

class CheckProxyAccess {
    public static void main(String[] args) {
        String[] propertiesToClear = {
                "http.proxyHost",
                "http.proxyPort",
                "https.proxyHost",
                "https.proxyPort",
                "ftp.proxyHost",
                "ftp.proxyPort",
                "socksProxyHost",
                "socksProxyPort"
        };
        for (String propertyToClear : propertiesToClear) {
            System.clearProperty(propertyToClear);
        }
        System.setProperty("java.net.useSystemProxies", "false");
        String inputLine2Check;
        TrustManager[] trustAll = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {

                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
        };
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAll, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        } catch (java.security.NoSuchAlgorithmException | java.security.KeyManagementException ex) {
            System.out.println("Failed to install trustAll TrustManager");
        }
        HostnameVerifier noHostnameVerify =
                (String s, SSLSession sslSession) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(noHostnameVerify);
        Config config = new Config(args);
        Thread[] tPool = new Thread[config.getNumThreads()];
        Results results = new Results(config.getConsoleOutput());
        try {
            BufferedReader urlsFile = new BufferedReader(new FileReader(config.getInfile()));
            while ((inputLine2Check = urlsFile.readLine()) != null) {
                try {
                    startCheckThread(InputLine2URL(inputLine2Check), tPool, config, results);
                } catch (MalformedURLException ex) {
                    System.out.println("Could not parse " + inputLine2Check);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Could not open " + config.getInfile() + " for reading");
        } catch (IOException ex) {
            System.out.println("I/O Error while reading " + config.getInfile());
        }
        for (Thread aTPool : tPool) {
            try {
                if (aTPool != null) {
                    aTPool.join();
                }
            } catch (InterruptedException ignored) {
            }
        }
        config.getOutFile().ifPresent(results::printOutfile);
    }

    private static void startCheckThread(URL url2Check, Thread[] tPool, Config config, Results results) {
        boolean done = false;
        while (!done) {
            for (int i = 0; i < tPool.length; i++) {
                if (tPool[i] == null || tPool[i].getState() == Thread.State.TERMINATED) {
                    tPool[i] = new Thread(new Worker(url2Check, config.getProxyList(), results));
                    tPool[i].start();
                    done = true;
                    break;
                }
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private static URL InputLine2URL(String inputLine) throws MalformedURLException {
        if (inputLine.matches("^(https?|ftp)://.*")) {
            return new URL(inputLine);
        } else {
            String[] hostPortSplit = inputLine.split(":", 2);
            String protocol;
            int port;
            if (hostPortSplit.length == 2) {
                port = Integer.parseUnsignedInt(hostPortSplit[1]);
                if (port == 443 || port == 8443) {
                    protocol = "https";
                } else {
                    protocol = "http";
                }
                return new URL(protocol, hostPortSplit[0], port, "");
            } else {
                return new URL("http", hostPortSplit[0], "");
            }
        }
    }
}
