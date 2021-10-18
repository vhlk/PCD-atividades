package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"time"
)

type ISushiBar interface {
	chegou()
	saiu()
}

type SushiBar chan struct {}

func NewSushiBar(n int) *SushiBar {
	sb := SushiBar(make(chan struct{}, n))

	return &sb
}

func (sb *SushiBar) saiu(id int) {
	time.Sleep(10)
	<- *sb
	fmt.Printf("O %d saiu\n", id)
}

func (sb *SushiBar) chegou(id int) {
	fmt.Printf("Chegou o %d e ta esperando\n", id)
	*sb <- struct {}{}
	fmt.Printf("O %d entrou\n", id)
	sb.saiu(id)
}

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	sb := NewSushiBar(5)

	id := 0
	for true {
		println("Enter the number of people to arrive or press q to quit:")
		scanner.Scan()
		usrText := scanner.Text()
		if usrText == "q" { os.Exit(0) }

		pessoas, _ := strconv.Atoi(usrText)

		for i := 0; i < pessoas; i++ {
			go sb.chegou(id)
			id ++
		}
	}
}
