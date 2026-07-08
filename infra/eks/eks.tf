# Cria Cluster para a api da officyna
resource "aws_eks_cluster" "cluster_api" {
  name = "eks-${var.project_name}"

  access_config {
    authentication_mode = "API"
  }

  role_arn = aws_iam_role.cluster.arn
  version  = "1.35"

  vpc_config {
    subnet_ids = var.subnet_ids
  }

  depends_on = [
    aws_iam_role_policy_attachment.cluster_AmazonEKSClusterPolicy,
  ]
}

# Cria Worker Node para a api da officyna
resource "aws_eks_node_group" "node_group" {
  cluster_name    = aws_eks_cluster.cluster_api.name
  node_group_name = "nodeg-${var.project_name}"
  node_role_arn = aws_iam_role.node.arn
  subnet_ids      = var.subnet_ids
  disk_size       = 50
  instance_types  = [var.instance_type]

  scaling_config {
    desired_size = 2
    max_size     = 3
    min_size     = 2
  }

  update_config {
    max_unavailable = 1
  }

  depends_on = [
    aws_iam_role_policy_attachment.node-AmazonEKSWorkerNodePolicy,
    aws_iam_role_policy_attachment.node-AmazonEKS_CNI_Policy,
    aws_iam_role_policy_attachment.node-AmazonEC2ContainerRegistryReadOnly,
  ]
}
