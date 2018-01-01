# Heroku RabbitMQ RPC Client

A simple [RabbitMQ](http://www.rabbitmq.com/) remote procedure call (RPC) client Heroku application written in Java.

The client makes a request for adding two numbers to its corresponding server, and waits for a response with the result from the server.

RPC uses **synchronous** communication. That is, when the client makes a request to the server, the client blocks until it receives the response from the server.

The server corresponding to this client is the [Heroku RabbitMQ RPC Server](https://github.com/weibeld/RPC-Server-Heroku).

## Implementation

This implementation uses:

- [RabbitMQ](http://www.rabbitmq.com/): message passing service (message broker) implementing the [AMQP](https://www.amqp.org/) protocol
- [RabbitMQ Java Client Library](http://www.rabbitmq.com/java-client.html): Java APIs for RabbitMQ
- [`RpcClient`](http://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/RpcServer.html): convenience class of the RabbitMQ Java Client Library
- [Heroku](http://heroku.com): Platform as a Service (PaaS) provider for running any apps in the cloud
- [CloudAMQP](https://elements.heroku.com/addons/cloudamqp): Heroku add-on providing "RabbitMQ as a Service" for Heroku apps


## Run on Heroku

### Create Heroku App

Create an app on Heroku for your RPC client:

~~~bash
heroku create YOUR-APP-NAME
~~~

### Set Up RabbitMQ

Install the CloudAMQP add-on for this Heroku application:

~~~bash
heroku addons:create cloudamqp
~~~

This creates an additional Heroku dyno running a **RabbitMQ server** for your application on Heroku.

In addition, it adds the following config vars to the RPC server Heroku application:

- `CLOUDAMQP_APIKEY`
- `CLOUDAMQP_URL`

You can confirm this with `heroku config`.

The value of the `CLOUDAMQP_URL` variable is the URI of the RabbitMQ server that has just been created on Heroku. Your application needs this URI in order to connect to the RabbitMQ server.

**Important:** you have to execute the above command **only once** for the client-server pair. If you already ran this for the RPC server, then **do not** run it again for the RPC client. Instead, just add the above config vars to the RPC client application:

~~~bash
heroku config:set CLOUDAMQP_APIKEY="..."
heroku config:set CLOUDAMQP_URL="..."
~~~

### Run

For running the RPC client for the first time (and after every change that you make to the source code), you have to use the following command:

~~~bash
git push heroku master
~~~

However, after this has been done once, you can run the RPC client on a [one-off dyno](https://devcenter.heroku.com/articles/one-off-dynos). This is faster than with the above approach, because it does not re-build the application:

~~~bash
heroku run "java -jar build/libs/rpc-client-all.jar"
~~~

The `java -jar build/libs/rpc-client-all.jar` command is the command that is executed on the on-off dyno. It is the same command as in the `Procfile`.

### Monitor

To see the queues and their content on the RabbitMQ server, use the **CloudAMQP Dashboard**:

~~~bash
heroku addons:open cloudamqp
~~~

Note that this command only works from the application (server or client) on which you *installed* the CloudAMQP add-on (i.e. the one in which you executed `heroku addons:create cloudamqp`).

### Order of Execution

The RPC client is a short-running application. It makes one request to the server, waits for the response, and then terminates. 

Thus, the normal order of execution is to first start the RPC server, and then the RPC client. In this case, the request sent by the RPC client is handled immediately by the RPC server.

However, starting the RPC client before the RPC server is running is also possible. In this case, there are two possibilities of what can happen:

- If the RPC request queue already exists (if the RPC server has been running before at some time), the message sent by the RPC client is stored in this queue until the RPC server starts up. When this happens, the message is delivered to the RPC server and handled by it.
- If the RPC request queue does not exist, then the message sent by the RPC client is simply discarded. When the RPC server starts up, it doesn't receive this message, because it has not been saved in the RPC request queue. Consequently, the RPC client will never receive a response for this message.


### Tip

If no messages seem to be sent at all, make sure that there's actually a dyno scaled for the RPC server and RPC client:

~~~bash
heroku ps
~~~~

Scale one dyno for the RPC client:

~~~bash
heroku ps:scale client=1
~~~

## Run Locally

For development purposes, it is convenient to run the application locally instead of deploying it to Heroku after every change.

### Prerequisites

Install the RabbitMQ server on your local machine according to the instructions [here](http://www.rabbitmq.com/download.html).

This provides the command `rabbitmq-server` for starting the RabbitMQ server on the default port 5672 (if you install with Homebrew, you might need to add the folder containing the executables to the `PATH`).

### Run

First, make sure the RabbitMQ server is running on the local machine with `rabbitmq-server`.

Then, start the RPC client:

~~~bash
heroku local
~~~~

### Monitor

See all queues and their content of the local RabbitMQ server in the [Management Web UI](http://www.rabbitmq.com/management.html) here (username: **guest**, password: **guest**): <http://localhost:15672> .

You can also list all the queues from the command line:

~~~bash
sudo rabbitmqctl list_queues
~~~
