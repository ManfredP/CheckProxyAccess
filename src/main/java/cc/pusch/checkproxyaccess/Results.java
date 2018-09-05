package cc.pusch.checkproxyaccess;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

class Results {
    private Map<URL, int[]> resmap;
    private boolean printConsoleOutput;

    Results(boolean printConsoleOutput) {
        resmap = new HashMap<>();
        this.printConsoleOutput = printConsoleOutput;
    }

    synchronized void addResult(URL url, int[] result) {
        resmap.put(url, result);
        if(printConsoleOutput) {
            System.out.printf("%s;", url.toString());
            for (int aResult : result) {
                System.out.printf("%s;", Integer.toString(aResult));
            }
            System.out.printf("\n");
        }
    }

    void addURL(URL url) {
        resmap.put(url, null);
    }

    void printOutfile(String outFile) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
            for (URL url : resmap.keySet()) {
                out.write(url.toString() + ";");
                for (int retcode : resmap.get(url)) {
                    out.write(Integer.toString(retcode) + ";");
                }
                out.write("\n");
            }
            out.close();
        } catch (IOException ex) {
            System.out.println("Writing output file " + outFile + "failed");
        }

    }
}
