package main

import (
	"context"
	"errors"
	"log"
	"net"

	pb "com.pcd/proto"
	"google.golang.org/grpc"
)

const (
	port = ":8888"
)

// server is used to implement helloworld.CalcSocketServer.
type server struct {
	pb.UnimplementedCalcSocketServer
}

// Calc implements helloworld.GreeterServer
func (s *server) Calc(ctx context.Context, in *pb.NumbersRequest) (*pb.Response, error) {
	if in.GetOperation() == "+" {
		return &pb.Response{Result: in.GetFirst() + in.GetSecond()}, nil
	} else if in.GetOperation() == "-" {
		return &pb.Response{Result: in.GetFirst() - in.GetSecond()}, nil
	} else if in.GetOperation() == "*" {
		return &pb.Response{Result: in.GetFirst() * in.GetSecond()}, nil
	} else if in.GetOperation() == "/" {
		if in.GetSecond() == 0 {
			return &pb.Response{}, errors.New("0 division")
		}
		return &pb.Response{Result: in.GetFirst() / in.GetSecond()}, nil
	}


	return &pb.Response{}, errors.New("operation not found")
}

func main() {
	lis, err := net.Listen("tcp", port)
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	s := grpc.NewServer()
	pb.RegisterCalcSocketServer(s, &server{})
	log.Printf("server listening at %v", lis.Addr())
	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}
