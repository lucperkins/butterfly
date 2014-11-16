import sbtprotobuf.{ProtobufPlugin=>PB}

name := "butterfly"

version := "0.1.0"

seq(PB.protobufSettings: _*)