# data "aws_iam_role" "cluster" {
#   name = "c221562a5587885l16219350t1w018491-LabEksClusterRole-zDmhLOVJdCYK"
# }
#
# data "aws_iam_role" "node" {
#   name = "c221562a5587885l16219350t1w018491720-LabEksNodeRole-j0I4XEGEPFW6"
# }

data "aws_iam_user" "principal_user" {
  user_name = "admin"
}