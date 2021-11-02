package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
	"sync"
	"time"
)

type CarPool struct {
	capacity       int
	arrived        int
	left           int
	isRunning      bool
	passengers     []*Passenger
	passengerMutex *sync.Mutex
	mu2Arrive      *sync.Mutex
	mu2Leave       *sync.Mutex
	condRun        *sync.Cond
}

func (cp *CarPool) load() {
	cp.mu2Arrive.Unlock()
}

func (cp *CarPool) run() {
	defer cp.condRun.L.Unlock()

	cp.condRun.L.Lock()

	for cp.arrived < cp.capacity {
		cp.condRun.Wait()
	}

	cp.isRunning = true
	println("Corridinhaaaaa")
	time.Sleep(1 * time.Second)

	cp.mu2Arrive.Lock()
	cp.mu2Leave.Lock()

	cp.arrived = 0

	cp.unload()
}

func (cp *CarPool) unload() {
	cp.mu2Leave.Unlock()
}

type Passenger struct {
	cp *CarPool
	id string
}

func (pass *Passenger) board() {
	if pass.cp.isRunning {
		println("Car is on his way, it is not possible to get in!")
		return
	}

	fmt.Printf("Passenger %s is trying to get in\n", pass.id)
	pass.cp.mu2Arrive.Lock()

	if pass.cp.arrived >= pass.cp.capacity {
		println("The car is full! Entering is not possible")
		pass.cp.mu2Arrive.Unlock()
		return
	}

	pass.cp.passengerMutex.Lock()
	pass.cp.passengers = append(pass.cp.passengers, pass)
	pass.cp.passengerMutex.Unlock()

	pass.cp.arrived++

	fmt.Printf("Passenger %s entered\n", pass.id)

	pass.cp.condRun.Broadcast()

	pass.cp.mu2Arrive.Unlock()
}

func (pass *Passenger) unboard() {
	defer pass.cp.mu2Leave.Unlock()

	fmt.Printf("Passenger %s is trying to leave\n", pass.id)

	pass.cp.mu2Leave.Lock()

	pass.cp.passengerMutex.Lock()
	pass.cp.passengers = RemoveFromQueue(pass.cp.passengers, pass.id)
	pass.cp.passengerMutex.Unlock()

	pass.cp.left++

	fmt.Printf("Passenger %s left\n", pass.id)

	if pass.cp.left == pass.cp.capacity {
		pass.cp.isRunning = false
		pass.cp.left = 0

		pass.cp.load()

		fmt.Println("The last passenger left, accepting new rides...")

		go pass.cp.run()
	}
}

func main() {
	mu2Arrive := sync.Mutex{}
	mu2Leave := sync.Mutex{}
	muRun := sync.Mutex{}
	condRun := sync.NewCond(&muRun)
	var passengers []*Passenger
	passengersMutex := sync.Mutex{}

	CP := CarPool{10, 0, 0, false, passengers, &passengersMutex, &mu2Arrive, &mu2Leave, condRun}
	go CP.run()

	scanner := bufio.NewScanner(os.Stdin)

	id := 0
	for true {
		if len(CP.passengers) > 0 {
			println("Enter if you want to board (B), unboard (U) or press q to quit:")
		} else {
			println("Enter if you want to board (B) or press q to quit:")
		}

		scanner.Scan()
		usrText := scanner.Text()
		if usrText == "q" {
			os.Exit(0)
		}

		if strings.ToLower(usrText) == "b" {
			println("Enter the number of passengers to board")
			scanner.Scan()
			usrText := scanner.Text()
			number, _ := strconv.Atoi(usrText)

			for i := 0; i < number; i++ {
				passenger := Passenger{&CP, strconv.Itoa(id)}

				go passenger.board()

				id++
			}

		} else if len(CP.passengers) > 0 && strings.ToLower(usrText) == "u" {
			println("Enter the id of the passengers to unboard, or A to unboard all")
			println("Current passengers id:")
			for _, passenger := range CP.passengers {
				print(passenger.id + ", ")
			}
			println("")

			scanner.Scan()
			usrText := scanner.Text()

			if strings.ToLower(usrText) == "a" {
				for len(CP.passengers) > 0 {
					CP.passengers[0].unboard()
				}
			} else {
				idFound := false

				for i := 0; i < len(CP.passengers); i++ {
					if CP.passengers[i].id == usrText {
						CP.passengers[i].unboard()
						idFound = true
						break
					}
				}

				if !idFound {
					println("Passenger ID not found, try again...")
				}
			}

		} else {
			println("Command not recognized")
		}
	}
}

func RemoveFromQueue(s []*Passenger, id string) []*Passenger {
	index := 0

	for i := 0; i < len(s); i++ {
		if s[i].id == id {
			index = i
			break
		}
	}

	return append(s[:index], s[index+1:]...)
}
