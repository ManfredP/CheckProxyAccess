package cc.pusch.checkproxyaccess;

import java.net.MalformedURLException;
import java.net.URL;

class SquidLog2Url {

    static URL hostPort2Url(String hostPort) throws MalformedURLException {
        String[] hostPortSplit = hostPort.split(":", 2);
        String protocol;
        URL url;
        int port;
        if (hostPortSplit.length == 2) {
            port = Integer.parseUnsignedInt(hostPortSplit[1]);
            if (port == 443) {
                protocol = "https";
            } else {
                protocol = "http";
            }
            url = new URL(protocol, hostPortSplit[0], port, "");
        } else {
            url = new URL("http", hostPortSplit[0], "");
        }
        return (url);
    }
}
