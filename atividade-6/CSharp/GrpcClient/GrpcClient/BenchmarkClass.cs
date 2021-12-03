using System.Diagnostics;
using Grpc.Net.Client;
using static GrpcClient.CalcSocket;

namespace GrpcClient
{
    public class BenchmarkClass
    {
        private readonly GrpcChannel grpcChannel;
        private readonly CalcSocketClient client;
        private readonly List<long> results;

        public BenchmarkClass()
        {
            grpcChannel = GrpcChannel.ForAddress("https://localhost:5001");
            client = new CalcSocketClient(grpcChannel);
            results = new List<long>();
        }

        public void Run(int timesToRun)
        {
            for (int i = 0; i < timesToRun; i++)
            {
                var time = Stopwatch.StartNew();

                for (int j = 0; j < 10000; j++)
                {
                    var firstNumber = 1;
                    var secondNumber = 1;
                    var operation = "+";

                    client.Calc(new NumbersRequest()
                    {
                        First = firstNumber,
                        Second = secondNumber,
                        Operation = operation
                    });
                }

                time.Stop();

                var timeInNanoseconds = time.ElapsedTicks * 100;
                results.Add(timeInNanoseconds);
            }


            for (int i = 0; i < results.Count; i++)
            {
                Console.WriteLine($"Execution {i+1} result: { results[i] }");
            }

            var average = results.Sum() / results.Count;
            Console.WriteLine($"Average: {average}");
        }
    }
}
