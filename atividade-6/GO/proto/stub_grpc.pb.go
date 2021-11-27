// Code generated by protoc-gen-go-grpc. DO NOT EDIT.

package proto

import (
	context "context"
	grpc "google.golang.org/grpc"
	codes "google.golang.org/grpc/codes"
	status "google.golang.org/grpc/status"
)

// This is a compile-time assertion to ensure that this generated file
// is compatible with the grpc package it is being compiled against.
// Requires gRPC-Go v1.32.0 or later.
const _ = grpc.SupportPackageIsVersion7

// CalcSocketClient is the client API for CalcSocket service.
//
// For semantics around ctx use and closing/ending streaming RPCs, please refer to https://pkg.go.dev/google.golang.org/grpc/?tab=doc#ClientConn.NewStream.
type CalcSocketClient interface {
	// Creates a calculator
	Calc(ctx context.Context, in *NumbersRequest, opts ...grpc.CallOption) (*Response, error)
}

type calcSocketClient struct {
	cc grpc.ClientConnInterface
}

func NewCalcSocketClient(cc grpc.ClientConnInterface) CalcSocketClient {
	return &calcSocketClient{cc}
}

func (c *calcSocketClient) Calc(ctx context.Context, in *NumbersRequest, opts ...grpc.CallOption) (*Response, error) {
	out := new(Response)
	err := c.cc.Invoke(ctx, "/helloworld.CalcSocket/Calc", in, out, opts...)
	if err != nil {
		return nil, err
	}
	return out, nil
}

// CalcSocketServer is the server API for CalcSocket service.
// All implementations must embed UnimplementedCalcSocketServer
// for forward compatibility
type CalcSocketServer interface {
	// Creates a calculator
	Calc(context.Context, *NumbersRequest) (*Response, error)
	mustEmbedUnimplementedCalcSocketServer()
}

// UnimplementedCalcSocketServer must be embedded to have forward compatible implementations.
type UnimplementedCalcSocketServer struct {
}

func (UnimplementedCalcSocketServer) Calc(context.Context, *NumbersRequest) (*Response, error) {
	return nil, status.Errorf(codes.Unimplemented, "method Calc not implemented")
}
func (UnimplementedCalcSocketServer) mustEmbedUnimplementedCalcSocketServer() {}

// UnsafeCalcSocketServer may be embedded to opt out of forward compatibility for this service.
// Use of this interface is not recommended, as added methods to CalcSocketServer will
// result in compilation errors.
type UnsafeCalcSocketServer interface {
	mustEmbedUnimplementedCalcSocketServer()
}

func RegisterCalcSocketServer(s grpc.ServiceRegistrar, srv CalcSocketServer) {
	s.RegisterService(&CalcSocket_ServiceDesc, srv)
}

func _CalcSocket_Calc_Handler(srv interface{}, ctx context.Context, dec func(interface{}) error, interceptor grpc.UnaryServerInterceptor) (interface{}, error) {
	in := new(NumbersRequest)
	if err := dec(in); err != nil {
		return nil, err
	}
	if interceptor == nil {
		return srv.(CalcSocketServer).Calc(ctx, in)
	}
	info := &grpc.UnaryServerInfo{
		Server:     srv,
		FullMethod: "/helloworld.CalcSocket/Calc",
	}
	handler := func(ctx context.Context, req interface{}) (interface{}, error) {
		return srv.(CalcSocketServer).Calc(ctx, req.(*NumbersRequest))
	}
	return interceptor(ctx, in, info, handler)
}

// CalcSocket_ServiceDesc is the grpc.ServiceDesc for CalcSocket service.
// It's only intended for direct use with grpc.RegisterService,
// and not to be introspected or modified (even as a copy)
var CalcSocket_ServiceDesc = grpc.ServiceDesc{
	ServiceName: "helloworld.CalcSocket",
	HandlerType: (*CalcSocketServer)(nil),
	Methods: []grpc.MethodDesc{
		{
			MethodName: "Calc",
			Handler:    _CalcSocket_Calc_Handler,
		},
	},
	Streams:  []grpc.StreamDesc{},
	Metadata: "stub.proto",
}
