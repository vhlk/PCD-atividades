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

        var numberOfCarsOnBridge = 0;
        var allThreads = new List<Thread>();

        for (var i = 0; i < numberCarsRightSide; i++)
        {
            var carId = i;
            var carThread = new Thread(() => CrossBridge(ref numberOfCarsOnBridge, carId, 'R'));
            allThreads.Add(carThread);
        }

        for (var i = 0; i < numberCarsLeftSide; i++)
        {
            var carId = i;
            var carThread = new Thread(() => CrossBridge(ref numberOfCarsOnBridge, carId, 'L'));
            allThreads.Add(carThread);
        }
        
        allThreads.ForEach(thread => thread.Start());
        Console.ReadLine();
    }

    private static void CrossBridge(ref int numberOfCarsOnBridge, int carId, char side)
    {
        Console.WriteLine("ARRIVE: Car {0} is waiting to cross from {1} side", carId, side);
        numberOfCarsOnBridge++;
        Console.WriteLine("CROSSING: Car {0} is crossing the bridge starting from {1} side", carId, side);
        if (numberOfCarsOnBridge > 1)
        {
            Console.WriteLine("***** COLLISION: Car {0} collided with {1} others cars *****", carId, numberOfCarsOnBridge);
        }
        numberOfCarsOnBridge--;
        Console.WriteLine("FINISHED: Car {0} finished to cross the bridge starting from {1} side", carId, side);
    }
}
