include "shared.thrift"

namespace java sockets.server.core

enum RetornoStatus {
  OK = 200,
  NO_CONTENT = 204,
  NOT_FOUND = 404,
  INTERNAL_ERROR = 500
}

struct Retorno {
  1: string status,
  2: i64 versao,
  3: i64 criado,
  4: i64 modificado,
  5: i64 content_length,
  6: optional binary conteudo
}

service RequestProcessor {
   void ping(),
   Retorno request(1:string request, 2:binary content),
}
