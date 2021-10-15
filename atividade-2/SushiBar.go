package main

import (
	"bufio"
	"os"
	"strconv"
)

type ISushiBar interface {
	chegou()
	saiu()
}

type SushiBar chan struct {}

func NewSushiBar(n int32) *SushiBar {
	sb := SushiBar(make(chan struct{}, n))

	return &sb
}

func (sb *SushiBar) saiu() {
	<- *sb
	println("Saiu 1 cara")
}

func (sb *SushiBar) chegou() {
	println("Chegou 1 cara e ta esperando")
	*sb <- struct {}{}
	println("Chegou 1 cara e entrou")
	sb.saiu()
}

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	sb := NewSushiBar(5)

	for true {
		println("Enter the number of people to arrive or press q to quit:")
		scanner.Scan()
		usrText := scanner.Text()
		if usrText == "q" { os.Exit(0) }

		pessoas, _ := strconv.Atoi(usrText)

		for i := 0; i < pessoas; i++ {
			go sb.chegou()
		}
	}
}
