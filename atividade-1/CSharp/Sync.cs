using System;
using System.Collections.Generic;
using System.Threading;

class Program
{
    static void Main()
    {
        Console.WriteLine("Enter the number of car on the right side of the bridge");
        var numberCarsRightSide = int.Parse(Console.ReadLine() ?? throw new ArgumentNullException());
        Console.WriteLine("Enter the number of car on the left side of the bridge");
        var numberCarsLeftSide = int.Parse(Console.ReadLine() ?? throw new ArgumentNullException());

        var bridge = new SemaphoreSlim(1, 1);
        var allThreads = new List<Thread>();

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

    private static void CrossBridge(SemaphoreSlim bridge, int position, char side)
    {
        Console.WriteLine("ARRIVE: Car {0} is waiting to cross from {1} side", position, side);
        bridge.Wait();
        Console.WriteLine("CROSSING: Car {0} is crossing the bridge starting from {1} side", position, side);
        bridge.Release();
        Console.WriteLine("FINISHED: Car {0} finished to cross the bridge starting from {1} side", position, side);
    }
}
