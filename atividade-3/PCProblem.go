package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
	"sync"
)

type PCond struct {
	condCons *sync.Cond
	condProd *sync.Cond
	capacity int
	queue []string
	produzido int
}

func (pcond *PCond) consumir(id string) int {
	defer pcond.condCons.L.Unlock()

	pcond.condCons.L.Lock()
	pcond.condProd.Broadcast()
	for len(pcond.queue) == pcond.capacity {
		println("A fila está cheia, não recebendo novos consumidores!")
		return -1
	}
	pcond.queue = append(pcond.queue, id)
	fmt.Printf("O consumidor %s está esperando a produção\n", id)
	pcond.condCons.Wait()

	fmt.Printf("O consumidor %s consumiu %d\n", id, pcond.produzido)
	return pcond.produzido
}

func (pcond *PCond) produzir(n int) {
	defer pcond.condProd.L.Unlock()

	pcond.condProd.L.Lock()

	for len(pcond.queue) == 0 {
		fmt.Printf("O produtor com valor %d está bloqueado\n", n)
		pcond.condProd.Wait()
	}
	pcond.produzido = n
	var newQueue []string
	pcond.queue = newQueue
	pcond.condCons.Broadcast()
}

func main() {
	muCons := sync.Mutex{}
	condCons := sync.NewCond(&muCons)
	muProd := sync.Mutex{}
	condProd := sync.NewCond(&muProd)
	var myQueue []string
	PC := PCond{condCons, condProd, 10, myQueue, -1}

	scanner := bufio.NewScanner(os.Stdin)

	id := 0
	for true {
		println("Enter if you want to consume (C), produce (P) or press q to quit:")
		scanner.Scan()
		usrText := scanner.Text()
		if usrText == "q" { os.Exit(0) }

		if strings.ToLower(usrText) == "c" {
			go PC.consumir(strconv.Itoa(id))
			id ++
		} else if strings.ToLower(usrText) == "p" {
			println("Enter the number produced:")
			scanner.Scan()
			produced, _ := strconv.Atoi(scanner.Text())
			go PC.produzir(produced)
		} else {
			println("Comand not recognized")
		}
	}
}