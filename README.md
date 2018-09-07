## CheckProxyAccess

A tool to check a list of web servers over different proxy servers. Very useful for migrations, to check if the new
proxy servers can reach all the sites.

## Installation

Use [Maven](https://maven.apache.org) to build the jar file:
```
mvn package
```
This will create a jar file named checkproxyaccess-${VERSION}.jar in the target directory. Alternatively you can just
use javac to compile all the java files in src/main/java/cc/pusch/checkproxyaccess by hand, because this programm does
not have any dependencies to external libraries.

## Usage

### Command line syntax
```
java -jar checkproxyaccess-1.0.jar -i input_file -p proxy1 [-p proxy2, -p proxy3, ...] [-o out_file] [-v] [-t num_threads]
```

#### -i The input file
First you need an input file with the targets to check. The entries can be full URLs like
`https://www.example.com/somepath/somedoc.html` or `host[:port]` combinations like `somehost.example.com`,
`otherhost.example.com:8080`. If you don't specify a protocol we treat everything as http except connections to port 443
and port 8443 these will be treated as https connections.

#### -p The proxies to check
Proxy servers to check have to be in the well known form `protocol://hostname:port` the only execption is `DIRECT` which
uses no proxy. The whole purpose of this program is to test connections over multiple proxy servers at once, so you can
have more than one `-p` options.

#### -o The out file
If no out file is specified the results are just printed to stdout, which is fine for small tests but maybe not so fine
if you test thousands of sites, this is where the output file might get handy.

#### -v Be "verbose"
This is not the classical "print lots of information during the run" type of verbose switch. It just enables the stdout
printing of the results, even when an output file is set.

#### -t The number of threads
That is just how many checks we do in parallel.

### The output format
Last but not least, the output format. The whole purpose of this program is to test the reachability of a huge list of
web servers over different proxy servers. So the output contains the url and the http reply code received from the web
servers in a semicolon separated list. The ordering of the results is identical to the ordering of the `-p` options.
Here a short example:
```
$ java -jar checkproxyaccess-1.0-SNAPSHOT.jar -i 2test -p http://some.proxy:8080 -p DIRECT
https://www.github.com:443;200;200;
http://www.reddit.com;500;301;
```
This tells us that the http requests to GitHub produced the same results over the proxy and directly. On the other hand
reddit was not reachable over the proxy server (HTTP reply code 500), while there was no problem reaching the site
directly (HTTP reply code 301).

I use awk to process the output further for example
```
awk -F \; '$2 != $3 {print $1}'
```
would print out all urls that had different results.
