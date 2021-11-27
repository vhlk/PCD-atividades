package main

import (
	"context"
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
	log.Printf("Received: %d + %d", in.GetFirst(), in.GetSecond())
	return &pb.Response{Result: in.GetFirst() + in.GetSecond()}, nil
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
