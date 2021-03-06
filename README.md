## Decription
Client-server application, where the Host exposes its screen and plays as a chat server, and the Client is a web application that shows what the server exposes and also allows to send/receive short messages.

## Build

### Server-side

```
mvn clean package
```
### Frontend

No build required

## Usage

### Server-side
```
usage: java -jar golushko-1.0-SNAPSHOT.jar -c <arg>
 -c,--config <arg>   Path to config file
```

### Frontend

Just open ``front/index.html``


## Technology stack
+ **Vertx** - http://vertx.io/ 
+ **Maven** 
+ **Twitter bootstrap**
+ **Jquery**
+ **Apache Maven shade plugin**

## Default Configuration:
```{
 "host" : {
    "port" : 8080,
    "users" : {
        "refresh_period" : 5
    }
 },
 "screenshot" : {
    "update_period" : 5
 }
}
```

### Configuration explained:
**host** - Parent object for all configuration params of main server features

**host.port** - Port to bind

**host.users** - Parent object for all configuration params about users

**host.users.refresh_period** - Timeout for sending a number of users to all clients

**screenshot** - Parent object for all configuration params of screenshot capturing feature

**screenshot.update_period** - Period for updating screenshot(capturing new screenshot)

*all periods are in seconds*