# 1. Cria um Grupo de Subnets para o Banco de Dados
resource "aws_docdb_subnet_group" "default" {
  name       = "officyna-docdb-subnet-group"
  subnet_ids = aws_subnet.subnet_public[*].id

  tags = {
    Name = "officyna-docdb-subnets"
  }
}

# 2. Cria um Grupo de Segurança
resource "aws_security_group" "docdb_sg" {
  name        = "officyna-docdb-sg"
  description = "Permite trafego interno na porta 27017 (MongoDB)"
  vpc_id      = aws_vpc.vpc_fiap.id

  ingress {
    from_port   = 27017
    to_port     = 27017
    protocol    = "tcp"
    cidr_blocks = ["10.0.0.0/16"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# 3. Cria o Cluster do DocumentDB
resource "aws_docdb_cluster" "docdb" {
  cluster_identifier      = "officyna-mongodb-cluster"
  engine                  = "docdb"
  master_username         = var.db_username
  master_password         = var.db_password
  skip_final_snapshot     = true
  db_subnet_group_name    = aws_docdb_subnet_group.default.name
  vpc_security_group_ids  = [aws_security_group.docdb_sg.id]
}

# 4. Cria a Instância do DocumentDB
resource "aws_docdb_cluster_instance" "cluster_instances" {
  count              = 1
  identifier         = "officyna-mongodb-instance-${count.index}"
  cluster_identifier = aws_docdb_cluster.docdb.id
  instance_class     = "db.t3.medium"
}