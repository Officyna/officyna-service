module "eks" {
  source = "./eks"
  vpc_id = module.db.vpc_id
}

module "db" {
  source = "./db"
}