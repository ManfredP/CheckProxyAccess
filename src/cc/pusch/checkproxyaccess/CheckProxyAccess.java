package cc.pusch.checkproxyaccess;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
        String hostPort2Check;
        Config config = new Config(args);
        Thread[] tPool = new Thread[config.getNumThreads()];
        Results results = new Results();
        try {
            BufferedReader urlsFile = new BufferedReader(new FileReader(config.getInfile()));
            while ((hostPort2Check = urlsFile.readLine()) != null) {
                try {
                    startCheckThread(SquidLog2Url.hostPort2Url(hostPort2Check), tPool, config, results);
                } catch (MalformedURLException ex) {
                    System.out.println("Could not parse " + hostPort2Check);
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
        results.printOutfile(config.getOutFile());
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
}
