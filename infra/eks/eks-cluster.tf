resource "aws_eks_cluster" "cluster" {
  name = "eks-${var.project_name}"

  access_config {
    authentication_mode = "API"
  }

  role_arn = data.aws_iam_role.cluster.arn
  version  = "1.35"

  vpc_config {
    subnet_ids = aws_subnet.subnet_public[*].id
  }
}