resource "aws_subnet" "subnet_public" {
  count                   = 3
  vpc_id                  = var.vpc_id
  cidr_block              = cidrsubnet(var.cidr_vpc, 4, count.index)
  map_public_ip_on_launch = true
  availability_zone       = ["us-east-1a", "us-east-1b", "us-east-1c"][count.index]

  tags = var.tags
}