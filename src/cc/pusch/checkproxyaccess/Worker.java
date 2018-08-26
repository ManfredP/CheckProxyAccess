package cc.pusch.checkproxyaccess;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

class Worker implements Runnable {
    private final URL url;
    private final Proxy[] proxy;
    private final Results results;

    Worker(URL url, Proxy[] proxy, Results results) {
        this.url = url;
        this.proxy = proxy;
        this.results = results;
    }

    public void run() {
        int[] result = new int[proxy.length];
        for (int i = 0; i < proxy.length; i++) {
            result[i] = checkHttpConn(url, proxy[i]);
        }
        results.addResult(url, result);
    }

    private int checkHttpConn(URL url, Proxy proxy) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
            return conn.getResponseCode();
        } catch (IOException ex) {
            return 500;
        }
    }
}
