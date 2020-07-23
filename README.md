# IBM MQ

IBM MQ puede transportar cualquier tipo de data como mensaje. Permite establecer reglas para construir arquitecturas flexibles y reuzables orientadas a servicios. Funciona en amplia gama de plataformas, aplicaciones, web services y protocolos de comunicacion donde hay un completo aseguramiento de la entrega. Proporciona una capa de comunicación para visibilidad y control del flujo de mensajes y data dentro de la organización.

IBM es un middleware de mensajes y encolamiento con diferentes modos de operacion:
- punto a punto
- publish/subscribe
- transferencia de archivos

Aplicaciones pueden publicar mensajes a muchos subscriptores sobre multicast

# Que se puede hacer con IBM MQ
- Envia y recibe data entre aplicaciones
- Desacopla de la aplicacion el envío de mensajes
- Se puede asegurar el envío de mensajes conSSL/TLS
- Con Advance Message Security se puede encriptar y firmar el mensaje


# Conceptos

## Messagin
Los programas se comunican enviando a otro data en mensajes en vez de llamarlos directamente

## Queuing
Mensajes son puestos en colas, asi que programas pueden correr independiente de otro con diferentes velocidades y tiempos en diferentes locaciones y sin tener coneccion directa entre ellos

## Point-to-point
La aplicación envia mensajes a una cola o a una lista de colas. El sender conoce el nombre del destinatario pero no donde está

## Publish/subscribe
La aplicación publica un mensaje en un topico como el resultado de un juego de un equipo. IBM MQ envia copias del mensaje a todas las aplicaciones que se subscriben al topico del resultado. El publicador no conoce los nombres de los subscriptores o donde están y los subscriptores no conocen quien emite el mensaje.

## Multicast
Multicast es una forma eficiente de publish/subscribe que escala a muchos subscriptores. Transfiere el esfuerzo de enviar una copia de una publicacion a cada subscriptor desde IBM MQ a la red. Una vez la ruta para la comunicación es estabelecida entre el publisher y el subscriber ibm MQ no esta involucrado en enviar la información.

## File transfer
Los archivos son transferidos en mensajes. Se puede integrar con otros sistemas de transferencia

## Queue Manager
Queue manager es el componente principal en una red de mensajes IBM MQ .Se pueden tener muchas colas o topicos en un queue manager. Puedes tener mas de un queue mnanager en un computador

## Queue
Son estructuras de datos que son usadas para almacenar mensajes y son objetos de IBM MQ Manager

# Topic
Es usado en el modelo pubish/subscribe. Denota el asunto de la información que es publicada. 

## Channel
Son usados para mover mensajes automaticamente de un queue manager a otro qmanager en una dirección dependiendo la configuración de los qmanagers.

# Prerequisitos
1. Docker instalado
2. IDE de Java
3. Java 11

# Configuración

1. Descargar imagen IBM MQ desde docker

https://hub.docker.com/r/ibmcom/mq/

```console
docker pull ibmcom/mq
```

2. Arrancar el contenedorcon la configuración básica creando el qmanager QM1

```console
docker run \
  --env LICENSE=accept \
  --env MQ_QMGR_NAME=QM1 \
  --publish 1414:1414 \
  --publish 9443:9443 \
  --detach \
  ibmcom/mq
  ```
 3. Abrir la consola e ingresar con las siguientes credenciales:
 
 https://localhost:9443/ibmmq/console/login.html
 
 ```console
 usuario:admin
 password: passw0rd
```

![alt text](https://github.com/giovany79/ibm-mq/blob/master/images/ibm-mq-console.png)

![alt text](https://github.com/giovany79/ibm-mq/blob/master/images/ibmmq-console2.png)


4. Crear una aplicación springboot con el initializer de spring

5. En el archivo **build.gradle**(https://github.com/giovany79/ibm-mq/blob/master/build.gradle). modificarlo para adicionar las siguientes librerias:

### implementation('org.springframework.boot:spring-boot-starter-webflux')
Inicia las configuraciones básicas de springboot con webflux

### implementation('com.ibm.mq:mq-jms-spring-boot-starter:2.2.3')
Realiza las configuraciones básicas para acceder a un qmanager a una cola expecifica.

6. Se debe crear un archivo de propiedades **application.yaml**(https://github.com/giovany79/ibm-mq/blob/master/src/main/resources/application.yaml) con las siguientes propiedades del IBM MQ local

```console
ibm:
  mq:
    queue-manager: QM1
    channel: DEV.ADMIN.SVRCONN
    conn-name: localhost(1414)
    user: admin
    password: passw0rdx
```

7. Se crea la clase **ControllerMQ.java**(https://github.com/giovany79/ibm-mq/blob/master/src/main/java/co/com/gcode/webspheremq/ControllerMQ.java) con los métodos que va a consumir los servicios:

Poner un mensaje en una cola:
```java
 @GetMapping("send")
    public String sendMessage(){
        try{
            jmsTemplate.convertAndSend("DEV.QUEUE.1", "Hello World!");
            return "OK";
        }catch(JmsException ex){
            ex.printStackTrace();
            return "FAIL";
        }
    }

```
Recibir un mensaje de una cola:

```java
    @GetMapping("recv")
    String recv(){
        try{
            return jmsTemplate.receiveAndConvert("DEV.QUEUE.1").toString();
        }catch(JmsException ex){
            ex.printStackTrace();
            return "FAIL";
        }
    }
```
8. Compilar el servicio y correrlo

9. Consumir el servicio para poner el mensaje "hello word"

http://localhost:8080/mq/send

10. Consumir el servicio para leer el mensaje en la cola

http://localhost:8080/mq/recv

11. Revisar el mensaje en la consola administrativa:

https://localhost:9443/ibmmq/console/login.html

![alt text](https://github.com/giovany79/ibm-mq/blob/master/images/ibm-mq-contenidocola.png)

# Posibles errores

## Error de versión mayor (A JNI error has occurred - UnsupportedClassVersionError)

El proyecto esta compilado con Java 11. Para esto debes ir a las propiedades del proyecto y cambiar la version del SDK

### intellij

File > Project Structure > Project Settings > Project > Project SDK

Elejir el SDK de Java 11




# Referencias
https://www.ibm.com/support/knowledgecenter/es/SSFKSJ_8.0.0/com.ibm.mq.pro.doc/q001020_.htm
https://www.ibm.com/support/knowledgecenter/es/SSFKSJ_8.0.0/com.ibm.mq.pro.doc/q004370_.htm
https://developer.ibm.com/technologies/spring/tutorials/mq-jms-application-development-with-spring-boot
