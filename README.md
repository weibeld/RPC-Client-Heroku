# Heroku RabbitMQ Synchronous Client

Synchronous (RPC-like) communication with [RabbitMQ](http://www.rabbitmq.com/).

This is a client making a synchronous request to a server. That is, after sending the request, the client blocks until it receives the response.

The request sent by the client is a string, and the response returned by the server is also a string.

The synchronous communication is implemented with RabbitMQ's remote procedure call (RPC) facilities. 

The corresponding synchronous server can be found [here](https://github.com/weibeld/RPC-Server-Heroku).

## Related Example

Note that although this code uses RabbitMQ's RPC mechanism, it does not implement a true RPC (calling a remote method) communication.

A true RPC implementation with RabbitMQ using JSON-RPC can be found [here (client)](https://github.com/weibeld/JSON-RPC-Client-Heroku) and [here (server)](https://github.com/weibeld/JSON-RPC-Server-Heroku).

## Implementation

This implementation uses:

- [RabbitMQ](http://www.rabbitmq.com/): message passing service (message broker) implementing the [AMQP](https://www.amqp.org/) protocol
- [RabbitMQ Java Client Library](http://www.rabbitmq.com/java-client.html): Java APIs for RabbitMQ
- [`RpcClient`](http://www.rabbitmq.com/releases/rabbitmq-java-client/current-javadoc/com/rabbitmq/client/RpcServer.html): class of the RabbitMQ Java Client Library for implementing RPC clients
- [Heroku](http://heroku.com): Platform as a Service (PaaS) provider for running any apps in the cloud
- [CloudAMQP](https://elements.heroku.com/addons/cloudamqp): Heroku add-on providing "RabbitMQ as a Service" for Heroku apps


## Run on Heroku

### Create Heroku App

Create an app on Heroku for your client:

~~~bash
heroku create YOUR-APP-NAME
~~~

### Set Up RabbitMQ

Install the CloudAMQP add-on for this Heroku application:

~~~bash
heroku addons:create cloudamqp
~~~

This creates an additional Heroku dyno running a **RabbitMQ server** for your application on Heroku.

In addition, it adds the following config vars to the client Heroku application:

- `CLOUDAMQP_APIKEY`
- `CLOUDAMQP_URL`

You can confirm this with `heroku config`.

The value of the `CLOUDAMQP_URL` variable is the URI of the RabbitMQ server that has just been created on Heroku. Your application needs this URI in order to connect to the RabbitMQ server.

**Important:** you have to execute the above command **only once** for the client/server pair. If you already ran this for the server, then **do not** run it again for the client. Instead, just add the above config vars to the client application:

~~~bash
heroku config:set CLOUDAMQP_APIKEY="..."
heroku config:set CLOUDAMQP_URL="..."
~~~

### Run

For running the client for the first time (and after every source code edit), you have to deploy to Heroku as usual:

~~~bash
git push heroku master
~~~

After this has been done once, you can run the client on a [one-off dyno](https://devcenter.heroku.com/articles/one-off-dynos). This is faster than with the above approach, because it doesn't rebuild the application:

~~~bash
heroku run "java -jar build/libs/client-all.jar"
~~~

The `java -jar build/libs/client-all.jar` command is the same command as in the Procfile.

#### Client Crashed?

If you deploy the client with `git push heroku master`, and then inspect the logs with `heroku logs`, you might see a line like:

~~~
Process exited with status 0
~~~

And after that a line like:

~~~
State changed from up to crashed
~~~

And then the process is started again, and the above repeats.

This is not actually a crash, but it's because our client terminates (on purpose) as soon as it receives a response from the server.

Heroku processes are actually not intended to terminate while the dyno is running. Instead, they are supposed to run as long as the dyno is running.

Thus, when our client process terminates, Heroku interprets this as a crash (even if the exit status is 0), and it applies its [dyno crash restart policy](https://devcenter.heroku.com/articles/dynos#dyno-crash-restart-policy).

With this policy, after the first "crash", the process is restarted immediately one more time. Then, if the process terminates again, it is repeatedly restarted after a certain amount of offset time.

This explains the behaviour that you see in the logs, if you deploy the client with `git push heroku master`.

If you start the client on a one-off dyno (as explained above), there are no such "crashes". This is because the one-off dyno just terminates when the process with which it was started terminates.

Note that in a real-world application the client would also be a non-terminating application, and thus the above wouldn't be an issue.

### Monitor

To see the queues and their content on the RabbitMQ server, use the **CloudAMQP Dashboard**:

~~~bash
heroku addons:open cloudamqp
~~~

Note that this command only works from the application (client or server) on which you *installed* the CloudAMQP add-on (i.e. the one in which you executed `heroku addons:create cloudamqp`).

### Order of Execution

The client is a one-shot application. It makes one request to the server, waits for the response, and then terminates. 

Thus, the normal order of execution is to first start the [server](https://github.com/weibeld/RPC-Server-Heroku), and then the client. In this case, the request sent by the client is handled immediately by the server.

However, starting the client before the server is running is also possible. In this case, there are two possibilities of what can happen:

- If the request queue already exists (if the server has been running before at some time), the message sent by the client is stored in this queue until the server starts up. When this happens, the message is delivered to the server and handled by it.
- If the request queue does not exist, then the message sent by the client is simply discarded. When the server starts up, it doesn't receive this message, because it has not been saved in the request queue. Consequently, the client will never receive a response for this message.


### Tip

If no messages seem to be sent at all, make sure that there's actually a dyno scaled for both the client and server:

~~~bash
heroku ps
~~~~

Scale one dyno for the client:

~~~bash
heroku ps:scale client=1
~~~

## Run Locally

During development, it is convenient to run the application locally instead of deploying it to Heroku:

However, for this to work, you need to install a RabbitMQ server on your machine.

### Install RabbitMQ Server

Install the RabbitMQ server on your local machine according to the instructions [here](http://www.rabbitmq.com/download.html).

This provides the command `rabbitmq-server` for starting the RabbitMQ server on the default port 5672

If you installed with Homebrew, you might need to add the folder containing the RabbitMQ executables to the `PATH`.

### Run

First, make sure the RabbitMQ server is running on the local machine with `rabbitmq-server`.

Then, start the client application:

~~~bash
heroku local
~~~~

### Monitor

See all queues and their content of the local RabbitMQ server in the [Management Web UI](http://www.rabbitmq.com/management.html) here (username: **guest**, password: **guest**): <http://localhost:15672> .

You can also list all the queues from the command line:

~~~bash
sudo rabbitmqctl list_queues
~~~
