package cc.pusch.checkproxyaccess;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class Results {
    private Map<URL, int[]> resmap;

    Results() {
        resmap = new HashMap<>();
    }

    synchronized void addResult(URL url, int[] result) {
        resmap.put(url, result);
        System.out.printf("%s;", url.toString());
        for (int aResult : result) {
            System.out.printf("%s;", Integer.toString(aResult));
        }
        System.out.printf("\n");
    }

    void addURL(URL url) {
        resmap.put(url, null);
    }

}
