import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.Gson;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

public class CalculatorConsumer {
    private static int sNumClients;
    public static void main(String[] args) {
        sNumClients = Integer.parseInt(args[0]);
        
        System.out.println("Consumer rodando...");
        try {            
            Gson gson = new Gson();
            ConnectionFactory factory = new ConnectionFactory();
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare("CalculatorRequest", false, false, false, null);
            channel.queueDeclare("CalculatorResponse", false, false, false, null);

            channel.basicConsume("CalculatorRequest", true, (consumerTag, message) -> {
                CalculatorRequest calculatorRequest = gson.fromJson(new String(message.getBody()), CalculatorRequest.class);

                channel.basicPublish("", "CalculatorResponse", false, null,
                        gson.toJson(calc(calculatorRequest.firstNumber, calculatorRequest.secondNumber, calculatorRequest.operation, calculatorRequest.id)).getBytes());
            }, consumerTag -> {});

            for (AtomicInteger i = new AtomicInteger(0); i.get() < sNumClients - 1; i.getAndIncrement()) {
                channel.queueDeclare("CalculatorRequest"+i, false, false, false, null);
                channel.queueDeclare("CalculatorResponse"+i, false, false, false, null);

                channel.basicConsume("CalculatorRequest"+i, true, (consumerTag, message) -> {
                    CalculatorRequest calculatorRequest = gson.fromJson(new String(message.getBody()), CalculatorRequest.class);
    
                    channel.basicPublish("", "CalculatorResponse"+i, false, null,
                            gson.toJson(calc(calculatorRequest.firstNumber, calculatorRequest.secondNumber, calculatorRequest.operation, calculatorRequest.id)).getBytes());
                }, consumerTag -> {});
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static CalculatorResponse calc(double firstNumber, double secondNumber, char operator, int id) {
        if (operator == '+')
            return new CalculatorResponse(firstNumber + secondNumber, id, null);
        else if (operator == '-')
           return new CalculatorResponse(firstNumber - secondNumber, id, null);
        else if (operator == '/') {
            if (secondNumber == 0) {
                return new CalculatorResponse(null, id, "Division by 0");
            }
            return new CalculatorResponse(firstNumber / secondNumber, id, null);
        }
        else if (operator == '*')
            return new CalculatorResponse(firstNumber * secondNumber, id, null);
        else {
            return new CalculatorResponse(null, id, "Invalid operation");
        }
    }
}

class CalculatorResponse {
    Double value;
    String msg;
    int id;

    public CalculatorResponse(Double value, int id, String msg) {
        this.value = value;
        this.msg = msg;
        this.id = id;
    }
}
