include "shared.thrift"

namespace java sockets.server.core

enum Operation {
  GET = 1,
  LIST = 2,
  ADD = 3,
  UPDATE = 4,
  DELETE = 5,
  UPDATE_VERSION = 6,
  DELETE_VERSION = 7
}

struct Work {
  1: string path,
  2: i32 conteudo_length,
  3: Operation op,
  4: optional binary conteudo
}

enum RetornoStatus {
  OK = 200,
  NO_CONTENT = 204,
  NOT_FOUND = 404,
  INTERNAL_ERROR = 500
}

struct Retorno {
  1: RetornoStatus status,
  2: i64 versao,
  3: i64 criado,
  4: i64 modificado,
  5: optional binary conteudo
}

exception InvalidOperation {
  1: i32 whatOp,
  2: string why
}

service RequestProcessor extends shared.SharedService {

   void ping(),

   i32 add(1:i32 num1, 2:i32 num2),

   Retorno calculate(1:i32 logid, 2:Work w) throws (1:InvalidOperation ouch),

   oneway void zip()

}
