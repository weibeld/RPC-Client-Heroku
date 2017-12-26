# RabbitMQ RPC Client

A simple [RabbitMQ](http://www.rabbitmq.com/) remote procedure call (RPC) client implemented in Java.

RPC uses the **synchronous** communication paradigm. That is, after making a request, the client blocks until it receives the response from the server.

## Implementation

This implementation uses:

- [RabbitMQ](http://www.rabbitmq.com/)
- [RabbitMQ Java Client Library](http://www.rabbitmq.com/java-client.html)
- [`RpcClient`](http://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/RpcClient.html) class

## Prerequisites

Install the RabbitMQ server on your local machine according to the instructions [here](http://www.rabbitmq.com/download.html).

This adds the command `rabbitmq-server` for starting the RabbitMQ server on the default port 5672.

## Run

First, make sure the RabbitMQ server is running on the local machine with `rabbitmq-server`.

Then:

~~~bash
./gradlew run
~~~

To receive any response, the [RPC server](https://github.com/weibeld/RPC-Server) must also be running on the local machine (client and server can be started in either order).

## Idea

RabbitMQ is a message passing service. It allows to pass messages between different processes. These processes can be either on the same machine or on different machines that are connected to the Internet.

All the messages go through the **RabbitMQ server**. This is another process that may run either on the same machine or on a different machine that is connected to the Internet.

For example, you can deploy the RPC client and the RPC server as two independent applications to Heroku (each one with its one version control and probably development team). Then you can deploy the RabbitMQ server to some other machine, and now the RPC client and RPC server can communicate with each other (note that for this specific use-case, using one of the [Heroku RabbitMQ add-ons](https://elements.heroku.com/search/addons?q=rabbitmq) would be more efficient).

## Tips

Show existing queues:

~~~bash
sudo rabbitmqctl list_queues
~~~

Delete a queue:

Use the [Management Web UI](http://www.rabbitmq.com/management.html) on <http://localhost:15672> (username: **guest**, password: **guest**).
