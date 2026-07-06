# Cria VPC para a api da officyna
resource "aws_vpc" "vpc_api" {
  cidr_block           = var.cidr_vpc
  enable_dns_hostnames = true
  enable_dns_support   = true

  tags = var.tags
}

# Cria as subnets para a api da officyna
resource "aws_subnet" "subnet_public_api" {
  count                   = 3
  vpc_id                  = aws_vpc.vpc_api.id
  cidr_block              = cidrsubnet(aws_vpc.vpc_api.cidr_block, 4, count.index)
  map_public_ip_on_launch = true
  availability_zone       = ["us-east-1a", "us-east-1b", "us-east-1c"][count.index]

  tags = var.tags
}

# Cria Internet Gateway para a api da officyna
resource "aws_internet_gateway" "igw_api" {
  vpc_id = aws_vpc.vpc_api.id
}

# Cria Route Table para a api da officyna
resource "aws_route_table" "route_table_public_api" {
  vpc_id = aws_vpc.vpc_api.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw_api.id
  }
  tags = var.tags

}

# Cria associação da Route Table para a api da officyna
resource "aws_route_table_association" "route_association" {
  count          = 3
  subnet_id      = aws_subnet.subnet_public_api[count.index].id
  route_table_id = aws_route_table.route_table_public_api.id
}