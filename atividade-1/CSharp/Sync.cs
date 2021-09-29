using System;
using System.Collections.Generic;
using System.Threading;

class Bridge : SemaphoreSlim
{
    public readonly Dictionary<char, int> FirstQueueCar;
    public Bridge() : base(1, 1)
    {
        FirstQueueCar = new Dictionary<char, int>() {{'L', 0}, {'R', 0}};
    }
}

class Program
{
    static void Main()
    {
        Console.WriteLine("Enter the number of car on the right side of the bridge");
        var numberCarsRightSide = int.Parse(Console.ReadLine() ?? throw new InvalidOperationException());
        Console.WriteLine("Enter the number of car on the left side of the bridge");
        var numberCarsLeftSide = int.Parse(Console.ReadLine() ?? throw new InvalidOperationException());
        
        var bridge = new Bridge();

        List<Thread> allThreads = new List<Thread>();
        
        for (var i = 0; i < numberCarsRightSide; i++)
        {
            var position = i;
            var carThread = new Thread(() => CrossBridge(bridge, position, 'R'));
            allThreads.Add(carThread);
        }
        
        for (var i = 0; i < numberCarsLeftSide; i++)
        {
            var position = i;
            var carThread = new Thread(() => CrossBridge(bridge, position, 'L'));
            allThreads.Add(carThread);
        }

        allThreads.ForEach(thread => thread.Start());
        
        Console.ReadLine();
    }
    
    private static void CrossBridge(Bridge bridge, int position, char side)
    {
        Console.WriteLine("ARRIVE: Car {0} is waiting your time at {1} side", position, side);
        while (bridge.FirstQueueCar[side] <= position)
        {
            if (bridge.FirstQueueCar[side] != position) 
                continue;
            
            Console.WriteLine("READY: Car {0} is ready to cross the bridge at {1} side", position, side);

            bridge.Wait();
            Console.WriteLine("CROSSING: Car {0} is crossing the bridge starting from {1} side", position, side);
            bridge.FirstQueueCar[side] += 1;
            Console.WriteLine("FINISHED: Car {0} finished to cross the bridge starting from {1} side", position, side);
            bridge.Release();
        }
    }
}
