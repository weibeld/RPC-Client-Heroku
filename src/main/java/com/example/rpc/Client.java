package com.example.rpc;

import com.rabbitmq.client.RpcClient;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class Client {

    private final static String REQUEST_QUEUE = "rpc_queue";

    public static void main(String[] args) throws Exception {

        // Establish connection to RabbitMQ server
        String uri = System.getenv("CLOUDAMQP_URL");
        if (uri == null) uri = "amqp://guest:guest@localhost";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUri(uri);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Create RPC client
        RpcClient rpcClient = new RpcClient(channel, "", REQUEST_QUEUE);

        // Do RPC call
        String message = "foo";
        System.out.println(" [x] Sending request: " + message);
        RpcClient.Response responseEnvelope = rpcClient.doCall(null, message.getBytes("UTF-8"));
        String responseMsg = new String(responseEnvelope.getBody(), "UTF-8");
        System.out.println(" [.] Got reply: " + responseMsg);

        // Close connection
        rpcClient.close();
        channel.close();
        connection.close();
    }

}
