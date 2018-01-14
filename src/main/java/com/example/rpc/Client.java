package com.example.rpc;

import com.rabbitmq.client.RpcClient;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Client {

    private final static String QUEUE = "request-queue";

    public static void main(String[] args) throws Exception {

        // Establish connection to RabbitMQ server
        String uri = System.getenv("CLOUDAMQP_URL");
        if (uri == null) uri = "amqp://guest:guest@localhost";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Create RPC client
        RpcClient client = new RpcClient(channel, "", QUEUE);

        // Make request
        String req = "hello";
        System.out.println(" [x] Sending request: \"" + req + "\"");
        RpcClient.Response r = client.doCall(null, req.getBytes("UTF-8"));
        String reply = new String(r.getBody(), "UTF-8");
        System.out.println(" [.] Got reply: \"" + reply + "\"");

        // Close connection
        client.close();
        channel.close();
        connection.close();
    }

}
