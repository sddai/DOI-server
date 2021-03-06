# Usage

## Table of contents
1. [Getting Help](#getting_help)
2. [Getting Version](#getting_version)
3. [Creating the configuration file](#creatin_conf_file)
4. [Encrypting a password](#encrypting_password)
    1. [Encrypting the password with the default key](#encrypting_password_default_key)
    2. [Encrypting the password with a custom key](#encrypting_password_custom_key)
5. [Decrypting the password](#decrypting_password)
    1. [Decrypting the password with the default key](#decrypting_password_default_key)
    2. [Decrypting the password with a custom key](#decrypting_password_custom_key)
6. [Configuration file](#configuration_file)
7. [Log configuration file](#log_configuration_file)
8. [Starting the server](#starting_server)
    1. [Starting the server with the default key to decrypt the passwords](#starting_server_default_key)
    2. [Starting the server with a custom key to decrypt the passwords](#starting_server_custom_key)
9. [Stopping the server](#stopping_server)



## 1. Getting Help <a name="getting_help"/>

```
malapert@heulet-HP-ZBook-15-G4:~/DOI$ java -Dlog4j.configurationFile=./log4j2.xml -jar DOI-server.jar -h

------------ Help for DOI Server -----------

Usage: java -jar DOI-server.jar [--secret <key>] [OPTIONS] [-s]


with :
  --secret <key>               : The 16 bits secret key to crypt/decrypt
  --key-sign-secret <key>      : The key to sign the token
                                 If not provided, a default one is used
  -s|--start                   : Starts the server
  -t|--stop                    : Stops the server
  -l|--status                  : Status of the server
with OPTIONS:
  -h|--help                    : This output
  -k|--key-sign                : Creates a key to sign JWT token
  -c <string>                  : Crypts a string in the standard output
  -e <string>                  : Decrypts a string in the standard output
  -d                           : Displays the configuration file
  -f <path>                    : Loads the configuation file
  -y|--cryptProperties <path>  : crypts the properties file on the output standard
  -z|--decryptProperties <path>: Decrypts the properties on the output standard
  -v|--version                 : DOI server version

```

## 2. Getting version <a name="getting_version"/>

```
malapert@heulet-HP-ZBook-15-G4:~/DOI$ java -Dlog4j.configurationFile=./log4j2.xml -jar DOI-server.jar -v
DOI-server (Copyright 2017-2019 CNES) - Version:1.0.0
```
## 3. Creating the configuration file <a name="creating_conf_file"/>
```
java -Dlog4j.configurationFile=server/target/log4j2.xml -jar server/target/DOI-server-1.0.0.jar -d > doi.conf
```

## 4. Encrypting a password <a name="encrypting_password"/>

### 4.1 Encrypting the password with the default key <a name="encrypting_password_default_key"/>
```
malapert@heulet-HP-ZBook-15-G4:~/DOI$ java -Dlog4j.configurationFile=./log4j2.xml -jar DOI-server.jar -c test
7nAsnRnwzGL+v/SsnQ4rXg==
```

### 4.2 Encrypting the password with a custom key <a name="encrypting_password_custom_key"/>
```
malapert@heulet-HP-ZBook-15-G4:~/DOI$ java -Dlog4j.configurationFile=./log4j2.xml -jar DOI-server.jar --secret wxcvbnqsdfg12345 -c test 
giXEr40f5832YFYgAiWMRA==
```

## 5. Decrypting a password <a name="decrypting_password"/>

### 5.1 Decrypting the password with the default key <a name="decrypting_password_default_key"/>
```
malapert@heulet-HP-ZBook-15-G4:~/DOI$ java -Dlog4j.configurationFile=./log4j2.xml -jar DOI-server.jar -e 7nAsnRnwzGL+v/SsnQ4rXg==
test
```

### 5.2 Decrypting the password with a custom key <a name="decrypting_password_custom_key"/>
```
malapert@heulet-HP-ZBook-15-G4:~/DOI$ java -Dlog4j.configurationFile=./log4j2.xml -jar DOI-server.jar --secret wxcvbnqsdfg12345 -e giXEr40f5832YFYgAiWMRA==
test
```

## 6. Configuration file <a name="configuration_file"/>
See [configuration](./configuration.html)

## 7. Log configuration file <a name="log_configuration_file"/>
```
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="INFO">
    <properties>
            <property name="servicename">doiserver</property>
            <property name="patternPerfo">%-5p | %d{yyyy-MM-dd HH:mm:ss} | [%t] %C{2} (%F:%L) - %m%n</property>
            <property name="patternFile">%-5level %d{yyyy-MM-dd HH:mm:ss} %C{2} (%F:%L) - %m%n</property> 
            <property name="patternApi">[%-5p - %t] %d %c - %m%n</property>     
            <property name="patternSecurity">%d{yyyy-MM-dd HH:mm:ss} %C{2} (%F:%L) - %m%n</property>                                                                             
    </properties>
    
    <Appenders>
        <Syslog name="syslog" host="localhost" port="514" protocol="UDP" appName="${servicename}">
        </Syslog>        
        <RollingFile name="RollingFilePerfo" fileName="logs/doi-perfo.log"
                         filePattern="logs/$${date:yyyy-MM}/perfo-%d{yyyy-MM-dd}-%i.log.gz">
                <PatternLayout>
                        <pattern>${patternPerfo}</pattern>
                </PatternLayout>
                <Policies>
                        <SizeBasedTriggeringPolicy size="100 MB"/> <!-- Or every 100 MB -->
                </Policies>
        </RollingFile>    
        <RollingFile name="RollingFileFile" fileName="logs/doi-file.log"
                         filePattern="logs/$${date:yyyy-MM}/file-%d{yyyy-MM-dd}-%i.log.gz">
                <PatternLayout>
                        <pattern>${patternFile}</pattern>
                </PatternLayout>
                <Policies>
                        <SizeBasedTriggeringPolicy size="100 MB"/> <!-- Or every 100 MB -->
                </Policies>
        </RollingFile>      
        <RollingFile name="RollingFileApi" fileName="logs/doi-api.log"
                         filePattern="logs/$${date:yyyy-MM}/api-%d{yyyy-MM-dd}-%i.log.gz">
                <PatternLayout>
                        <pattern>${patternApi}</pattern>
                </PatternLayout>
                <Policies>
                        <SizeBasedTriggeringPolicy size="100 MB"/> <!-- Or every 100 MB -->
                </Policies>
        </RollingFile>  
        <RollingFile name="RollingFileSecurity" fileName="logs/doi-security.log"
                         filePattern="logs/$${date:yyyy-MM}/security-%d{yyyy-MM-dd}-%i.log.gz">
                <PatternLayout>
                        <pattern>${patternSecurity}</pattern>
                </PatternLayout>
                <Policies>
                        <SizeBasedTriggeringPolicy size="100 MB"/> <!-- Or every 100 MB -->
                </Policies>
        </RollingFile>                    
        <Console name="CONSOLE" target="SYSTEM_OUT">
            <PatternLayout pattern="%highlight{%d [%t] %-5level}: %msg%n%throwable"/><!--%highlight{%d [%t] %-5level}: %msg%n%throwable-->
        </Console>
        <Console name="SHELL" target="SYSTEM_OUT">
            <PatternLayout pattern="%msg%n"/>
        </Console>        
    </Appenders>

    <Loggers>
        <Logger name="fr.cnes.doi.logging.security" level="INFO">
            <AppenderRef ref="RollingFileSecurity"/>
            <appender-ref ref="syslog"/>
        </Logger>
        <Logger name="fr.cnes.doi.logging.app" level="INFO">
            <AppenderRef ref="RollingFilePerfo"/>                        
        </Logger>        
        <Logger name="fr.cnes.doi.logging.api" level="INFO">
            <AppenderRef ref="RollingFileApi"/>
            <appender-ref ref="syslog"/>                        
        </Logger>
        <Logger name="fr.cnes.doi.application" level="INFO">
            <AppenderRef ref="RollingFileFile"/>            
        </Logger>      
        <Logger name="fr.cnes.doi.logging.shell" level="INFO">
             <AppenderRef ref="SHELL"/>
        </Logger>          
        <Logger name="fr.cnes.doi.server" level="INFO">
             <AppenderRef ref="FILE"/>
        </Logger>  
                          
        <Root level="INFO">
            <AppenderRef ref="FILE"/>            
        </Root>
    </Loggers>

</Configuration>
```

## 8. Starting the server <a name="starting_server"/>
### 8.1 Starting the server with the default key to decrypt the passwords <a name="starting_server_default_key"/>
```
java -Dlog4j.configurationFile=./log4j2.xml -jar DOI-server.jar -f config.properties --start
```

### 8.2 Starting the server with a custom key to decrypt the passwords <a name="starting_server_custom_key"/>
```
java -Dlog4j.configurationFile=./log4j2.xml -jar DOI-server.jar --secret wxcvbnqsdfg12345 -f config.properties --start
```

## 9. Stopping the server <a name="stopping_server"/>
```
java -Dlog4j.configurationFile=./log4j2.xml -jar DOI-server.jar -f config.properties --stop
```

