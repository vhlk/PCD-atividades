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
	ongoingProduction bool
}

func (pcond *PCond) consumir(id string) int {
	defer pcond.condCons.L.Unlock()

	pcond.condCons.L.Lock()

	// verificamos se a chegamos no limite de consumidores possíveis (capacity)
	for len(pcond.queue) == pcond.capacity {
		println("A fila está cheia, não recebendo novos consumidores!")
		pcond.condProd.Broadcast()
		return -1
	}

	// vamos inserir o consumidor na fila e avisar ao produtor
	pcond.queue = append(pcond.queue, id)
	fmt.Printf("O consumidor %s está esperando a produção\n", id)
	pcond.condProd.Broadcast()
	pcond.condCons.Wait()

	fmt.Printf("O consumidor %s consumiu %d\n", id, pcond.produzido)

	// como pedido na aula, removemos o consumidor na medida que for sendo consumido (1 por vez)
	for i := 0; i < len(pcond.queue); i++ {
		if pcond.queue[i] == id {
			pcond.queue = RemoveIndex(pcond.queue, i)
			break
		}
	}

	// se o último tiver sido consumido, acabou a produção
	if len(pcond.queue) == 0 {
		pcond.ongoingProduction = false
	}

	return pcond.produzido
}

func (pcond *PCond) produzir(n int) {
	defer pcond.condProd.L.Unlock()

	pcond.condProd.L.Lock()

	// verificar se já tem um valor produzido
	if pcond.ongoingProduction {
		println("O produtor já está cheio, tente novamente mais tarde...")
		return
	}

	pcond.ongoingProduction = true

	// se não tiver consumidores, esperamos
	if len(pcond.queue) == 0 {
		fmt.Printf("O produtor com valor %d está bloqueado\n", n)
		pcond.condProd.Wait()
	}

	// produzimos n e avisamos ao consumidor
	pcond.produzido = n
	pcond.condCons.Broadcast()
}

func main() {
	muCons := sync.Mutex{}
	condCons := sync.NewCond(&muCons)
	muProd := sync.Mutex{}
	condProd := sync.NewCond(&muProd)
	var myQueue []string
	PC := PCond{condCons, condProd, 10, myQueue, -1, false}

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

func RemoveIndex(s []string, index int) []string {
	return append(s[:index], s[index+1:]...)
}