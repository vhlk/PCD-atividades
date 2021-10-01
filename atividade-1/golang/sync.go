package main

import "fmt"

func crossBridge(bridge *chan int, carNumber int, sideLetter byte) {
	fmt.Printf("ARRIVE: Car %d is waiting your time at %c side\n", carNumber, sideLetter)
	_ = <- *bridge
	fmt.Printf("CROSSING: Car %d is crossing the bridge starting from %c side\n", carNumber, sideLetter)
	*bridge <- 1
	fmt.Printf("FINISHED: Car %d finished to cross the bridge starting from %c side\n", carNumber, sideLetter)
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

	for i := 0; i < carrosDireita; i++ { go crossBridge(&bridge, i, 'R') }

	for i := 0; i < carrosEsquerda; i++ { go crossBridge(&bridge, i, 'L') }

	_,_ = fmt.Scanln()
}