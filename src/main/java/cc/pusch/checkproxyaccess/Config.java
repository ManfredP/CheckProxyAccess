package cc.pusch.checkproxyaccess;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.net.Proxy.Type.DIRECT;
import static java.net.Proxy.Type.HTTP;
import static java.net.Proxy.Type.SOCKS;

class Config {
    private final Proxy[] proxies;
    private String inFile;
    private String outFile;
    private boolean consoleOutput;
    private int numThreads;

    Config(String[] args) {
        numThreads = 1;
        consoleOutput = false;
        int numOfProxies = 0;
        for (String arg : args) {
            if (arg.equals("-p")) {
                numOfProxies++;
            }
        }
        proxies = new Proxy[numOfProxies];
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-p":
                    if (args.length > i + 1) {
                        createProxy(args[i + 1]);
                        i++;
                    } else {
                        System.out.println("Option -p requires an argument");
                        System.exit(1);
                    }
                    break;
                case "-i":
                    if (args.length > i + 1) {
                        setInfile(args[i + 1]);
                        i++;
                    } else {
                        System.out.println("Option -i requires an argument");
                        System.exit(1);
                    }
                    break;
                case "-o":
                    if (args.length > i + 1) {
                        setOutFile(args[i + 1]);
                        i++;
                    } else {
                        System.out.println("Option -o requires an argument");
                        System.exit(1);
                    }
                    break;
                case "-t":
                    if (args.length > i + 1) {
                        setNumThreads(Integer.parseInt(args[i + 1]));
                        i++;
                    } else {
                        System.out.println("Option -t requires an argument");
                        System.exit(1);
                    }
                    break;
                case "-v":
                    consoleOutput = true;
                    break;
                case "--help":
                    printUsage();
                    System.exit(0);
                    break;
            }
        }
        if (numOfProxies < 1) {
            System.out.println("You have to specify at least one proxy parameter");
            System.exit(1);
        }
        if (numThreads < 1) {
            System.out.println("You need at least one thread");
            System.exit(1);
        }
        if (inFile == null || inFile.isEmpty()) {
            System.out.println("Invalid input file");
            System.exit(1);
        }
        if (outFile == null) {
            consoleOutput = true;
        }
    }

    private void createProxy(String proxyStr) {
        if (proxyStr.equals("DIRECT")) {
            addProxy(Proxy.NO_PROXY);
        } else {
            String pattern = "^(http|socks)://([a-z0-9\\.\\-]+):([0-9]+)/?";
            Pattern regEx = Pattern.compile(pattern);
            Matcher matcher = regEx.matcher(proxyStr.toLowerCase());
            if (matcher.find()) {
                Proxy.Type ptype = DIRECT;
                switch (matcher.group(1)) {
                    case "http":
                        ptype = HTTP;
                        break;
                    case "socks":
                        ptype = SOCKS;
                        break;
                    default:
                        System.out.println("Proxy protocol " + matcher.group(1) + " not supported");
                        System.exit(1);
                }
                addProxy(new Proxy(ptype, new InetSocketAddress(matcher.group(2), Integer.parseInt(matcher.group(3)))));
            } else {
                System.out.println("Could not parse Proxy: " + proxyStr);
                System.exit(1);
            }
        }

    }

    private void addProxy(Proxy proxy) {
        for (int i = 0; i < this.proxies.length; i++) {
            if (this.proxies[i] == null) {
                this.proxies[i] = proxy;
                break;
            }
        }
    }

    private void setInfile(String infile) {
        this.inFile = infile;
    }

    private void setOutFile(String outFile) {
        this.outFile = outFile;
    }

    private void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    private void printUsage() {
        System.out.println("usage: checkproxyaccess -i infile -p proxy_or_DIRECT [-o outfile] [-t num_threads] [-v]");
    }

    Proxy[] getProxyList() {
        return (proxies);
    }

    String getInfile() {
        return inFile;
    }

    String getOutFile() {
        return outFile;
    }

    int getNumThreads() {
        return numThreads;
    }

    boolean getConsoleOutput() {
        return consoleOutput;
    }
}
