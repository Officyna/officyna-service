data "aws_internet_gateway" "igw_api" {
  filter {
    name   = "attachment.vpc-id"
    values = [var.vpc_id]
  }
}

resource "aws_route_table" "route_table_public" {
  vpc_id = var.vpc_id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = data.aws_internet_gateway.igw_api.id
  }

  tags = var.tags
}

resource "aws_route_table_association" "route_association" {
  count = length(var.subnet_ids) > 0 ? length(var.subnet_ids) : length(aws_subnet.subnet_public)

  subnet_id      = length(var.subnet_ids) > 0 ? var.subnet_ids[count.index] : aws_subnet.subnet_public[count.index].id
  route_table_id = aws_route_table.route_table_public.id
}