using Grpc.Core;
using Microsoft.Extensions.Logging;
using System;
using System.Threading.Tasks;

namespace GrpcService1
{
    public class CalculatorService : CalcSocket.CalcSocketBase
    {
        private readonly ILogger<CalculatorService> _logger;
        public CalculatorService(ILogger<CalculatorService> logger)
        {
            _logger = logger;
        }

        public override async Task<Response> Calc(NumbersRequest request, ServerCallContext context)
        {
            try
            {
                // _logger.LogInformation("Recebida a requisição de soma dos números {0} e {1}", request.First, request.Second);
          
                if(request.Second == 0 && request.Operation == "/")
                {
                    throw new RpcException(new Status(StatusCode.InvalidArgument, "Não é possível dividir por 0"));
                }

                return new Response()
                {
                    Result = request.Operation switch
                    {
                        "+" => request.First + request.Second,
                        "-" => request.First - request.Second,
                        "*" => request.First * request.Second,
                        "/" => request.First / request.Second,
                        _ => throw new InvalidOperationException($"Operador {request.Operation} não reconhecido")
                    }
                };
            }
            catch (Exception e)
            {
                throw new RpcException(new Status(StatusCode.Internal, $"Error durante recebimento da requisição: {e.Message}"));
            }
        }
    }
}
