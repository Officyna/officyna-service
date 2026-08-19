module "eks" {
  source = "./eks"
}

module "db" {
  source = "./db"

  vpc_id = module.eks.vpc_id
}