# Cria Internet Gateway para a api da officyna
resource "aws_internet_gateway" "igw_api" {
  vpc_id = aws_vpc.vpc_fiap.id
}