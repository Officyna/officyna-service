# Cria Internet Gateway para a api da officyna
resource "aws_internet_gateway" "igw_api" {
  vpc_id = var.vpc_id
}

# Cria Route Table para a api da officyna
resource "aws_route_table" "route_table_public_api" {
  vpc_id = var.vpc_id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw_api.id
  }
  tags = var.tags

}

# Cria associação da Route Table para a api da officyna
resource "aws_route_table_association" "route_association" {
  count          = 2
  subnet_id      = var.subnet_ids[count.index]
  route_table_id = aws_route_table.route_table_public_api.id
}