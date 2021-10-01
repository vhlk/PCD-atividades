package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
)

func crossBridgeNoSync(carNumber int, hasCar *bool, sideLetter byte) {
	fmt.Printf("ARRIVE: Car %d is waiting your time at %c side\n", carNumber, sideLetter)
	fmt.Printf("CROSSING: Car %d is crossing the bridge starting from %c side\n", carNumber, sideLetter)
	if *hasCar { fmt.Printf("**************** COLISION DETECTED (%d) ****************\n", carNumber) }
	*hasCar = true
	fmt.Printf("FINISHED: Car %d finished to cross the bridge starting from %c side\n", carNumber, sideLetter)
	*hasCar = false
}

func main() {
	scanner := bufio.NewScanner(os.Stdin)

	println("Enter the number of car on the right side of the bridge:")
	scanner.Scan()
	carrosDireita, _ := strconv.Atoi(scanner.Text())

	println("Enter the number of car on the left side of the bridge:")
	scanner.Scan()
	carrosEsquerda, _ := strconv.Atoi(scanner.Text())

	var hasCar bool
	hasCar = false

	for i := 0; i < carrosDireita; i++ { go crossBridgeNoSync(i, &hasCar, 'R')	}

	for i := 0; i < carrosEsquerda; i++ { go crossBridgeNoSync(i, &hasCar, 'L') }

	_,_ = fmt.Scanln()
}