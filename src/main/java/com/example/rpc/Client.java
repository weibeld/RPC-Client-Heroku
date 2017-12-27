package com.example.rpc;

import com.rabbitmq.client.RpcClient;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.google.gson.Gson;

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

        // Prepare request
        Gson gson = new Gson();
        RequestObj reqObj = new RequestObj(2, 5);
        System.out.println(" [x] Sending request object: " + reqObj);
        String reqJson = gson.toJson(reqObj);
        System.out.println(" [.] Serialized to JSON: " + reqJson);

        // Do RPC call
        RpcClient.Response replyEnvelope = rpcClient.doCall(null, reqJson.getBytes("UTF-8"));
        String replyJson = new String(replyEnvelope.getBody(), "UTF-8");

        // Read response
        System.out.println(" [.] Got reply JSON: " + replyJson);
        ReplyObj replyObj = gson.fromJson(replyJson, ReplyObj.class);
        System.out.println(" [.] Deserialized to object: " + replyObj);

        // Close connection
        rpcClient.close();
        channel.close();
        connection.close();
    }

}
