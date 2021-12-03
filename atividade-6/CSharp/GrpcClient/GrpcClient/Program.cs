using Grpc.Net.Client;
using GrpcClient;

using var channel = GrpcChannel.ForAddress("https://localhost:5001");
var client = new CalcSocket.CalcSocketClient(channel);
var validOptions = new List<string>() { "+", "-", "*", "/" };

Console.WriteLine("Welcome to gRPC calculator by Luan, Victor e Vinicius!");

while (true)
{
    try
    {
        Console.WriteLine();
        Console.WriteLine("Type the first number or 'q' to quit:");
        var firstInput = Console.ReadLine();
        if (firstInput == "q")
            break;

        var firstNumber = double.Parse(firstInput);

        Console.WriteLine("Type the the operation you want to do:");
        string operation = Console.ReadLine();

        if (!validOptions.Contains(operation))
        {
            Console.WriteLine($"Operation '{operation}' not suported, try again");
            continue;
        }

        Console.WriteLine("Type the second number:");
        var secondNumber = double.Parse(Console.ReadLine());

        var response = client.Calc(new NumbersRequest()
        {
            First = firstNumber,
            Second = secondNumber,
            Operation = operation
        });

        Console.WriteLine($"Result: {response.Result}");
    }
    catch (Exception e)
    {
        Console.WriteLine(e.Message);
    }
}

//Console.WriteLine("Starting benchmark");
//var benchmarkClass = new BenchmarkClass();
//benchmarkClass.Run(10);


