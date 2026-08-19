resource "aws_route_table" "route_table_public" {
  vpc_id = var.vpc_id

  # since this is exactly the route AWS will create, the route will be adopted
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw_api.id
  }
  tags = var.tags

}

resource "aws_route_table_association" "route_association" {
  count          = 3
  subnet_id      = aws_subnet.subnet_public[count.index].id
  route_table_id = aws_route_table.route_table_public.id
}