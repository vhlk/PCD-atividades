package main

import "fmt"

func left(bridge *chan int, carNumber int) {
	fmt.Printf("ARRIVE: Car %d is waiting your time at L side\n", carNumber)
	_ = <- *bridge
	fmt.Printf("CROSSING: Car %d is crossing the bridge starting from L side\n", carNumber)

	*bridge <- 1

	fmt.Printf("FINISHED: Car %d finished to cross the bridge starting from L side\n", carNumber)
}

func right(bridge *chan int, carNumber int) {
	fmt.Printf("ARRIVE: Car %d is waiting your time at R side\n", carNumber)
	_ = <- *bridge
	fmt.Printf("CROSSING: Car %d is crossing the bridge starting from R side\n", carNumber)

	*bridge <- 1
	fmt.Printf("FINISHED: Car %d finished to cross the bridge starting from R side\n", carNumber)
}

func main() {
	bridge := make(chan int, 1)
	bridge <- 1

	println("Enter the number of car on the right side of the bridge:")
	var carrosDireita int
	_, _ = fmt.Scan(&carrosDireita)

	println("Enter the number of car on the left side of the bridge:")
	var carrosEsquerda int
	_, _ = fmt.Scan(&carrosEsquerda)

	for i := 0; i < carrosDireita; i++ {
		go right(&bridge, i)
	}

	for i := 0; i < carrosEsquerda; i++ {
		go left(&bridge, i)
	}

	_,_ = fmt.Scanln()
}
