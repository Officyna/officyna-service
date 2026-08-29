output "docdb_endpoint" {
  description = "Endpoint de conexao do cluster DocumentDB"
  value       = aws_docdb_cluster.docdb.endpoint
}

output "docdb_port" {
  description = "Porta do banco de dados"
  value       = aws_docdb_cluster.docdb.port
}

output "vpc_cidr" {
  value = aws_vpc.vpc_fiap.cidr_block
}

output "vpc_id" {
  value = aws_vpc.vpc_fiap.id
}

output "subnet_ids" {
  description = "Lista de subnets públicas criadas na VPC do banco"
  value       = aws_subnet.subnet_public[*].id
}