package main

import (
	"bufio"
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
	cond2Arrive    *sync.Cond
	cond2Leave     *sync.Cond
	condRun        *sync.Cond
}

func (cp *CarPool) load() {
	cp.cond2Arrive.Broadcast()
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

	cp.unload()
}

func (cp *CarPool) unload() {
	cp.cond2Leave.Broadcast()
}

type Passenger struct {
	cp *CarPool
	id string
}

func (pass *Passenger) board() {
	if pass.cp.isRunning {
		println("Carro em movimento, não é possível entrar!")
		return
	}

	pass.cp.cond2Arrive.Wait()

	pass.cp.passengerMutex.Lock()
	pass.cp.passengers = append(pass.cp.passengers, pass)
	pass.cp.passengerMutex.Unlock()

	pass.cp.arrived++

	pass.cp.cond2Arrive.L.Unlock()

	pass.cp.condRun.Broadcast()
}

func (pass *Passenger) unboard() {
	defer pass.cp.cond2Leave.L.Unlock()

	pass.cp.cond2Leave.Wait()

	pass.cp.passengerMutex.Lock()
	pass.cp.passengers = RemoveFromQueue(pass.cp.passengers, pass.id)
	pass.cp.passengerMutex.Unlock()

	pass.cp.left++

	if pass.cp.left == pass.cp.capacity {
		pass.cp.isRunning = false
		pass.cp.left = 0
	}
}

func main() {
	mu2Arrive := sync.Mutex{}
	cond2Arrive := sync.NewCond(&mu2Arrive)
	mu2Leave := sync.Mutex{}
	cond2Leave := sync.NewCond(&mu2Leave)
	muRun := sync.Mutex{}
	condRun := sync.NewCond(&muRun)
	var passengers []*Passenger
	passengersMutex := sync.Mutex{}

	CP := CarPool{10, 0, 0,  false, passengers, &passengersMutex, cond2Arrive, cond2Leave, condRun}
	go CP.run()

	scanner := bufio.NewScanner(os.Stdin)

	id := 0
	for true {
		println("Enter if you want to board (B), unboard (U) or press q to quit:")
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

		} else if strings.ToLower(usrText) == "u" {
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
				for i := 0; i < len(CP.passengers); i++ {
					if CP.passengers[i].id == usrText {
						CP.passengers[i].unboard()
						break
					}
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
