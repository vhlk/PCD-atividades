package main

import "bufio"
import "fmt"
import "os"
import "strconv"

func crossBridge(bridge *chan int, carNumber int, sideLetter byte) {
	fmt.Printf("ARRIVE: Car %d is waiting your time at %c side\n", carNumber, sideLetter)
	_ = <- *bridge
	fmt.Printf("CROSSING: Car %d is crossing the bridge starting from %c side\n", carNumber, sideLetter)
	*bridge <- 1
	fmt.Printf("FINISHED: Car %d finished to cross the bridge starting from %c side\n", carNumber, sideLetter)
}

func main() {
	scanner := bufio.NewScanner(os.Stdin)

	bridge := make(chan int, 1)
	bridge <- 1

	println("Enter the number of car on the right side of the bridge:")
	scanner.Scan()
	carrosDireita, _ := strconv.Atoi(scanner.Text())

	println("Enter the number of car on the left side of the bridge:")
	scanner.Scan()
	carrosEsquerda, _ := strconv.Atoi(scanner.Text())

	for i := 0; i < carrosDireita; i++ { go crossBridge(&bridge, i, 'R') }

	for i := 0; i < carrosEsquerda; i++ { go crossBridge(&bridge, i, 'L') }

	_,_ = fmt.Scanln()
}