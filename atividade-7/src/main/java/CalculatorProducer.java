import com.google.gson.Gson;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class CalculatorProducer {
    public static void main(String[] args) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare("CalculatorRequest", false, false, false, null);
            channel.queueDeclare("CalculatorResponse", false, false, false, null);

            Scanner in = new Scanner(System.in);

            int id = 0;
            while (true) {
                System.out.println("Type the first number or 'q' to quit:");
                String userText = in.nextLine();

                if (userText.equals("q"))
                    System.exit(0);

                double firstNumber = Double.parseDouble(userText);

                System.out.println("Type the operator or 'q' to quit:");
                userText = in.nextLine();

                if (userText.equals("q"))
                    System.exit(0);

                char operator = userText.charAt(0);

                if (operator != '+' && operator != '-' && operator != '*' && operator != '/') {
                    System.out.println("Operador inválido!");
                    continue;
                }

                System.out.println("Type the second number or 'q' to quit:");
                userText = in.nextLine();

                if (userText.equals("q"))
                    System.exit(0);

                double secondNumber = Double.parseDouble(userText);

                CalculatorRequest calculatorRequest = new CalculatorRequest(firstNumber, secondNumber, operator, id);

                String json = new Gson().toJson(calculatorRequest);

                channel.basicPublish("", "CalculatorRequest", false, null, json.getBytes());

                channel.basicConsume("CalculatorResponse", true, (consumerTag, message) -> {
                    CalculatorResponse calculatorResponse = new Gson().fromJson(new String(message.getBody()), CalculatorResponse.class);

                    if (calculatorResponse.msg != null) {
                        System.out.println("Erro: "+calculatorResponse.msg);
                    }
                    else {
                        System.out.println("Resultado: "+calculatorResponse.value);
                    }
                }, consumerTag -> {});
                id++;
            }

        } catch (IOException | TimeoutException e) {
            System.out.println(e.getMessage());
        }


    }
}

class CalculatorRequest {
    public double firstNumber;
    public double secondNumber;
    public char operation;
    public int id;

    public CalculatorRequest(double firstNumber, double secondNumber, char operation, int id) {
        this.firstNumber = firstNumber;
        this.secondNumber = secondNumber;
        this.operation = operation;
        this.id = id;
    }
}


