/*
 *
 * Copyright 2015 gRPC authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

// Package main implements a client for Greeter service.
package main

import (
	"bufio"
	"context"
	"log"
	"os"
	"strconv"
	"time"

	pb "com.pcd/proto"
	"google.golang.org/grpc"
)

const (
	address     = "localhost:8888"
)

func main() {
	// Set up a connection to the server.
	conn, err := grpc.Dial(address, grpc.WithInsecure(), grpc.WithBlock())
	if err != nil {
		log.Fatalf("did not connect: %v", err)
	}
	defer conn.Close()
	c := pb.NewCalcSocketClient(conn)

	scanner := bufio.NewScanner(os.Stdin)

	for {
		println("Type the first number or 'q' to quit:")
		scanner.Scan()
		usrText := scanner.Text()
		if usrText == "q" {
			os.Exit(0)
		}

		firstNumber, _ := strconv.Atoi(usrText)


		println("Type the operation you want to do or 'q' to quit:")
		scanner.Scan()
		operation := scanner.Text()
		if operation == "q" {
			os.Exit(0)
		}

		println("Type the second number or 'q' to quit:")
		scanner.Scan()
		usrText = scanner.Text()
		if usrText == "q" {
			os.Exit(0)
		}

		secondNumber, _ := strconv.Atoi(usrText)

		ctx, cancel := context.WithTimeout(context.Background(), time.Second)
		r, err := c.Calc(ctx, &pb.NumbersRequest{First: int32(firstNumber), Second: int32(secondNumber), Operation: operation})
		if err != nil {
			log.Fatalf("could not greet: %v", err)
		}
		log.Printf("Result: %d", r.GetResult())

		cancel()
	}
}
