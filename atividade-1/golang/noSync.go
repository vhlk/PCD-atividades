package main

import "fmt"

func leftNoSync(carNumber int, hasCar *bool) {
	fmt.Printf("ARRIVE: Car %d is waiting your time at L side\n", carNumber)
	fmt.Printf("CROSSING: Car %d is crossing the bridge starting from L side\n", carNumber)
	if *hasCar { fmt.Printf("**************** COLISION DETECTED (%d) ****************\n", carNumber) }
	*hasCar = true
	fmt.Printf("FINISHED: Car %d finished to cross the bridge starting from L side\n", carNumber)
	*hasCar = false
}

func rightNoSync(carNumber int, hasCar *bool) {
	fmt.Printf("ARRIVE: Car %d is waiting your time at R side\n", carNumber)
	fmt.Printf("CROSSING: Car %d is crossing the bridge starting from R side\n", carNumber)
	if *hasCar { fmt.Printf("**************** COLISION DETECTED (%d) ****************\n", carNumber) }
	*hasCar = true
	fmt.Printf("FINISHED: Car %d finished to cross the bridge starting from R side\n", carNumber)
	*hasCar = false
}

func main() {
	println("Enter the number of car on the right side of the bridge:")
	var carrosDireita int
	_, _ = fmt.Scan(&carrosDireita)

	println("Enter the number of car on the left side of the bridge:")
	var carrosEsquerda int
	_, _ = fmt.Scan(&carrosEsquerda)

	var hasCar bool
	hasCar = false

	for i := 0; i < carrosDireita; i++ {
		go rightNoSync(i, &hasCar)
	}

	for i := 0; i < carrosEsquerda; i++ {
		go leftNoSync(i, &hasCar)
	}

	_,_ = fmt.Scanln()
}
